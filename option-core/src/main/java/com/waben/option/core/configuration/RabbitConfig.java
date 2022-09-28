package com.waben.option.core.configuration;

import com.waben.option.common.constants.RabbitMessageQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig extends com.waben.option.common.configuration.RabbitConfig {

	@Bean("queueUserLoggerConsumer")
	public Queue queueUserLoggerConsumer() {
		return new Queue(RabbitMessageQueue.QUEUE_LOGGER_USER, true);
	}

	@Bean
	public Binding queueUserLoggerConsumerBinding(@Qualifier("queueUserLoggerConsumer") Queue queue) {
		return BindingBuilder.bind(queue).to(exchangeDirectDefault()).with(RabbitMessageQueue.QUEUE_LOGGER_USER);
	}

	@Bean("queuePushSystemNoticeConsumer")
	public Queue queuePushSystemNoticeConsumer() {
		return new Queue(RabbitMessageQueue.QUEUE_PUSH_SYSTEM_NOTICE, false);
	}

	@Bean
	public Binding queuePushSystemNoticeConsumerBinding(@Qualifier("queuePushSystemNoticeConsumer") Queue queue) {
		return BindingBuilder.bind(queue).to(exchangeDirectDefault()).with(RabbitMessageQueue.QUEUE_PUSH_SYSTEM_NOTICE);
	}

	@Bean("queueUserLoginConsumer")
	public Queue queueUserLoginConsumer() {
		return new Queue(RabbitMessageQueue.QUEUE_USER_LOGIN, true);
	}

	@Bean
	public Binding queueUserLoginConsumerBinding(@Qualifier("queueUserLoginConsumer") Queue queue) {
		return BindingBuilder.bind(queue).to(exchangeDirectDefault()).with(RabbitMessageQueue.QUEUE_USER_LOGIN);
	}

	@Bean("queueUserRegisterConsumer")
	public Queue queueUserRegisterConsumer() {
		return new Queue(RabbitMessageQueue.QUEUE_USER_REGISTER, true);
	}

	@Bean
	public Binding queueUserRegisterConsumerBinding(@Qualifier("queueUserRegisterConsumer") Queue queue) {
		return BindingBuilder.bind(queue).to(exchangeDirectDefault()).with(RabbitMessageQueue.QUEUE_USER_REGISTER);
	}
	
	@Bean("queueUserBerealConsumer")
	public Queue queueUserBerealConsumer() {
		return new Queue(RabbitMessageQueue.QUEUE_USER_BEREAL, true);
	}

	@Bean
	public Binding queueUserBerealConsumerBinding(@Qualifier("queueUserBerealConsumer") Queue queue) {
		return BindingBuilder.bind(queue).to(exchangeDirectDefault()).with(RabbitMessageQueue.QUEUE_USER_BEREAL);
	}

	@Bean("queueUserAccountStatementConsumer")
	public Queue queueUserAccountStatementConsumer() {
		return new Queue(RabbitMessageQueue.QUEUE_USER_ACCOUNT_STATEMENT, true);
	}

	@Bean
	public Binding queueUserAccountStatementConsumerBinding(
			@Qualifier("queueUserAccountStatementConsumer") Queue queue) {
		return BindingBuilder.bind(queue).to(exchangeDirectDefault())
				.with(RabbitMessageQueue.QUEUE_USER_ACCOUNT_STATEMENT);
	}

	@Bean("queueUserMissionCompleteStatementConsumer")
	public Queue queueUserMissionCompleteStatementConsumer() {
		return new Queue(RabbitMessageQueue.QUEUE_USER_MISSION_COMPLETE_STATEMENT, true);
	}

	@Bean
	public Binding queueUserMissionCompleteStatementConsumerBinding(
			@Qualifier("queueUserMissionCompleteStatementConsumer") Queue queue) {
		return BindingBuilder.bind(queue).to(exchangeDirectDefault())
				.with(RabbitMessageQueue.QUEUE_USER_MISSION_COMPLETE_STATEMENT);
	}

	/********************* 订单结算START **********************/

	@Bean("queueOrderSettlementConsumer")
	public Queue queueOrderSettlementConsumer() {
		return new Queue(RabbitMessageQueue.QUEUE_ORDER_SETTLEMENT, true);
	}

	@Bean
	public Binding queueOrderSettlementConsumerBinding(@Qualifier("queueOrderSettlementConsumer") Queue queue) {
		return BindingBuilder.bind(queue).to(exchangeDirectDefault()).with(RabbitMessageQueue.QUEUE_ORDER_SETTLEMENT);
	}

	@Bean
	public FanoutExchange exchangeFanoutOrderSettlementZeroDelay() {
		return new FanoutExchange(RabbitMessageQueue.getExchangeFanoutSettlementDelay(0), true, false);
	}

	@Bean
	public Queue queueOrderSettlementZeroDelay() {
		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
		return new Queue(RabbitMessageQueue.getSettlementDelay(0), true, false, false, arguments);
	}

	@Bean
	public Binding queueDelayOrderSettlementZeroBinding() {
		return BindingBuilder.bind(queueOrderSettlementZeroDelay()).to(exchangeFanoutOrderSettlementZeroDelay());
	}

	@Bean
	public FanoutExchange exchangeFanoutOrderSettlementOneDelay() {
		return new FanoutExchange(RabbitMessageQueue.getExchangeFanoutSettlementDelay(1), true, false);
	}

	@Bean
	public Queue queueOrderSettlementOneDelay() {
		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
		return new Queue(RabbitMessageQueue.getSettlementDelay(1), true, false, false, arguments);
	}

	@Bean
	public Binding queueDelayOrderSettlementBinding() {
		return BindingBuilder.bind(queueOrderSettlementOneDelay()).to(exchangeFanoutOrderSettlementOneDelay());
	}

	@Bean
	public FanoutExchange exchangeFanoutOrderSettlementTwoDelay() {
		return new FanoutExchange(RabbitMessageQueue.getExchangeFanoutSettlementDelay(2), true, false);
	}

	@Bean
	public Queue queueOrderSettlementTwoDelay() {
		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
		return new Queue(RabbitMessageQueue.getSettlementDelay(2), true, false, false, arguments);
	}

	@Bean
	public Binding queueDelayOrderSettlementTwoBinding() {
		return BindingBuilder.bind(queueOrderSettlementTwoDelay()).to(exchangeFanoutOrderSettlementTwoDelay());
	}

	@Bean
	public FanoutExchange exchangeFanoutOrderSettlementThreeDelay() {
		return new FanoutExchange(RabbitMessageQueue.getExchangeFanoutSettlementDelay(3), true, false);
	}

	@Bean
	public Queue queueOrderSettlementThreeDelay() {
		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
		return new Queue(RabbitMessageQueue.getSettlementDelay(3), true, false, false, arguments);
	}

	@Bean
	public Binding queueDelayOrderSettlementThreeBinding() {
		return BindingBuilder.bind(queueOrderSettlementThreeDelay()).to(exchangeFanoutOrderSettlementThreeDelay());
	}

	@Bean
	public FanoutExchange exchangeFanoutOrderSettlementFourDelay() {
		return new FanoutExchange(RabbitMessageQueue.getExchangeFanoutSettlementDelay(4), true, false);
	}

	@Bean
	public Queue queueOrderSettlementFourDelay() {
		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
		return new Queue(RabbitMessageQueue.getSettlementDelay(4), true, false, false, arguments);
	}

	@Bean
	public Binding queueDelayOrderSettlementFourBinding() {
		return BindingBuilder.bind(queueOrderSettlementFourDelay()).to(exchangeFanoutOrderSettlementFourDelay());
	}

	@Bean
	public FanoutExchange exchangeFanoutOrderSettlementFiveDelay() {
		return new FanoutExchange(RabbitMessageQueue.getExchangeFanoutSettlementDelay(5), true, false);
	}

	@Bean
	public Queue queueOrderSettlementFiveDelay() {
		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
		return new Queue(RabbitMessageQueue.getSettlementDelay(5), true, false, false, arguments);
	}

	@Bean
	public Binding queueDelayOrderSettlementFiveBinding() {
		return BindingBuilder.bind(queueOrderSettlementFiveDelay()).to(exchangeFanoutOrderSettlementFiveDelay());
	}

	@Bean
	public FanoutExchange exchangeFanoutOrderSettlementSixDelay() {
		return new FanoutExchange(RabbitMessageQueue.getExchangeFanoutSettlementDelay(6), true, false);
	}

	@Bean
	public Queue queueOrderSettlementSixDelay() {
		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
		return new Queue(RabbitMessageQueue.getSettlementDelay(6), true, false, false, arguments);
	}

	@Bean
	public Binding queueDelayOrderSettlementSixBinding() {
		return BindingBuilder.bind(queueOrderSettlementSixDelay()).to(exchangeFanoutOrderSettlementSixDelay());
	}

	@Bean
	public FanoutExchange exchangeFanoutOrderSettlementSevenDelay() {
		return new FanoutExchange(RabbitMessageQueue.getExchangeFanoutSettlementDelay(7), true, false);
	}

	@Bean
	public Queue queueOrderSettlementSevenDelay() {
		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
		return new Queue(RabbitMessageQueue.getSettlementDelay(7), true, false, false, arguments);
	}

	@Bean
	public Binding queueDelayOrderSettlementSevenBinding() {
		return BindingBuilder.bind(queueOrderSettlementSevenDelay()).to(exchangeFanoutOrderSettlementSevenDelay());
	}

	@Bean
	public FanoutExchange exchangeFanoutOrderSettlementEightDelay() {
		return new FanoutExchange(RabbitMessageQueue.getExchangeFanoutSettlementDelay(8), true, false);
	}

	@Bean
	public Queue queueOrderSettlementEightDelay() {
		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
		return new Queue(RabbitMessageQueue.getSettlementDelay(8), true, false, false, arguments);
	}

	@Bean
	public Binding queueDelayOrderSettlementEightBinding() {
		return BindingBuilder.bind(queueOrderSettlementEightDelay()).to(exchangeFanoutOrderSettlementEightDelay());
	}

	@Bean
	public FanoutExchange exchangeFanoutOrderSettlementNineDelay() {
		return new FanoutExchange(RabbitMessageQueue.getExchangeFanoutSettlementDelay(9), true, false);
	}

	@Bean
	public Queue queueOrderSettlementNineDelay() {
		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
		return new Queue(RabbitMessageQueue.getSettlementDelay(9), true, false, false, arguments);
	}

	@Bean
	public Binding queueDelayOrderSettlementNineBinding() {
		return BindingBuilder.bind(queueOrderSettlementNineDelay()).to(exchangeFanoutOrderSettlementNineDelay());
	}

	/********************* 订单结算END **********************/
}
