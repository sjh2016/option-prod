package com.waben.option.common.service;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.waben.option.common.amqp.AMQPMessage;
import com.waben.option.common.constants.RabbitMessageQueue;

public class AMQPService {

	public enum AMQPPublishMode {
		CONFIRMS, TRANSACTION;
	}

	private RabbitTemplate rabbitTemplate;

	private RabbitTemplate rabbitTransactionTemplate;

	public AMQPService(RabbitTemplate rabbitTemplate, RabbitTemplate rabbitTransactionTemplate) {
		this.rabbitTemplate = rabbitTemplate;
		this.rabbitTransactionTemplate = rabbitTransactionTemplate;
	}

	public <T> void convertAndSendDelay(AMQPPublishMode mode, String routingKey, AMQPMessage<T> payload, int delayMs) {
		convertAndSendDelay(mode, RabbitMessageQueue.EXCHANGE_FANOUT_DELAY, routingKey, payload, delayMs, null);
	}

	public <T> void convertAndSendDelay(AMQPPublishMode mode, String exchange, String routingKey,
			AMQPMessage<T> payload, int delayMs) {
		convertAndSendDelay(mode, exchange, routingKey, payload, delayMs, null);
	}

	public <T> void convertAndSendDelay(AMQPPublishMode mode, String exchange, String routingKey,
			AMQPMessage<T> payload, int delayMs, Map<String, Object> headers) {
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setExpiration(String.valueOf(delayMs));
		if (headers != null) {
			messageProperties.getHeaders().putAll(headers);
		}
		Message message = buildMessage(mode, rabbitTemplate.getMessageConverter().toMessage(payload, messageProperties),
				rabbitTransactionTemplate.getMessageConverter().toMessage(payload, messageProperties));
		send(mode, exchange, routingKey, message);
	}

	public <T> void convertAndSend(AMQPPublishMode mode, String routingKey, AMQPMessage<T> payload) {
		convertAndSend(mode, RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT, routingKey, payload, null);
	}

	public <T> void convertAndSend(AMQPPublishMode mode, String exchange, String routingKey, AMQPMessage<T> payload) {
		convertAndSend(mode, exchange, routingKey, payload, null);
	}

	public <T> void convertAndSend(AMQPPublishMode mode, String exchange, String routingKey, AMQPMessage<T> payload,
			Map<String, Object> headers) {
		MessageProperties messageProperties = new MessageProperties();
		if (headers != null) {
			messageProperties.getHeaders().putAll(headers);
		}
		payload.setMode(mode);
		payload.setExchange(exchange);
		payload.setRoutingKey(routingKey);
		Message message = buildMessage(mode, rabbitTemplate.getMessageConverter().toMessage(payload, messageProperties),
				rabbitTransactionTemplate.getMessageConverter().toMessage(payload, messageProperties));
		send(mode, exchange, routingKey, message);
	}

	public <T> void sendDelay(String exchange, String routingKey, AMQPMessage<T> payload, long time) {
		this.rabbitTemplate.convertAndSend(exchange, routingKey, payload, message -> {
			message.getMessageProperties().setHeader("x-delay", time);
			return message;
		});
	}

	public <T> void send(AMQPPublishMode mode, String exchange, String routingKey, T message) {
		if (mode == null) {
			throw new RuntimeException("mode is null");
		}

		if (StringUtils.isBlank(exchange)) {
			throw new RuntimeException("exchange is null");
		}

		if (StringUtils.isBlank(routingKey)) {
			throw new RuntimeException("routingKey is null");
		}

		if (message == null) {
			throw new RuntimeException("message is null");
		}

		CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
		switch (mode) {
		case CONFIRMS:
			if (TransactionSynchronizationManager.isActualTransactionActive()) {
				TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {

					@Override
					public void afterCompletion(int status) {
						if (status == STATUS_COMMITTED) {
							rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationId);
						}
					}

				});
			} else {
				rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationId);
			}
			break;
		case TRANSACTION:
			rabbitTransactionTemplate.convertAndSend(exchange, routingKey, message, correlationId);
			break;
		}
	}

	private Message buildMessage(AMQPPublishMode mode, Message message2, Message message3) {
		Message message = null;
		switch (mode) {
		case CONFIRMS:
			message = message2;
			break;
		case TRANSACTION:
			message = message3;
			break;
		}
		return message;
	}

}
