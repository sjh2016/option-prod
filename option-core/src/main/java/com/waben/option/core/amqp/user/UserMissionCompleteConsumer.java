package com.waben.option.core.amqp.user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.waben.option.common.amqp.BaseAMPQConsumer;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.model.bean.AccountTransactionBean;
import com.waben.option.common.model.bean.TradeTransactionRemark;
import com.waben.option.common.model.bean.TransactionRemark;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.model.enums.InviteAuditStatusEnum;
import com.waben.option.common.model.enums.TransactionEnum;
import com.waben.option.core.amqp.message.UserMissionCompleteMessage;
import com.waben.option.core.service.account.AccountService;
import com.waben.option.data.entity.user.UserMissionComplete;
import com.waben.option.data.repository.user.UserMissionCompleteDao;

@Component
@RabbitListener(containerFactory = "rabbitListenerContainerFactory", admin = "amqpAdmin", queues = RabbitMessageQueue.QUEUE_USER_MISSION_COMPLETE_STATEMENT)
public class UserMissionCompleteConsumer extends BaseAMPQConsumer<UserMissionCompleteMessage> {

    @Resource
    private AccountService accountService;

    @Resource
    private UserMissionCompleteDao userMissionCompleteDao;
    
	@Resource
	private StaticConfig staticConfig;

//    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handle(UserMissionCompleteMessage message) {
        UserMissionComplete complete = userMissionCompleteDao.selectById(message.getCompleteId());
        if (complete == null) return;
        complete.setInviteAuditStatus(InviteAuditStatusEnum.PASS.name());
        complete.setStatus(true);
        userMissionCompleteDao.updateById(complete);
        List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
        BigDecimal number = message.getInviteVolume().subtract(message.getMinLimitNumber());
        transactionBeanList.add(AccountTransactionBean.builder()
                .userId(message.getUserId()).type(TransactionEnum.CREDIT_INVITE_REGISTER)
                .amount(message.getAmount().multiply(number)).transactionId(complete.getId()).currency(staticConfig.getDefaultCurrency()).remark(buildTransactionRemark(ActivityTypeEnum.INVITE.name())).build());
        accountService.transaction(message.getUserId(), transactionBeanList);
    }

    private String buildTransactionRemark(String name) {
        TransactionRemark<TradeTransactionRemark> remark = new TransactionRemark<>(TransactionRemark.RemarkEnum.TRADE,
                TradeTransactionRemark.builder().args(name).build());
        return remark.toString();
    }
}
