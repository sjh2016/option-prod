package com.waben.option.common.configuration;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.waben.option.common.amqp.AMQPMessage;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.service.AMQPService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RabbitConfig {

	@Value("${spring.rabbitmq.consumer.min:1}")
	private int concurrentConsumers;

	@Value("${spring.rabbitmq.consumer.max:10}")
	private int maxConcurrentConsumers;

	@Value("${spring.rabbitmq.consumer.prefetchCount:10}")
	private int prefetchCount;

	@Bean
	public AMQPService amqpService(@Qualifier("rabbitTemplate") RabbitTemplate rabbitTemplate,
			@Qualifier("rabbitTransactionTemplate") RabbitTemplate rabbitTransactionTemplate) {
		return new AMQPService(rabbitTemplate, rabbitTransactionTemplate);
	}

	@Bean
	public RabbitConnectionFactoryBean rabbitConnectionFactoryBean(RabbitProperties config) throws Exception {
		RabbitConnectionFactoryBean factory = new RabbitConnectionFactoryBean();
		if (config.determineHost() != null) {
			factory.setHost(config.determineHost());
		}
		factory.setPort(config.determinePort());
		if (config.determineUsername() != null) {
			factory.setUsername(config.determineUsername());
		}
		if (config.determinePassword() != null) {
			factory.setPassword(config.determinePassword());
		}
		if (config.determineVirtualHost() != null) {
			factory.setVirtualHost(config.determineVirtualHost());
		}
		if (config.getRequestedHeartbeat() != null) {
			factory.setRequestedHeartbeat((int) config.getRequestedHeartbeat().getSeconds());
		}
		RabbitProperties.Ssl ssl = config.getSsl();
		if (ssl.getEnabled() != null && ssl.getEnabled()) {
			factory.setUseSSL(true);
			if (ssl.getAlgorithm() != null) {
				factory.setSslAlgorithm(ssl.getAlgorithm());
			}
			factory.setKeyStore(ssl.getKeyStore());
			factory.setKeyStorePassphrase(ssl.getKeyStorePassword());
			factory.setTrustStore(ssl.getTrustStore());
			factory.setTrustStorePassphrase(ssl.getTrustStorePassword());
		}
		if (config.getConnectionTimeout() != null) {
			factory.setConnectionTimeout((int) config.getConnectionTimeout().getSeconds() * 1000);
		}
		factory.afterPropertiesSet();
		return factory;
	}

	@Bean("rabbitConnectionFactory")
	public CachingConnectionFactory rabbitConnectionFactory(RabbitProperties config,
			RabbitConnectionFactoryBean factory) throws Exception {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(factory.getObject());
		connectionFactory.setAddresses(config.determineAddresses());
		connectionFactory.setPublisherReturns(config.isPublisherReturns());
		if (config.getCache().getChannel().getSize() != null) {
			connectionFactory.setChannelCacheSize(config.getCache().getChannel().getSize());
		}
		if (config.getCache().getConnection().getMode() != null) {
			connectionFactory.setCacheMode(config.getCache().getConnection().getMode());
		}
		if (config.getCache().getConnection().getSize() != null) {
			connectionFactory.setConnectionCacheSize(config.getCache().getConnection().getSize());
		}
		if (config.getCache().getChannel().getCheckoutTimeout() != null) {
			connectionFactory.setChannelCheckoutTimeout(
					(int) config.getCache().getChannel().getCheckoutTimeout().getSeconds() * 1000);
		}
		return connectionFactory;
	}

	@Bean("rabbitTransactionConnectionFactory")
	public CachingConnectionFactory rabbitTransactionConnectionFactory(RabbitProperties config,
			RabbitConnectionFactoryBean factory) throws Exception {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(factory.getObject());
		connectionFactory.setAddresses(config.determineAddresses());
		if (config.getCache().getChannel().getSize() != null) {
			connectionFactory.setChannelCacheSize(config.getCache().getChannel().getSize());
		}
		if (config.getCache().getConnection().getMode() != null) {
			connectionFactory.setCacheMode(config.getCache().getConnection().getMode());
		}
		if (config.getCache().getConnection().getSize() != null) {
			connectionFactory.setConnectionCacheSize(config.getCache().getConnection().getSize());
		}
		if (config.getCache().getChannel().getCheckoutTimeout() != null) {
			connectionFactory.setChannelCheckoutTimeout(
					(int) config.getCache().getChannel().getCheckoutTimeout().getSeconds() * 1000);
		}
		return connectionFactory;
	}

	@Bean("rabbitListenerContainerFactory")
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
			SimpleRabbitListenerContainerFactoryConfigurer configurer,
			@Qualifier("rabbitConnectionFactory") ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		factory.setConcurrentConsumers(concurrentConsumers);
		factory.setMaxConcurrentConsumers(maxConcurrentConsumers);
		factory.setPrefetchCount(prefetchCount);
		return factory;
	}

	@Bean("rabbitTransactionListenerContainerFactory")
	public SimpleRabbitListenerContainerFactory rabbitTransactionListenerContainerFactory(
			SimpleRabbitListenerContainerFactoryConfigurer configurer,
			@Qualifier("rabbitTransactionConnectionFactory") ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
		factory.setChannelTransacted(true);
		factory.setConcurrentConsumers(concurrentConsumers);
		factory.setMaxConcurrentConsumers(maxConcurrentConsumers);
		factory.setPrefetchCount(prefetchCount);
		return factory;
	}

	@Resource
	private IdWorker idWorker;

	@Bean
	public MessageConverter messageConverter() {
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
				.registerModule(new ParameterNamesModule()).registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule());
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		return new Jackson2JsonMessageEnhanceConverter(mapper);
	}

	public static class Jackson2JsonMessageEnhanceConverter extends Jackson2JsonMessageConverter {

		Jackson2JsonMessageEnhanceConverter(ObjectMapper jsonObjectMapper) {
			super(jsonObjectMapper);
		}

		public Object fromMessage(Message message, @Nullable Object conversionHint) throws MessageConversionException {
			MessageProperties properties = message.getMessageProperties();
			if (properties != null) {
				String contentType = properties.getContentType();
				if (contentType != null && contentType.equals(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)) {
					AMQPMessage amqpMessage = new AMQPMessage(new String(message.getBody()));
					amqpMessage.setExchange(RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
					amqpMessage.setRoutingKey(properties.getConsumerQueue());
					amqpMessage.setMode(AMQPService.AMQPPublishMode.CONFIRMS);
					return amqpMessage;
				}
			}
			return super.fromMessage(message, conversionHint);
		}

	}

	@Bean(name = "rabbitTemplate")
	public RabbitTemplate rabbitTemplate(@Qualifier("rabbitConnectionFactory") ConnectionFactory connectionFactory,
			ObjectProvider<MessageConverter> messageConverter, RabbitProperties properties) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		MessageConverter _messageConverter = messageConverter.getIfUnique();
		if (_messageConverter != null) {
			rabbitTemplate.setMessageConverter(_messageConverter);
		}
		rabbitTemplate.setMandatory(determineMandatoryFlag(properties));
		RabbitProperties.Template templateProperties = properties.getTemplate();
		RabbitProperties.Retry retryProperties = templateProperties.getRetry();
		if (retryProperties.isEnabled()) {
			rabbitTemplate.setRetryTemplate(createRetryTemplate(retryProperties));
		}
		if (templateProperties.getReceiveTimeout() != null) {
			rabbitTemplate.setReceiveTimeout(templateProperties.getReceiveTimeout().getSeconds() * 1000);
		}
		if (templateProperties.getReplyTimeout() != null) {
			rabbitTemplate.setReplyTimeout(templateProperties.getReplyTimeout().getSeconds() * 1000);
		}
		rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
			@Override
			public void confirm(CorrelationData correlationData, boolean ack, String cause) {
				log.info("AMQP_CONFIRM|" + correlationData.getId() + "|" + ack + "|" + cause);
			}
		});
		rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
			@Override
			public void returnedMessage(Message message, int replyCode, String replyText, String exchange,
					String routingKey) {
				log.info("AMQP_RETURN|" + exchange + "|" + routingKey + "|" + replyCode + "|" + replyText + "|"
						+ message);
			}
		});
		return rabbitTemplate;
	}

	@Bean(name = "rabbitTransactionTemplate")
	public RabbitTemplate rabbitTransactionTemplate(
			@Qualifier("rabbitTransactionConnectionFactory") ConnectionFactory connectionFactory,
			ObjectProvider<MessageConverter> messageConverter, RabbitProperties properties) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setChannelTransacted(true);
		MessageConverter _messageConverter = messageConverter.getIfUnique();
		if (_messageConverter != null) {
			rabbitTemplate.setMessageConverter(_messageConverter);
		}
		RabbitProperties.Template templateProperties = properties.getTemplate();
		RabbitProperties.Retry retryProperties = templateProperties.getRetry();
		if (retryProperties.isEnabled()) {
			rabbitTemplate.setRetryTemplate(createRetryTemplate(retryProperties));
		}
		if (templateProperties.getReceiveTimeout() != null) {
			rabbitTemplate.setReceiveTimeout(templateProperties.getReceiveTimeout().getSeconds() * 1000);
		}
		if (templateProperties.getReplyTimeout() != null) {
			rabbitTemplate.setReplyTimeout(templateProperties.getReplyTimeout().getSeconds() * 1000);
		}
		return rabbitTemplate;
	}

	private boolean determineMandatoryFlag(RabbitProperties properties) {
		Boolean mandatory = properties.getTemplate().getMandatory();
		return (mandatory != null ? mandatory : properties.isPublisherReturns());
	}

	private RetryTemplate createRetryTemplate(RabbitProperties.Retry properties) {
		RetryTemplate template = new RetryTemplate();
		SimpleRetryPolicy policy = new SimpleRetryPolicy();
		policy.setMaxAttempts(properties.getMaxAttempts());
		template.setRetryPolicy(policy);
		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setInitialInterval(properties.getInitialInterval().getSeconds() * 1000l);
		backOffPolicy.setMultiplier(properties.getMultiplier());
		backOffPolicy.setMaxInterval(properties.getMaxInterval().getSeconds() * 1000l);
		template.setBackOffPolicy(backOffPolicy);
		return template;
	}

	@Bean("amqpAdmin")
	@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "dynamic", matchIfMissing = true)
	public AmqpAdmin amqpAdmin(@Qualifier("rabbitConnectionFactory") ConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}

	@Bean("amqpTransactionAdmin")
	public AmqpAdmin amqpTransactionAdmin(
			@Qualifier("rabbitTransactionConnectionFactory") ConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}

	/**
	 * ---------------------------------------------delay--------------------------------------------------------
	 */
	@Bean
	public FanoutExchange exchangeFanoutDelay() {
		return new FanoutExchange(RabbitMessageQueue.EXCHANGE_FANOUT_DELAY, true, false);
	}

	@Bean
	public Queue queueDelay() {
		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
		return new Queue(RabbitMessageQueue.QUEUE_DELAY, true, false, false, arguments);
	}

	@Bean
	public Binding queueDelayBinding() {
		return BindingBuilder.bind(queueDelay()).to(exchangeFanoutDelay());
	}

	@Bean
	public FanoutExchange exchangeFanoutRetry() {
		return new FanoutExchange(RabbitMessageQueue.EXCHANGE_FANOUT_RETRY, true, false);
	}

	@Bean
	public Queue queueRetry() {
		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
		arguments.put("x-message-ttl", 15000);
		return new Queue(RabbitMessageQueue.QUEUE_RETRY, true, false, false, arguments);
	}

	@Bean
	public Binding queueJobRetryBinding() {
		return BindingBuilder.bind(queueRetry()).to(exchangeFanoutRetry());
	}

	@Bean
	public DirectExchange exchangeDirectDefault() {
		return new DirectExchange(RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT, true, false);
	}

	@Bean
	public Queue queueError() {
		return new Queue(RabbitMessageQueue.QUEUE_ERROR, true);
	}

	@Bean
	public Binding queueErrorBinding() {
		return BindingBuilder.bind(queueError()).to(exchangeDirectDefault()).with(RabbitMessageQueue.QUEUE_ERROR);
	}

	@Bean
	public Queue queueJob() {
		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_FANOUT_RETRY);
		return new Queue(RabbitMessageQueue.QUEUE_JOB, true, false, false, arguments);
	}

	@Bean
	public Binding queueJobBinding() {
		return BindingBuilder.bind(queueJob()).to(exchangeDirectDefault()).with(RabbitMessageQueue.QUEUE_JOB);
	}

}
