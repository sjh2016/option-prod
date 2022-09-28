package com.waben.option.core.amqp.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.waben.option.common.amqp.BaseAMPQConsumer;
import com.waben.option.common.amqp.message.OrderSettlementMessage;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.interfaces.account.AccountAPI;
import com.waben.option.common.interfaces.user.UserAPI;
import com.waben.option.common.model.bean.AccountTransactionBean;
import com.waben.option.common.model.bean.TradeTransactionRemark;
import com.waben.option.common.model.bean.TransactionRemark;
import com.waben.option.common.model.enums.TransactionEnum;
import com.waben.option.data.entity.order.Order;
import com.waben.option.data.repository.order.OrderDao;

@Component
@RabbitListener(containerFactory = "rabbitListenerContainerFactory", admin = "amqpAdmin", queues = RabbitMessageQueue.QUEUE_ORDER_SETTLEMENT)
public class OrderSettlementConsumer extends BaseAMPQConsumer<OrderSettlementMessage> {

	@Resource
	private AccountAPI accountAPI;

	@Resource
	private UserAPI userAPI;

	@Resource
	private OrderDao orderDao;
	
	@Resource
	private StaticConfig staticConfig;

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void handle(OrderSettlementMessage message) {
		Order order = orderDao.selectById(message.getOrderId());
		if (order == null) {
			return;
		}
		BigDecimal profitAmount = order.getAmount().multiply(order.getVolume()).multiply(order.getReturnRate())
				.divide(new BigDecimal(24), 2, RoundingMode.DOWN);
		order.setProfit(order.getProfit().add(profitAmount));
		orderDao.updateById(order);

		List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
		transactionBeanList.add(AccountTransactionBean.builder().userId(order.getUserId())
				.type(TransactionEnum.CREDIT_PROFIT).amount(profitAmount).transactionId(message.getOrderId())
				.currency(staticConfig.getDefaultCurrency()).remark(buildTransactionRemark(order.getName())).build());
		accountAPI.transaction(order.getUserId(), transactionBeanList);
		if (!order.getFree()) {
			userAPI.userRatioDivide(profitAmount, order.getUserId());
		}
	}

	private String buildTransactionRemark(String name) {
		TransactionRemark<TradeTransactionRemark> remark = new TransactionRemark<>(TransactionRemark.RemarkEnum.TRADE,
				TradeTransactionRemark.builder().args(name).build());
		return remark.toString();
	}
}
