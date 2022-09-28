package com.waben.option.core.service.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.account.AccountAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.bean.AccountTransactionBean;
import com.waben.option.common.model.dto.account.UserAccountDTO;
import com.waben.option.common.model.dto.order.PurchaseGoodsDTO;
import com.waben.option.common.model.enums.PurchaseGoodEnum;
import com.waben.option.common.model.enums.PurchaseGoodStatusEnum;
import com.waben.option.common.model.enums.TransactionEnum;
import com.waben.option.common.model.request.order.AuditUserPurchaseGoodsRequest;
import com.waben.option.common.model.request.order.BuyUserPurchaseGoodsRequest;
import com.waben.option.common.model.request.order.CreateUserPurchaseGoodsRequest;
import com.waben.option.common.model.request.order.UploadUserPurchaseGoodsImageRequest;
import com.waben.option.data.entity.order.PurchaseGoods;
import com.waben.option.data.repository.order.PurchaseGoodsDao;
import com.waben.option.data.repository.user.UserDao;

@Service
public class PurchaseGoodsService {

	@Resource
	private PurchaseGoodsDao purchaseGoodsDao;

	@Resource
	private AccountAPI accountAPI;

	@Resource
	private UserDao userDao;
	
	@Resource
	private StaticConfig staticConfig;

	@Resource
	private ModelMapper modelMapper;

	@Resource
	private IdWorker idWorker;

	public PageInfo<PurchaseGoodsDTO> queryList(Long userId, PurchaseGoodEnum type, PurchaseGoodStatusEnum status,
			int page, int size) {
		QueryWrapper<PurchaseGoods> query = new QueryWrapper<>();
		if (userId != null) {
			query = query.eq(PurchaseGoods.USER_ID, userId);
		}
		if (type != null) {
			query = query.eq(PurchaseGoods.TYPE, type);
		}
		if (status != null) {
			query = query.eq(PurchaseGoods.STATUS, status);
		}
		query.orderByDesc(PurchaseGoods.GMT_CREATE);
		PageInfo<PurchaseGoodsDTO> pageInfo = new PageInfo<>();
		IPage<PurchaseGoods> purchaseGoodsIPage = purchaseGoodsDao.selectPage(new Page<>(page, size), query);
		if (purchaseGoodsIPage.getTotal() > 0) {
			List<PurchaseGoodsDTO> purchaseGoodsList = purchaseGoodsIPage.getRecords().stream()
					.map(purchaseGoods -> modelMapper.map(purchaseGoods, PurchaseGoodsDTO.class))
					.collect(Collectors.toList());
			pageInfo.setRecords(purchaseGoodsList);
			pageInfo.setTotal(purchaseGoodsIPage.getTotal());
			pageInfo.setPage((int) purchaseGoodsIPage.getPages());
			pageInfo.setSize((int) purchaseGoodsIPage.getSize());
			return pageInfo;
		}
		return pageInfo;
	}

	/**
	 * 新增用户信息
	 * 
	 * @param request
	 * @return
	 */
//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public PurchaseGoodsDTO createUserInfo(CreateUserPurchaseGoodsRequest request) {
		PurchaseGoods purchaseGoods = new PurchaseGoods();
		if (request.getId() == null) {
			purchaseGoods.setId(idWorker.nextId());
			purchaseGoods.setUserId(request.getUserId());
			if (!StringUtils.isBlank(request.getName())) {
				purchaseGoods.setName(request.getName());
			}
			if (!StringUtils.isBlank(request.getSurname())) {
				purchaseGoods.setSurname(request.getSurname());
			}
			if (!StringUtils.isBlank(request.getProvince())) {
				purchaseGoods.setProvince(request.getProvince());
			}
			if (!StringUtils.isBlank(request.getPostCode())) {
				purchaseGoods.setPostCode(request.getPostCode());
			}
			if (!StringUtils.isBlank(request.getUrbanArea())) {
				purchaseGoods.setUrbanArea(request.getUrbanArea());
			}
			if (!StringUtils.isBlank(request.getStreetName())) {
				purchaseGoods.setStreetName(request.getStreetName());
			}
			if (!StringUtils.isBlank(request.getPhone())) {
				purchaseGoods.setPhone(request.getPhone());
			}
			purchaseGoodsDao.insert(purchaseGoods);
		} else {
			purchaseGoods = purchaseGoodsDao.selectById(request.getId());
			if (!StringUtils.isBlank(request.getName())) {
				purchaseGoods.setName(request.getName());
			}
			if (!StringUtils.isBlank(request.getSurname())) {
				purchaseGoods.setSurname(request.getSurname());
			}
			if (!StringUtils.isBlank(request.getProvince())) {
				purchaseGoods.setProvince(request.getProvince());
			}
			if (!StringUtils.isBlank(request.getPostCode())) {
				purchaseGoods.setPostCode(request.getPostCode());
			}
			if (!StringUtils.isBlank(request.getUrbanArea())) {
				purchaseGoods.setUrbanArea(request.getUrbanArea());
			}
			if (!StringUtils.isBlank(request.getStreetName())) {
				purchaseGoods.setStreetName(request.getStreetName());
			}
			if (!StringUtils.isBlank(request.getPhone())) {
				purchaseGoods.setPhone(request.getPhone());
			}
			purchaseGoodsDao.updateById(purchaseGoods);
		}
		return modelMapper.map(purchaseGoods, PurchaseGoodsDTO.class);
	}

	/**
	 * 购买商品
	 * 
	 * @return request
	 */
