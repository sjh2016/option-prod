package com.waben.option.thirdparty.service.sms.amazon;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.waben.option.common.amqp.AMQPMessage;
import com.waben.option.common.amqp.message.SendEmailMessage;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.model.enums.EmailTypeEnum;
import com.waben.option.common.service.AMQPService;
import com.waben.option.thirdparty.service.sms.AbstractBaseSmsService;

@Service
public class AmazonGroupEmailService extends AbstractBaseSmsService {

	@Resource
	protected AMQPService amqpService;

	public boolean sendCode(String toEmail, String code, EmailTypeEnum type, String content, String ip) {
		amqpService.convertAndSend(AMQPService.AMQPPublishMode.CONFIRMS, RabbitMessageQueue.QUEUE_SEND_GROUP_EMAIL,
				new AMQPMessage<SendEmailMessage>(new SendEmailMessage(toEmail, code, content, type, 1)));
		return true;
	}
}
