package com.waben.option.thirdparty.configuration;

import com.waben.option.common.constants.RabbitMessageQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig extends com.waben.option.common.configuration.RabbitConfig {

    @Bean("queueSendEmailConsumer")
    public Queue queueSendEmailConsumer() {
        return new Queue(RabbitMessageQueue.QUEUE_SEND_EMAIL, true);
    }

    @Bean
    public Binding queueSendEmailConsumerBinding(@Qualifier("queueSendEmailConsumer") Queue queue) {
        return BindingBuilder.bind(queue).to(exchangeDirectDefault()).with(RabbitMessageQueue.QUEUE_SEND_EMAIL);
    }

    @Bean("queueSendGroupEmailConsumer")
    public Queue queueSendGroupEmailConsumer() {
        return new Queue(RabbitMessageQueue.QUEUE_SEND_GROUP_EMAIL, true);
    }

    @Bean
    public Binding queueSendGroupEmailConsumerBinding(@Qualifier("queueSendGroupEmailConsumer") Queue queue) {
        return BindingBuilder.bind(queue).to(exchangeDirectDefault()).with(RabbitMessageQueue.QUEUE_SEND_GROUP_EMAIL);
    }
}
