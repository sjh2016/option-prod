package com.waben.option.core.amqp.user;

import java.io.Serializable;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.waben.option.common.amqp.BaseAMPQConsumer;
import com.waben.option.common.amqp.message.UserLoginMessage;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.core.service.account.AccountService;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.repository.user.UserDao;

@Component
@RabbitListener(containerFactory = "rabbitListenerContainerFactory", admin = "amqpAdmin", queues = RabbitMessageQueue.QUEUE_USER_LOGIN)
public class UserLoginConsumer extends BaseAMPQConsumer<UserLoginMessage> {

    @Resource
    private AccountService accountService;

    @Resource
    private UserDao userDao;

    @Resource
    private RedisTemplate<Serializable, Object> redisTemplate;


//    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handle(UserLoginMessage message) {
        User user = userDao.selectById(message.getUserId());
        if (user == null) {
            return;
        }
        /*LocalDate lastLoginTime = LocalDate.now().plusDays(-1);
        if (user.getLastLoginTime() != null) {
            lastLoginTime = user.getLastLoginTime().toLocalDate();
        }*/
        user.setLastLoginTime(message.getLastLoginTime());
        user.setLastLoginIp(message.getLastLoginIp());
        userDao.updateById(user);
        /*if (lastLoginTime != null && lastLoginTime != LocalDate.now()) {
            String key = RedisKey.getKey(RedisKey.OPTION_USER_LOGIN_DAY_COUNT_KEY, message.getUserId(), LocalDate.now());
            Integer count = (Integer) redisTemplate.opsForValue().get(key);
            if (count == null) {
                List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
                transactionBeanList.add(AccountTransactionBean.builder().userId(message.getUserId()).type(TransactionEnum.CREDIT_LOGIN_PROFIT)
                        .amount(new BigDecimal(0.2)).transactionId(message.getUserId()).currency(CurrencyEnum.IDR).build());
                accountService.transaction(message.getUserId(), transactionBeanList);
            }
            redisTemplate.opsForValue().set(key, 1);
            redisTemplate.expire(key, 2, TimeUnit.DAYS);
        }*/
    }

}
