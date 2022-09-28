package com.waben.option.core.amqp.order;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.waben.option.common.amqp.BaseAMPQConsumer;
import com.waben.option.common.amqp.message.OrderGroupSettlementMessage;
import com.waben.option.common.amqp.message.OrderGroupSettlementMessage.OrderSettlementInfo;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.model.bean.AccountTransactionBean;
import com.waben.option.common.model.bean.TradeTransactionRemark;
import com.waben.option.common.model.bean.TransactionRemark;
import com.waben.option.common.model.enums.TransactionEnum;
import com.waben.option.core.service.account.AccountService;
import com.waben.option.data.entity.order.Order;
import com.waben.option.data.repository.order.OrderDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RabbitListener(containerFactory = "rabbitListenerContainerFactory", admin = "amqpAdmin", queues = RabbitMessageQueue.QUEUE_ORDER_GROUP_SETTLEMENT)
public class OrderGroupSettlementConsumer extends BaseAMPQConsumer<OrderGroupSettlementMessage> {

    @Resource
    private OrderDao orderDao;

    @Resource
    private AccountService accountService;

    @Resource
    private StaticConfig staticConfig;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handle(OrderGroupSettlementMessage message) {
        try {
            log.info("OrderGroupSettlementMessage: userId {}, groupIndex {}", message.getUserId(), message.getGroupIndex());
            List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
            List<AccountTransactionBean> parentTransactionBeanList = new ArrayList<>();
            List<AccountTransactionBean> secondParentTransactionBeanList = new ArrayList<>();
            List<AccountTransactionBean> thirdParentTransactionBeanList = new ArrayList<>();
            List<OrderSettlementInfo> orderInfoList = message.getOrderInfoList();
            for (OrderSettlementInfo orderInfo : orderInfoList) {
                // 修改订单收益
                UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
                updateWrapper.setSql("profit=profit+" + orderInfo.getProfit());
                updateWrapper.setSql("worked_days=worked_days+1");
                if ((orderInfo.getWorkedDays() + 1) >= orderInfo.getCycle().intValue()) {
                    updateWrapper.setSql("status='FINISHED'");
                    if (orderInfo.getNeedReturn() != null && orderInfo.getNeedReturn()) {
                        // 到期退回投注本金，按照需求取消该设置 2022-04-15
//					transactionBeanList.add(AccountTransactionBean.builder().userId(message.getUserId())
//							.type(TransactionEnum.CREDIT_RETURN_WAGER).amount(orderInfo.getActualAmount())
//							.transactionId(orderInfo.getId()).currency(staticConfig.getDefaultCurrency())
//							.remark(buildTransactionRemark(orderInfo.getName())).build());
                    }
                }
                updateWrapper.eq(Order.ID, orderInfo.getId());
                orderDao.update(null, updateWrapper);
                // 用户资金业务
                transactionBeanList.add(AccountTransactionBean.builder().userId(message.getUserId())
                        .type(TransactionEnum.CREDIT_PROFIT).amount(orderInfo.getProfit()).transactionId(orderInfo.getId())
                        .currency(staticConfig.getDefaultCurrency()).remark(buildTransactionRemark(orderInfo.getName()))
                        .build());
                // 上级用户资金业务
                if (orderInfo.isNeedDivide()) {
                    parentTransactionBeanList.add(AccountTransactionBean.builder().userId(message.getParentId())
                            .type(TransactionEnum.CREDIT_SUBORDINATE).amount(orderInfo.getProfitDivide())
                            .transactionId(orderInfo.getId()).currency(staticConfig.getDefaultCurrency())
                            .remark(buildTransactionRemark(orderInfo.getName())).build());
                    if (null != message.getSecondParentId() && 0 != message.getSecondParentId() && null != message.getSecondProfitDivideRatio()) {
                        secondParentTransactionBeanList.add(AccountTransactionBean.builder().userId(message.getSecondParentId())
                                .type(TransactionEnum.CREDIT_SUBORDINATE).amount(orderInfo.getSecondProfitDivide())
                                .transactionId(orderInfo.getId()).currency(staticConfig.getDefaultCurrency())
                                .remark(buildTransactionRemark(orderInfo.getName())).build());
                    }
                    if (null != message.getThirdParentId() && 0 != message.getThirdParentId() && null != message.getThirdProfitDivideRatio()) {
                        thirdParentTransactionBeanList.add(AccountTransactionBean.builder().userId(message.getThirdParentId())
                                .type(TransactionEnum.CREDIT_SUBORDINATE).amount(orderInfo.getThirdProfitDivide())
                                .transactionId(orderInfo.getId()).currency(staticConfig.getDefaultCurrency())
                                .remark(buildTransactionRemark(orderInfo.getName())).build());
                    }
                }
            }
            accountService.transaction(message.getUserId(), transactionBeanList);
            if (parentTransactionBeanList.size() > 0) {
                accountService.transactionComm(message.getParentId(), parentTransactionBeanList, 1);
            }
            if (secondParentTransactionBeanList.size() > 0) {
                accountService.transactionComm(message.getSecondParentId(), secondParentTransactionBeanList, 2);
            }
            if (thirdParentTransactionBeanList.size() > 0) {
                accountService.transactionComm(message.getThirdParentId(), thirdParentTransactionBeanList, 3);
            }
        } catch (Exception e) {
            log.error("刷新数据异常W", e);
        }
    }

    private String buildTransactionRemark(String name) {
        TransactionRemark<TradeTransactionRemark> remark = new TransactionRemark<>(TransactionRemark.RemarkEnum.TRADE,
                TradeTransactionRemark.builder().args(name).build());
        return remark.toString();
    }

}
