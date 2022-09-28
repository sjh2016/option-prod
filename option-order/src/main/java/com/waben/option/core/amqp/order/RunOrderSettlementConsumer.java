package com.waben.option.core.amqp.order;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.waben.option.common.amqp.BaseAMPQConsumer;
import com.waben.option.common.amqp.message.RunOrderSettlementMessage;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.model.bean.AccountTransactionBean;
import com.waben.option.common.model.bean.TradeTransactionRemark;
import com.waben.option.common.model.bean.TransactionRemark;
import com.waben.option.common.model.enums.TransactionEnum;
import com.waben.option.core.service.account.AccountService;
import com.waben.option.data.entity.point.PointProductOrder;
import com.waben.option.data.entity.point.PointRunOrder;
import com.waben.option.data.entity.point.PointRunOrderDynamic;
import com.waben.option.data.repository.point.PointProductOrderDao;
import com.waben.option.data.repository.point.PointRunOrderDao;
import com.waben.option.data.repository.point.PointRunOrderDynamicDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
// @RabbitListener(containerFactory = "rabbitListenerContainerFactory", admin = "amqpAdmin", queues = RabbitMessageQueue.QUEUE_RUN_ORDER_SETTLEMENT)
@RabbitListener(containerFactory = "rabbitListenerContainerFactory", admin = "amqpAdmin", queues = RabbitMessageQueue.QUEUE_RUN_ORDER_DELAY)
public class RunOrderSettlementConsumer extends BaseAMPQConsumer<RunOrderSettlementMessage> {

	@Resource
	private PointRunOrderDao pointRunOrderDao;

	@Resource
	private PointRunOrderDynamicDao pointRunOrderDynamicDao;

	@Resource
	private PointProductOrderDao pointProductOrderDao;

	@Resource
	private AccountService accountService;
	
	@Resource
	private StaticConfig staticConfig;

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void handle(RunOrderSettlementMessage message) {
		log.info("run order settlement: {}", message);
		String finalTime = LocalDateTime.now().format(formatter);
		// 修改兑换动态订单状态
		UpdateWrapper<PointRunOrderDynamic> dynWrapper = new UpdateWrapper<>();
		dynWrapper.setSql("profit=" + message.getProfit() + ", status='SUCCESSFUL', final_time='" + finalTime + "'");
		dynWrapper.eq(PointRunOrderDynamic.ID, message.getRunOrderId());
		pointRunOrderDynamicDao.update(null, dynWrapper);
		if (message.getIsGenerate() != null && message.getIsGenerate()) {
			return;
		}
		// 修改产品订单收益
		UpdateWrapper<PointProductOrder> productWrapper = new UpdateWrapper<>();
		productWrapper.setSql("total_profit=total_profit+" + message.getProfit());
		productWrapper.eq(PointProductOrder.ID, message.getProductOrderId());
		pointProductOrderDao.update(null, productWrapper);
		// 修改兑换订单状态
		UpdateWrapper<PointRunOrder> updateWrapper = new UpdateWrapper<>();
		updateWrapper.setSql("profit=" + message.getProfit() + ", status='SUCCESSFUL', final_time='" + finalTime + "'");
		updateWrapper.eq(PointRunOrder.ID, message.getRunOrderId());
		pointRunOrderDao.update(null, updateWrapper);
		// 用户资金业务
		List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
		List<AccountTransactionBean> parentTransactionBeanList = new ArrayList<>();
		transactionBeanList.add(AccountTransactionBean.builder().userId(message.getUserId())
				.type(TransactionEnum.CREDIT_PROFIT).amount(message.getProfit()).transactionId(message.getRunOrderId())
				.currency(staticConfig.getDefaultCurrency()).remark(buildTransactionRemark(message.getRunOrderId())).build());
		// 上级用户资金业务
		if (message.isNeedDivide()) {
			parentTransactionBeanList.add(AccountTransactionBean.builder().userId(message.getParentId())
					.type(TransactionEnum.CREDIT_SUBORDINATE).amount(message.getProfitDivide())
					.transactionId(message.getRunOrderId()).currency(staticConfig.getDefaultCurrency())
					.remark(buildTransactionRemark(message.getRunOrderId())).build());
		}
		// 资金变化
		accountService.transaction(message.getUserId(), transactionBeanList);
		if (parentTransactionBeanList.size() > 0) {
			accountService.transaction(message.getParentId(), parentTransactionBeanList);
		}
	}

	private String buildTransactionRemark(Long runOrderId) {
		TransactionRemark<TradeTransactionRemark> remark = new TransactionRemark<>(TransactionRemark.RemarkEnum.TRADE,
				TradeTransactionRemark.builder().args(String.valueOf(runOrderId)).build());
		return remark.toString();
	}

}
