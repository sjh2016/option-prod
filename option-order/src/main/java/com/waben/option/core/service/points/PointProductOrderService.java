package com.waben.option.core.service.points;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.waben.option.core.service.settlement.SettlementService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.exception.BusinessErrorConstants;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.activity.ActivityAPI;
import com.waben.option.common.interfaces.activity.AllowanceDetailAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.bean.AccountTransactionBean;
import com.waben.option.common.model.dto.activity.UpdateJoinDTO;
import com.waben.option.common.model.dto.point.PointProductOrderDTO;
import com.waben.option.common.model.dto.point.PointProductOrderUserStaDTO;
import com.waben.option.common.model.dto.resource.LevelIncomeDTO;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.model.enums.ProductOrderStatusEnum;
import com.waben.option.common.model.enums.RunOrderStatusEnum;
import com.waben.option.common.model.enums.TransactionEnum;
import com.waben.option.common.model.request.point.PointAuditOrderRequest;
import com.waben.option.common.model.request.point.PointPlaceOrderRequest;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.core.service.account.AccountService;
import com.waben.option.data.entity.point.PointProduct;
import com.waben.option.data.entity.point.PointProductOrder;
import com.waben.option.data.entity.point.PointRunOrder;
import com.waben.option.data.entity.resource.Config;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.repository.point.PointProductDao;
import com.waben.option.data.repository.point.PointProductOrderDao;
import com.waben.option.data.repository.point.PointRunOrderDao;
import com.waben.option.data.repository.resource.ConfigDao;
import com.waben.option.data.repository.user.UserDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PointProductOrderService {

	@Resource
	private IdWorker idWorker;

	@Resource
	private PointProductOrderDao pointProductOrderDao;

	@Resource
	private PointProductDao pointProductDao;

	@Resource
	private PointRunOrderDao pointRunOrderDao;

	@Resource
	private ConfigDao configDao;

	@Resource
	private UserDao userDao;

	@Resource
	private AccountService accountService;

	@Resource
	private StaticConfig staticConfig;

	@Resource
	private ActivityAPI activityAPI;

	@Resource
	private AllowanceDetailAPI allowanceDetailAPI;

	@Resource
	private ModelMapper modelMapper;

	@Resource
	private SettlementService settlementService;

	public PageInfo<PointProductOrderDTO> page(Long userId, ProductOrderStatusEnum status, int page, int size) {
		QueryWrapper<PointProductOrder> query = new QueryWrapper<>();
		if (userId != null) {
			query.eq(PointProductOrder.USER_ID, userId);
		}
		if (status != null) {
			query.eq(PointProductOrder.STATUS, status);
		}
		query.orderByDesc(PointProductOrder.GMT_CREATE);
		IPage<PointProductOrder> pageWrapper = pointProductOrderDao.selectPage(new Page<>(page, size), query);
		PageInfo<PointProductOrderDTO> pageInfo = new PageInfo<>();
		pageInfo.setRecords(pageWrapper.getRecords().stream()
				.map(temp -> modelMapper.map(temp, PointProductOrderDTO.class)).collect(Collectors.toList()));
		pageInfo.setPage((int) pageWrapper.getPages());
		pageInfo.setSize((int) pageWrapper.getSize());
		pageInfo.setTotal(pageWrapper.getTotal());
		return pageInfo;
	}

	@Transactional
	public void placeOrder(Long userId, boolean registerGift, PointPlaceOrderRequest req) {
		log.info("user {} place order {}, registerGift {}", userId, req.getProductId(), registerGift);
		// step 1 : 验证
		PointProduct product = pointProductDao.selectById(req.getProductId());
		if (product == null || !product.getOnline()) {
			if (registerGift) {
				log.error("user {} register gift product {}, but database product not exist or not online!", userId,
						req.getProductId());
				return;
			}
			throw new ServerException(BusinessErrorConstants.ERROR_PRODUCT_NOTEXIST_OR_UNONLINE);
		}
		if (product.getLimitQuantity().intValue() > 0
				&& product.getUsedQuantity().intValue() >= product.getLimitQuantity().intValue()) {
			throw new ServerException(BusinessErrorConstants.ERROR_PRODUCT_SELLOUT);
		}
		if (registerGift && !product.getGift()) {
			log.error("user {} register gift product {}, but database gift is false!", userId, req.getProductId());
			return;
		}
		if (!registerGift && product.getGift()) {
			throw new ServerException(BusinessErrorConstants.ERROR_PRODUCT_SELLOUT);
		}
		// step 2 : 检查余额
		if (!registerGift) {
			accountService.checkAmount(userId, staticConfig.getDefaultCurrency(), product.getActualAmount());
		}
		// step 3 : 创建订单
		PointProductOrder order = new PointProductOrder();
		order.setId(idWorker.nextId());
		order.setUserId(userId);
		order.setProductId(product.getId());
		order.setProductName(product.getName());
		order.setStarLevel(product.getStarLevel());
		order.setAmount(product.getAmount());
		order.setActualAmount(product.getActualAmount());
		order.setCycle(product.getCycle());
		order.setRunRefreshQuantity(product.getRunQuantity());
		order.setRunTotalQuantity(0);
		order.setRunUsedQuantity(0);
		order.setMinRunPoint(product.getMinRunPoint());
		order.setMaxRunPoint(product.getMaxRunPoint());
		order.setTotalProfit(BigDecimal.ZERO);
		order.setGift(product.getGift());
		order.setStatus(ProductOrderStatusEnum.PENDING);
		pointProductOrderDao.insert(order);
		// step 4 : 扣除余额
		if (!registerGift) {
			List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
			transactionBeanList.add(AccountTransactionBean.builder().userId(userId).type(TransactionEnum.DEBIT_WAGER)
					.amount(order.getActualAmount()).transactionId(order.getId())
					.currency(staticConfig.getDefaultCurrency()).build());
			accountService.transaction(userId, transactionBeanList);
		} else {
			// 修改订单状态
			order.setStatus(ProductOrderStatusEnum.WORKING);
			order.setRunTotalQuantity(1);
			order.setAuditTime(LocalDateTime.now());
			pointProductOrderDao.updateById(order);
			// 注册当天赠送一次兑换机会
			PointRunOrder runOrder = new PointRunOrder();
			runOrder.setId(idWorker.nextId());
			runOrder.setUserId(order.getUserId());
			runOrder.setProductOrderId(order.getId());
			runOrder.setProductId(order.getProductId());
			runOrder.setProductName(order.getProductName());
			runOrder.setStarLevel(order.getStarLevel());
			runOrder.setAmount(order.getAmount());
			runOrder.setMinRunPoint(order.getMinRunPoint());
			runOrder.setMaxRunPoint(order.getMaxRunPoint());
			runOrder.setProfit(BigDecimal.ZERO);
			runOrder.setStatus(RunOrderStatusEnum.PENDING);
			runOrder.setExpireTime(getExpireTime());
			pointRunOrderDao.insert(runOrder);
		}
		// step 5 : 更新产品已购买次数
		if (product.getLimitQuantity().intValue() > 0) {
			product.setUsedQuantity(product.getUsedQuantity() + 1);
			pointProductDao.updateById(product);
		}
		// step 6 : 更新投资活动参与信息
		UpdateJoinDTO updateJoinDTO = new UpdateJoinDTO();
		updateJoinDTO.setUserId(order.getUserId());
		updateJoinDTO.setType(ActivityTypeEnum.INVESTMENT);
		updateJoinDTO.setQuantity(order.getAmount());
		updateJoinDTO.setOrderId(order.getId());
		activityAPI.updateJoin(updateJoinDTO);
		// step 7 : 扣除津贴
		allowanceDetailAPI.distribute(order.getId(), order.getUserId(), order.getCycle(), BigDecimal.ZERO,
				order.getAmount(), 1);
	}

	@Transactional
	public void auditOrder(PointAuditOrderRequest req) {
		log.info("pointProductOrderService auditOrder PointAuditOrderRequest={}", req);
		// 验证
		PointProductOrder order = pointProductOrderDao.selectById(req.getId());
		if (order == null) {
			throw new ServerException(BusinessErrorConstants.ERROR_PRODUCT_ORDER_NOTEXIST);
		}
		if (order.getStatus() != ProductOrderStatusEnum.PENDING) {
			throw new ServerException(BusinessErrorConstants.ERROR_PRODUCT_ORDER_STATUS_NOTMATCH);
		}
		// 处理审核
		if (req.getStatus() == ProductOrderStatusEnum.WORKING) {
			// 修改订单状态
			order.setStatus(ProductOrderStatusEnum.WORKING);
			order.setRunTotalQuantity(order.getRunRefreshQuantity());
			order.setAuditTime(LocalDateTime.now());
			pointProductOrderDao.updateById(order);
			// 生成跑分订单
			int quantity = order.getRunRefreshQuantity();
			if (quantity > 0) {
				for (int i = 0; i < quantity; i++) {
					PointRunOrder runOrder = new PointRunOrder();
					runOrder.setId(idWorker.nextId());
					runOrder.setUserId(order.getUserId());
					runOrder.setProductOrderId(order.getId());
					runOrder.setProductId(order.getProductId());
					runOrder.setProductName(order.getProductName());
					runOrder.setStarLevel(order.getStarLevel());
					runOrder.setAmount(order.getAmount());
					runOrder.setMinRunPoint(order.getMinRunPoint());
					runOrder.setMaxRunPoint(order.getMaxRunPoint());
					runOrder.setProfit(BigDecimal.ZERO);
					runOrder.setStatus(RunOrderStatusEnum.PENDING);
					runOrder.setExpireTime(getExpireTime());
					pointRunOrderDao.insert(runOrder);
				}
			}
			// 给上级投资分成
//			BigDecimal investmentDivideRatio = queryInvestmentDivideRatio();
			User user = userDao.selectById(order.getUserId());
//			if (user != null && user.getIsReal() != null && user.getIsReal() && !user.getParentId().equals(0L)
//					&& investmentDivideRatio.compareTo(BigDecimal.ZERO) > 0 && settlementService.checkSelfTransactionAmount(user.getParentId(),order.getAmount())) {
//				BigDecimal investmentDivide = computeInvestmentDivide(order.getActualAmount(), investmentDivideRatio);
//				List<AccountTransactionBean> accountBeanList = new ArrayList<>();
//				accountBeanList.add(AccountTransactionBean.builder().userId(user.getParentId())
//						.type(TransactionEnum.CREDIT_INVITE_WAGER).amount(investmentDivide).transactionId(order.getId())
//						.currency(staticConfig.getDefaultCurrency()).build());
//				accountService.transaction(user.getParentId(), accountBeanList);
//			}
			// 更新用户星级
			if (user.getStarLevel() == null || user.getStarLevel().intValue() < order.getStarLevel().intValue()) {
				user.setStarLevel(order.getStarLevel());
				userDao.updateById(user);
			}
		} else if (req.getStatus() == ProductOrderStatusEnum.REFUSED) {
			// 修改订单状态
			order.setStatus(ProductOrderStatusEnum.REFUSED);
			order.setAuditTime(LocalDateTime.now());
			pointProductOrderDao.updateById(order);
			// 退回投资金额
			List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
			transactionBeanList.add(AccountTransactionBean.builder().userId(order.getUserId())
					.type(TransactionEnum.CREDIT_WAGER_RETURN).amount(order.getActualAmount())
					.transactionId(order.getId()).currency(staticConfig.getDefaultCurrency()).build());
			accountService.transaction(order.getUserId(), transactionBeanList);
		}
	}

	private LocalDateTime getExpireTime() {
		return LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(0, 0, 0));
	}

	private BigDecimal queryInvestmentDivideRatio() {
		QueryWrapper<Config> query = new QueryWrapper<>();
		query.eq(Config.KEY, "levelIncome");
		Config config = configDao.selectOne(query);
		if (config != null && config.getValue() != null && config.getValue().trim().startsWith("[")
				&& config.getValue().trim().endsWith("]")) {
			try {
				List<LevelIncomeDTO> list = JacksonUtil.decode(config.getValue(), ArrayList.class,
						LevelIncomeDTO.class);
				if (list != null && list.size() > 0) {
					for (LevelIncomeDTO dto : list) {
						if (dto.getLevel() == 1 && dto.getInvestment().compareTo(BigDecimal.ZERO) >= 0) {
							return dto.getInvestment();
						}
					}
				}
			} catch (Exception ex) {
			}
		}
		return BigDecimal.ZERO;
	}

	private BigDecimal computeInvestmentDivide(BigDecimal amount, BigDecimal investmentDivideRatio) {
		return amount.multiply(investmentDivideRatio).setScale(0, RoundingMode.HALF_UP);
	}

	public List<PointProductOrderDTO> userOrderList(Long userId) {
		QueryWrapper<PointProductOrder> query = new QueryWrapper<>();
		query.eq(PointProductOrder.USER_ID, userId);
		query.orderByDesc(PointProductOrder.GMT_CREATE);
		List<PointProductOrder> list = pointProductOrderDao.selectList(query);
		return list.stream().map(temp -> modelMapper.map(temp, PointProductOrderDTO.class))
				.collect(Collectors.toList());
	}

	public PointProductOrderUserStaDTO userSta(Long userId) {
		PointProductOrderUserStaDTO sta = pointProductOrderDao.userSta(userId);
		if (sta == null) {
			sta = new PointProductOrderUserStaDTO();
		}
		if (sta.getSumAmount() == null) {
			sta.setSumAmount(BigDecimal.ZERO);
		}
		if (sta.getSumProfit() == null) {
			sta.setSumProfit(BigDecimal.ZERO);
		}
		return sta;
	}

	@Transactional
	public void generateRunOrderSchedule() {
		QueryWrapper<PointProductOrder> query = new QueryWrapper<>();
		// query.gt(PointProductOrder.PRODUCT_ID, 1);
		query.eq(PointProductOrder.STATUS, ProductOrderStatusEnum.WORKING);
		List<PointProductOrder> list = pointProductOrderDao.selectList(query);
		if (list != null && list.size() > 0) {
			for (PointProductOrder order : list) {
				int quantity = order.getRunRefreshQuantity();
				if (quantity > 0) {
					for (int i = 0; i < quantity; i++) {
						PointRunOrder runOrder = new PointRunOrder();
						runOrder.setId(idWorker.nextId());
						runOrder.setUserId(order.getUserId());
						runOrder.setProductOrderId(order.getId());
						runOrder.setProductId(order.getProductId());
						runOrder.setProductName(order.getProductName());
						runOrder.setStarLevel(order.getStarLevel());
						runOrder.setAmount(order.getAmount());
						runOrder.setMinRunPoint(order.getMinRunPoint());
						runOrder.setMaxRunPoint(order.getMaxRunPoint());
						runOrder.setProfit(BigDecimal.ZERO);
						runOrder.setStatus(RunOrderStatusEnum.PENDING);
						runOrder.setExpireTime(getExpireTime());
						pointRunOrderDao.insert(runOrder);
					}
				}
			}
		}
	}

}
