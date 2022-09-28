package com.waben.option.common.amqp;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.waben.option.common.amqp.message.LoggerMessage;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.constants.RedisKey;
import com.waben.option.common.model.dto.push.PushDataDTO;
import com.waben.option.common.service.AMQPService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseAMPQConsumer<T> {

	private final static Logger LOG = LoggerFactory.getLogger("RabbitMQ");

	@Resource
	private RedisTemplate<Serializable, Object> redisTemplate;

	@Resource
	private AMQPService amqpService;

	@Resource
	private IdWorker idWorker;

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	@RabbitHandler
	public void handlerMessage(AMQPMessage<T> message) {
		long time = System.currentTimeMillis();
		String key = RedisKey.OPTION_AMPQ_RETRY_COUNT_KEY + message.getId();
		Integer count = (Integer) redisTemplate.opsForValue().get(key);
		if (count != null && count >= message.getMaxConsumeCount()) {
			message.setId(idWorker.nextId());
			amqpService.convertAndSendDelay(message.getMode(), message.getRoutingKey(), message, 30000);
			return;
		}
		boolean isSuccess = false;
		try {
			handle(message.getMessage());
			isSuccess = true;
		} catch (Exception e) {
			count = redisTemplate.opsForValue().increment(key, 1).intValue();
			if (count == 1) {
				redisTemplate.expire(key, 120000, TimeUnit.MILLISECONDS);
			}
			log.warn("AMPQ_COUNT_OVER_MAXCOUNT|{}|{}", count, message);
			log.error("", e);
			throw e;
		} finally {
			if ((message.getMessage() instanceof LoggerMessage) || (message.getMessage() instanceof PushDataDTO)) {
				return;
			}
			LOG.info("AMQP_EXECUTE|{}|{}|{}", isSuccess, System.currentTimeMillis() - time,
					message.getMessage().toString());
		}
	}

	public abstract void handle(T message);

}