//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public PurchaseGoodsDTO buy(BuyUserPurchaseGoodsRequest request) {
		PurchaseGoods purchaseGoods = purchaseGoodsDao.selectById(request.getId());
		if (purchaseGoods == null)
			throw new ServerException(5005);
		if (purchaseGoods != null && purchaseGoods.getType() != null)
			throw new ServerException(5007);
		if (request.getType() == PurchaseGoodEnum.MILLIONS_SOLAR) {
			purchaseGoods.setPrice(new BigDecimal(20000000));
		}
		if (request.getType() == PurchaseGoodEnum.TWO_MILLIONS_SOLAR) {
			purchaseGoods.setPrice(new BigDecimal(40000000));
		}
		if (request.getQuantity() == null) {
			purchaseGoods.setQuantity(1);
		} else {
			if (request.getQuantity().intValue() <= 0) {
				throw new ServerException(1001);
			} else {
				purchaseGoods.setQuantity(request.getQuantity());
			}
		}
		BigDecimal totalPrice = purchaseGoods.getPrice().multiply(new BigDecimal(purchaseGoods.getQuantity()));
		purchaseGoods.setTotalPrice(totalPrice);
		purchaseGoods.setType(request.getType());
		purchaseGoods.setStatus(PurchaseGoodStatusEnum.PAYMENT_SUCCESSFUL);
		purchaseGoods.setExpireDate(LocalDate.now().plusDays(30L));
		purchaseGoodsDao.updateById(purchaseGoods);
		buyWater(request.getUserId(), request.getId(), totalPrice);
		return modelMapper.map(purchaseGoods, PurchaseGoodsDTO.class);
	}

	/**
	 * 审核
	 * 
	 * @param request
	 */
//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public PurchaseGoodsDTO auditGoods(AuditUserPurchaseGoodsRequest request) {
		PurchaseGoods purchaseGoods = purchaseGoodsDao.selectById(request.getId());
		if (purchaseGoods == null)
			throw new ServerException(5005);
		if (request.getStatus() == PurchaseGoodStatusEnum.PURCHASE_GOODS) {
			purchaseGoods.setStatus(PurchaseGoodStatusEnum.PURCHASE_GOODS);
		}
		if (request.getStatus() == PurchaseGoodStatusEnum.SEND_POST_GOODS) {
			purchaseGoods.setStatus(PurchaseGoodStatusEnum.SEND_POST_GOODS);
		}
		if (request.getStatus() == PurchaseGoodStatusEnum.HOME_INSTALLATION) {
			purchaseGoods.setStatus(PurchaseGoodStatusEnum.HOME_INSTALLATION);
		}
		purchaseGoodsDao.updateById(purchaseGoods);
		return modelMapper.map(purchaseGoods, PurchaseGoodsDTO.class);
	}

	/**
	 * 图片上传
	 * 
	 * @param request
	 */
//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public PurchaseGoodsDTO uploadImage(UploadUserPurchaseGoodsImageRequest request) {
		PurchaseGoods purchaseGoods = purchaseGoodsDao.selectById(request.getId());
		if (purchaseGoods == null)
			throw new ServerException(5005);
		if (!StringUtils.isBlank(request.getPicture0())) {
			purchaseGoods.setPicture0(request.getPicture0());
		}
		if (!StringUtils.isBlank(request.getPicture1())) {
			purchaseGoods.setPicture1(request.getPicture1());
		}
		if (!StringUtils.isBlank(request.getPicture2())) {
			purchaseGoods.setPicture2(request.getPicture2());
		}
		if (!StringUtils.isBlank(request.getPicture3())) {
			purchaseGoods.setPicture3(request.getPicture3());
		}
		purchaseGoodsDao.updateById(purchaseGoods);
		return modelMapper.map(purchaseGoods, PurchaseGoodsDTO.class);
	}

	/**
	 * 购买流水
	 * 
	 * @param userId
	 * @param type
	 */
	public void buyWater(Long userId, Long id, BigDecimal totalPrice) {
		UserAccountDTO account = accountAPI.queryAccount(userId);
		if (account != null) {
			BigDecimal amount = account.getBalance().subtract(account.getFreezeCapital());
			if (amount.compareTo(totalPrice) >= 0) {
				List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
				transactionBeanList
						.add(AccountTransactionBean.builder().userId(userId).type(TransactionEnum.DEBIT_BUY_GOOD)
								.amount(totalPrice).transactionId(id).currency(staticConfig.getDefaultCurrency()).build());
				accountAPI.transaction(userId, transactionBeanList);
			} else {
				throw new ServerException(3002);
			}
		}
	}

}
