package com.waben.option.common.amqp;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.component.SpringContext;
import com.waben.option.common.service.AMQPService;
import lombok.Data;

@Data
public class AMQPMessage<T> {

	private AMQPService.AMQPPublishMode mode;

	private String exchange;
	
	private String routingKey;
	
	private long id;

	private int maxConsumeCount;

	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
	private T message;

	
	public AMQPMessage() {
		this(null);
	}

	public AMQPMessage(T message) {
		this(message, 20);
	}

	public AMQPMessage(long id, T message) {
		this(id, message, 20);
	}

	public AMQPMessage(T message, int maxConsumeCount) {
		this(SpringContext.getBean(IdWorker.class).nextId(), message, maxConsumeCount);
	}

	public AMQPMessage(long id, T message, int maxConsumeCount) {
		this.id = id;
		this.message = message;
		this.maxConsumeCount = maxConsumeCount;
	}

}
