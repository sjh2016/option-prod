package com.waben.option.core.amqp.user;

import java.io.Serializable;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.waben.option.common.amqp.BaseAMPQConsumer;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.core.amqp.message.UserAccountStatementMessage;

@Component
@RabbitListener(containerFactory = "rabbitListenerContainerFactory", admin = "amqpAdmin", queues = RabbitMessageQueue.QUEUE_USER_ACCOUNT_STATEMENT)
public class AccountStatementConsumer extends BaseAMPQConsumer<UserAccountStatementMessage> {

    @Resource
    private IdWorker idWorker;

    @Resource
    private RedisTemplate<Serializable, Object> redisTemplate;


//    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handle(UserAccountStatementMessage message) {

    }

}
