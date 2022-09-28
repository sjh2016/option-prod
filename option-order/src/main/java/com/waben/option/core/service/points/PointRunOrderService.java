package com.waben.option.core.service.points;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.waben.option.common.amqp.AMQPMessage;
import com.waben.option.common.amqp.message.RunOrderSettlementMessage;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.exception.BusinessErrorConstants;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.point.PointMerchantDTO;
import com.waben.option.common.model.dto.point.PointRunOrderDTO;
import com.waben.option.common.model.dto.resource.LevelIncomeDTO;
import com.waben.option.common.model.enums.RunOrderStatusEnum;
import com.waben.option.common.model.request.point.PointRunRequest;
import com.waben.option.common.model.request.point.PointRunUserOrderRequest;
import com.waben.option.common.service.AMQPService;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.data.entity.point.PointMerchant;
import com.waben.option.data.entity.point.PointProductOrder;
import com.waben.option.data.entity.point.PointRunOrder;
import com.waben.option.data.entity.point.PointRunOrderDynamic;
import com.waben.option.data.entity.resource.Config;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.repository.point.PointMerchantDao;
import com.waben.option.data.repository.point.PointProductOrderDao;
import com.waben.option.data.repository.point.PointRunOrderDao;
import com.waben.option.data.repository.point.PointRunOrderDynamicDao;
import com.waben.option.data.repository.resource.ConfigDao;
import com.waben.option.data.repository.user.UserDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PointRunOrderService {

	@Resource
	private ModelMapper modelMapper;

	@Resource
	private ConfigDao configDao;

	@Resource
	private UserDao userDao;

	@Resource
	private PointRunOrderDao pointRunOrderDao;

	@Resource
	private PointMerchantDao pointMerchantDao;

	@Resource
	private PointProductOrderDao pointProductOrderDao;

	@Resource
	private PointRunOrderDynamicDao pointRunOrderDynamicDao;

	@Resource
	private IdWorker idWorker;

	@Resource
	private AMQPService amqpService;

	private Random random = new Random();

	public void run(Long userId, PointRunRequest req) {
		boolean hasOrderId = false;
		boolean hasMerchantId = false;
		if (req.getRunOrderId() != null && req.getRunOrderId().longValue() > 0) {
			hasOrderId = true;
		}
		if (req.getMerchantId() != null && req.getMerchantId().longValue() > 0) {
			hasMerchantId = true;
		}
		if (!hasOrderId && !hasMerchantId) {
			throw new ServerException(BusinessErrorConstants.ERROR_PARAM_FORMAT);
		} else if (hasOrderId && hasMerchantId) {
			run(userId, req.getRunOrderId(), req.getMerchantId());
		} else if (hasOrderId && !hasMerchantId) {
			// 提供了订单ID，系统匹配商家
			PointMerchantDTO merchant = matchMerchant(userId, req.getRunOrderId());
			run(userId, req.getRunOrderId(), merchant.getId());
		} else if (!hasOrderId && hasMerchantId) {
			// 提供了商家ID，系统匹配订单
			PointRunOrderDTO order = matchOrder(userId, req.getMerchantId());
			run(userId, order.getId(), req.getMerchantId());
		}
	}

	public PointMerchantDTO matchMerchant(Long userId, Long runOrderId) {
		PointRunOrder runOrder = pointRunOrderDao.selectById(runOrderId);
		if (runOrder == null) {
			throw new ServerException(BusinessErrorConstants.ERROR_RUN_ORDER_NOTEXIST);
		}
		if (!userId.equals(runOrder.getUserId())) {
			// 操作用户不匹配
			throw new ServerException(BusinessErrorConstants.ERROR_OPERATE_USER_NOTMATCH);
		}
		QueryWrapper<PointMerchant> query = new QueryWrapper<>();
		query.eq(PointMerchant.ONLINE, true);
		query.le(PointMerchant.MIN_AMOUNT, runOrder.getAmount());
		query.ge(PointMerchant.MAX_AMOUNT, runOrder.getAmount());
		List<PointMerchant> list = pointMerchantDao.selectList(query);
		if (list != null && list.size() > 0) {
			// 排除额度不够的商家
			Iterator<PointMerchant> iter = list.iterator();
			while (iter.hasNext()) {
				PointMerchant merchant = iter.next();
				if (merchant.getLimitAmount().compareTo(BigDecimal.ZERO) > 0 && merchant.getLimitAmount()
						.subtract(merchant.getUsedAmount()).compareTo(runOrder.getAmount()) < 0) {
					list.remove(merchant);
				}
			}
			// 随机返回一个商家
			if (list != null && list.size() > 0) {
				return modelMapper.map(list.get(random.nextInt(list.size())), PointMerchantDTO.class);
			}
		}
		// 当前无匹配的商家，请稍候再试
		throw new ServerException(BusinessErrorConstants.ERROR_RUN_MERCHANT_NO_MATCH);
	}

	public PointRunOrderDTO matchOrder(Long userId, Long merchantId) {
		PointMerchant merchant = pointMerchantDao.selectById(merchantId);
		if (merchant == null) {
			throw new ServerException(BusinessErrorConstants.ERROR_RUN_MERCHANT_NOTEXIST);
		}
		QueryWrapper<PointRunOrder> query = new QueryWrapper<>();
		query.eq(PointRunOrder.USER_ID, userId);
		query.eq(PointRunOrder.STATUS, RunOrderStatusEnum.PENDING);
		query.gt(PointRunOrder.EXPIRE_TIME, LocalDateTime.now());
		query.ge(PointRunOrder.AMOUNT, merchant.getMinAmount());
		query.le(PointRunOrder.AMOUNT, merchant.getMaxAmount());
		query.orderByAsc(PointRunOrder.AMOUNT);
		query.orderByAsc(PointRunOrder.GMT_CREATE);
		List<PointRunOrder> list = pointRunOrderDao.selectList(query);
		if (list != null && list.size() > 0) {
			return modelMapper.map(list.get(0), PointRunOrderDTO.class);
		}
		// 当前无匹配的兑换订单，请明天再试或者发起质押申请
		throw new ServerException(BusinessErrorConstants.ERROR_RUN_ORDER_NO_MATCH);
	}

	@Transactional
	public void run(Long userId, Long runOrderId, Long merchantId) {
		log.info("user {} run order {}, mechant {}", userId, runOrderId, merchantId);
		User user = userDao.selectById(userId);
		// step 1 : 验证订单
		PointRunOrder runOrder = pointRunOrderDao.selectById(runOrderId);
		runOrderVerify(userId, runOrder);
		// step 2 : 验证商家
		PointMerchant merchant = pointMerchantDao.selectById(merchantId);
		runMerchantVerify(runOrder.getAmount(), merchant);
		// step 3 : 修改商家已用金额
		if (merchant.getLimitAmount().compareTo(BigDecimal.ZERO) > 0) {
			merchant.setUsedAmount(merchant.getUsedAmount().add(runOrder.getAmount()));
			pointMerchantDao.updateById(merchant);
		}
		// step 4 : 修改产品订单当天已用次数
		PointProductOrder productOrder = pointProductOrderDao.selectById(runOrder.getProductOrderId());
		productOrder.setRunUsedQuantity(productOrder.getRunUsedQuantity() + 1);
		pointProductOrderDao.updateById(productOrder);
		// step 5 : 处理订单
		runOrder.setMerchantId(merchantId);
		runOrder.setMerchantName(merchant.getName());
		runOrder.setRunPoint(merchant.getRunPoint());
		runOrder.setStatus(RunOrderStatusEnum.PROCESSING);
		runOrder.setRunTime(LocalDateTime.now());
		pointRunOrderDao.updateById(runOrder);
		// step 6 : 创建兑换动态订单
		PointRunOrderDynamic runDynamicOrder = modelMapper.map(runOrder, PointRunOrderDynamic.class);
		runDynamicOrder.setId(runOrder.getId());
		runDynamicOrder.setUid(user.getUid());
		runDynamicOrder.setGmtCreate(runOrder.getGmtCreate());
		pointRunOrderDynamicDao.insert(runDynamicOrder);
		// step 7 : 等待成交
		int tradeDelayTime = computeTradeDelayTime(runOrder.getStarLevel());
		BigDecimal profit = computeProfit(runOrder.getAmount(), merchant.getRunPoint());
		boolean needDivide = false;
		BigDecimal profitDivide = BigDecimal.ZERO;
		BigDecimal profitDivideRatio = BigDecimal.ZERO;
		Long parentId = user.getParentId();
		if (user.getIsReal() != null && user.getIsReal() && !productOrder.getGift() && parentId != null
				&& parentId.longValue() > 0) {
			profitDivideRatio = queryProfitDivideRatio();
			if (profitDivideRatio != null && profitDivideRatio.compareTo(BigDecimal.ZERO) > 0) {
				needDivide = true;
				profitDivide = computeProfitDivide(profit, profitDivideRatio);
			}
		}
		RunOrderSettlementMessage message = new RunOrderSettlementMessage();
		message.setUserId(userId);
		message.setParentId(parentId);
		message.setRunOrderId(runOrderId);
		message.setProductOrderId(runOrder.getProductOrderId());
		message.setProfit(profit);
		message.setNeedDivide(needDivide);
		message.setProfitDivideRatio(profitDivideRatio);
		message.setProfitDivide(profitDivide);
		AMQPMessage<RunOrderSettlementMessage> amqpMessage = new AMQPMessage<RunOrderSettlementMessage>(message);
		amqpMessage.setMode(AMQPService.AMQPPublishMode.CONFIRMS);
		amqpMessage.setRoutingKey(RabbitMessageQueue.QUEUE_RUN_ORDER_SETTLEMENT);
//		amqpService.convertAndSendDelay(AMQPService.AMQPPublishMode.CONFIRMS,
//				RabbitMessageQueue.getRunOrderSettlementFanoutDelayExchange(0),
//				RabbitMessageQueue.QUEUE_RUN_ORDER_SETTLEMENT, amqpMessage, tradeDelayTime);
		amqpService.sendDelay(RabbitMessageQueue.EXCHANGE_RUN_ORDER_DELAY, RabbitMessageQueue.QUEUE_RUN_ORDER_DELAY,
				amqpMessage, tradeDelayTime);
		log.info("run order {}, starLevel is {}, tradeDelayTime is {}", runOrderId, runOrder.getStarLevel(),
				tradeDelayTime);
	}

	private void runOrderVerify(Long userId, PointRunOrder runOrder) {
		if (runOrder == null) {
			// 兑换订单不存在
			throw new ServerException(BusinessErrorConstants.ERROR_RUN_ORDER_NOTEXIST);
		}
		if (runOrder.getStatus() != RunOrderStatusEnum.PENDING
				|| runOrder.getExpireTime().isBefore(LocalDateTime.now())) {
			// 兑换订单状态不匹配或者已过期
			throw new ServerException(BusinessErrorConstants.ERROR_RUN_ORDER_STATUS_NOTMATCH);
		}
		if (!userId.equals(runOrder.getUserId())) {
			// 操作用户不匹配
			throw new ServerException(BusinessErrorConstants.ERROR_OPERATE_USER_NOTMATCH);
		}
	}

	private void runMerchantVerify(BigDecimal amount, PointMerchant merchant) {
		if (merchant == null) {
			// 商家不存在
			throw new ServerException(BusinessErrorConstants.ERROR_RUN_MERCHANT_NOTEXIST);
		}
		if (amount.compareTo(merchant.getMinAmount()) < 0 && amount.compareTo(merchant.getMaxAmount()) > 0) {
			// 兑换金额不匹配
			throw new ServerException(BusinessErrorConstants.ERROR_RUN_ORDER_AMOUNT_NOT_MATCH);
		}
		if (merchant.getLimitAmount().compareTo(BigDecimal.ZERO) > 0
				&& merchant.getLimitAmount().subtract(merchant.getUsedAmount()).compareTo(amount) < 0) {
			// 商户金额不足
			throw new ServerException(BusinessErrorConstants.ERROR_RUN_MERCHANT_AMOUNT_NOTENOUGH);
		}
	}

	private BigDecimal queryProfitDivideRatio() {
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
						if (dto.getLevel() == 1 && dto.getIncome().compareTo(BigDecimal.ZERO) >= 0) {
							return dto.getIncome();
						}
					}
				}
			} catch (Exception ex) {
			}
		}
		return BigDecimal.ZERO;
	}

	private BigDecimal computeProfit(BigDecimal amount, BigDecimal runPoint) {
		return amount.multiply(runPoint).setScale(0, RoundingMode.DOWN);
	}

	private BigDecimal computeProfitDivide(BigDecimal profit, BigDecimal profitDivideRatio) {
		return profit.multiply(profitDivideRatio).setScale(0, RoundingMode.HALF_UP);
	}

	private int computeTradeDelayTime(Integer startLevel) {
		int time = 5;
		if (startLevel != null) {
			JsonNode config = queryTradeFinishTime(startLevel);
			if (config != null) {
				int min = config.get("min").asInt();
				int max = config.get("max").asInt();
				time = min + random.nextInt(max - min);
			}
		}
		return time * 1000;
	}

	private JsonNode queryTradeFinishTime(Integer startLevel) {
		QueryWrapper<Config> query = new QueryWrapper<>();
		query.eq(Config.KEY, "tradeFinishTime");
		Config config = configDao.selectOne(query);
		if (config != null && !StringUtils.isBlank(config.getValue())) {
			JsonNode valueNode = JacksonUtil.decodeToNode(config.getValue());
			JsonNode result = valueNode.get("startLevel" + startLevel);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Transactional
	public void giftRunOrder(Long userId) {
		QueryWrapper<PointProductOrder> query = new QueryWrapper<>();
		query.eq(PointProductOrder.USER_ID, userId);
		query.eq(PointProductOrder.GIFT, true);
		List<PointProductOrder> list = pointProductOrderDao.selectList(query);
		if (list != null && list.size() > 0) {
			PointProductOrder productOrder = list.get(0);
			PointRunOrder runOrder = new PointRunOrder();
			runOrder.setId(idWorker.nextId());
			runOrder.setUserId(productOrder.getUserId());
			runOrder.setProductOrderId(productOrder.getId());
			runOrder.setProductId(productOrder.getProductId());
			runOrder.setProductName(productOrder.getProductName());
			runOrder.setStarLevel(productOrder.getStarLevel());
			runOrder.setAmount(productOrder.getAmount());
			runOrder.setMinRunPoint(productOrder.getMinRunPoint());
			runOrder.setMaxRunPoint(productOrder.getMaxRunPoint());
			runOrder.setProfit(BigDecimal.ZERO);
			runOrder.setStatus(RunOrderStatusEnum.PENDING);
			runOrder.setExpireTime(getExpireTime());
			pointRunOrderDao.insert(runOrder);
			productOrder.setRunTotalQuantity(productOrder.getRunTotalQuantity() + 1);
			pointProductOrderDao.updateById(productOrder);
		}
	}

	private LocalDateTime getExpireTime() {
		return LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(0, 0, 0));
	}

	public PageInfo<PointRunOrderDTO> userOrderPage(PointRunUserOrderRequest req) {
		QueryWrapper<PointRunOrder> query = new QueryWrapper<>();
		query.eq(PointRunOrder.USER_ID, req.getUserId());
		if (req.getStatusList() != null && req.getStatusList().size() == 1) {
			query.eq(PointRunOrder.STATUS, req.getStatusList().get(0).name());
			if (req.getStatusList().get(0) == RunOrderStatusEnum.PENDING) {
				query.gt(PointRunOrder.EXPIRE_TIME, LocalDateTime.now());
			}
		} else if (req.getStatusList() != null && req.getStatusList().size() > 1) {
			query.in(PointRunOrder.STATUS, req.getStatusList());
		}
		query.orderByDesc(PointRunOrder.RUN_TIME).orderByDesc(PointRunOrder.AMOUNT);
		IPage<PointRunOrder> pageWrapper = pointRunOrderDao.selectPage(new Page<>(req.getPage(), req.getSize()), query);
		PageInfo<PointRunOrderDTO> pageInfo = new PageInfo<>();
		pageInfo.setRecords(pageWrapper.getRecords().stream().map(temp -> modelMapper.map(temp, PointRunOrderDTO.class))
				.collect(Collectors.toList()));
		pageInfo.setPage((int) pageWrapper.getPages());
		pageInfo.setSize((int) pageWrapper.getSize());
		pageInfo.setTotal(pageWrapper.getTotal());
		return pageInfo;
	}

}
