package com.waben.option.core.configuration;

import com.waben.option.common.constants.RabbitMessageQueue;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SettleRabbitConfig {

    @Resource
    private DirectExchange exchangeDirectDefault;

    /********************* 订单分组结算START **********************/

    @Bean("queueOrderGroupSettlementConsumer")
    public Queue queueOrderGroupSettlementConsumer() {
        return new Queue(RabbitMessageQueue.QUEUE_ORDER_GROUP_SETTLEMENT, true);
    }

    @Bean("queueOrderGroupWagerConsumer")
    public Queue queueOrderGroupWagerConsumer() {
        return new Queue(RabbitMessageQueue.QUEUE_ORDER_GROUP_WAGER, true);
    }

    @Bean
    public Binding queueOrderGroupSettlementConsumerBinding(
            @Qualifier("queueOrderGroupSettlementConsumer") Queue queue) {
        return BindingBuilder.bind(queue).to(exchangeDirectDefault)
                .with(RabbitMessageQueue.QUEUE_ORDER_GROUP_SETTLEMENT);
    }

    @Bean
    public Binding queueOrderGroupWagerConsumerBinding(
            @Qualifier("queueOrderGroupWagerConsumer") Queue queue) {
        return BindingBuilder.bind(queue).to(exchangeDirectDefault)
                .with(RabbitMessageQueue.QUEUE_ORDER_GROUP_WAGER);
    }

    @Bean
    public FanoutExchange orderGroupSettlementFanoutDelayExchange0() {
        return new FanoutExchange(RabbitMessageQueue.getOrderGroupSettlementFanoutDelayExchange(0), true, false);
    }

    @Bean
    public Queue orderGroupSettlementDelayQueue0() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupSettlementDelayQueue(0), true, false, false, arguments);
    }

    @Bean
    public Queue orderGroupWagerDelayQueue0() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupWagerDelayQueue(0), true, false, false, arguments);
    }

    @Bean
    public Queue orderGroupWagerDelayQueue1() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupWagerDelayQueue(1), true, false, false, arguments);
    }

    @Bean
    public Queue orderGroupWagerDelayQueue2() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupWagerDelayQueue(2), true, false, false, arguments);
    }

    @Bean
    public Queue orderGroupWagerDelayQueue3() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupWagerDelayQueue(3), true, false, false, arguments);
    }

    @Bean
    public Queue orderGroupWagerDelayQueue4() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupWagerDelayQueue(4), true, false, false, arguments);
    }

    @Bean
    public Queue orderGroupWagerDelayQueue5() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupWagerDelayQueue(5), true, false, false, arguments);
    }

    @Bean
    public Queue orderGroupWagerDelayQueue6() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupWagerDelayQueue(6), true, false, false, arguments);
    }

    @Bean
    public Queue orderGroupWagerDelayQueue7() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupWagerDelayQueue(7), true, false, false, arguments);
    }

    @Bean
    public Queue orderGroupWagerDelayQueue8() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupWagerDelayQueue(8), true, false, false, arguments);
    }

    @Bean
    public Queue orderGroupWagerDelayQueue9() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupWagerDelayQueue(9), true, false, false, arguments);
    }

    @Bean
    public Binding orderGroupSettlementDelayBinding0() {
        return BindingBuilder.bind(orderGroupSettlementDelayQueue0()).to(orderGroupSettlementFanoutDelayExchange0());
    }

    @Bean
    public FanoutExchange orderGroupSettlementFanoutDelayExchange1() {
        return new FanoutExchange(RabbitMessageQueue.getOrderGroupSettlementFanoutDelayExchange(1), true, false);
    }

    @Bean
    public Queue orderGroupSettlementDelayQueue1() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupSettlementDelayQueue(1), true, false, false, arguments);
    }

    @Bean
    public Binding orderGroupSettlementDelayBinding1() {
        return BindingBuilder.bind(orderGroupSettlementDelayQueue1()).to(orderGroupSettlementFanoutDelayExchange1());
    }

    @Bean
    public FanoutExchange orderGroupSettlementFanoutDelayExchange2() {
        return new FanoutExchange(RabbitMessageQueue.getOrderGroupSettlementFanoutDelayExchange(2), true, false);
    }

    @Bean
    public Queue orderGroupSettlementDelayQueue2() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupSettlementDelayQueue(2), true, false, false, arguments);
    }

    @Bean
    public Binding orderGroupSettlementDelayBinding2() {
        return BindingBuilder.bind(orderGroupSettlementDelayQueue2()).to(orderGroupSettlementFanoutDelayExchange2());
    }

    @Bean
    public FanoutExchange orderGroupSettlementFanoutDelayExchange3() {
        return new FanoutExchange(RabbitMessageQueue.getOrderGroupSettlementFanoutDelayExchange(3), true, false);
    }

    @Bean
    public Queue orderGroupSettlementDelayQueue3() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupSettlementDelayQueue(3), true, false, false, arguments);
    }

    @Bean
    public Binding orderGroupSettlementDelayBinding3() {
        return BindingBuilder.bind(orderGroupSettlementDelayQueue3()).to(orderGroupSettlementFanoutDelayExchange3());
    }

    @Bean
    public FanoutExchange orderGroupSettlementFanoutDelayExchange4() {
        return new FanoutExchange(RabbitMessageQueue.getOrderGroupSettlementFanoutDelayExchange(4), true, false);
    }

    @Bean
    public Queue orderGroupSettlementDelayQueue4() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupSettlementDelayQueue(4), true, false, false, arguments);
    }

    @Bean
    public Binding orderGroupSettlementDelayBinding4() {
        return BindingBuilder.bind(orderGroupSettlementDelayQueue4()).to(orderGroupSettlementFanoutDelayExchange4());
    }

    @Bean
    public FanoutExchange orderGroupSettlementFanoutDelayExchange5() {
        return new FanoutExchange(RabbitMessageQueue.getOrderGroupSettlementFanoutDelayExchange(5), true, false);
    }

    @Bean
    public Queue orderGroupSettlementDelayQueue5() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupSettlementDelayQueue(5), true, false, false, arguments);
    }

    @Bean
    public Binding orderGroupSettlementDelayBinding5() {
        return BindingBuilder.bind(orderGroupSettlementDelayQueue5()).to(orderGroupSettlementFanoutDelayExchange5());
    }

    @Bean
    public FanoutExchange orderGroupSettlementFanoutDelayExchange6() {
        return new FanoutExchange(RabbitMessageQueue.getOrderGroupSettlementFanoutDelayExchange(6), true, false);
    }

    @Bean
    public Queue orderGroupSettlementDelayQueue6() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupSettlementDelayQueue(6), true, false, false, arguments);
    }

    @Bean
    public Binding orderGroupSettlementDelayBinding6() {
        return BindingBuilder.bind(orderGroupSettlementDelayQueue6()).to(orderGroupSettlementFanoutDelayExchange6());
    }

    @Bean
    public FanoutExchange orderGroupSettlementFanoutDelayExchange7() {
        return new FanoutExchange(RabbitMessageQueue.getOrderGroupSettlementFanoutDelayExchange(7), true, false);
    }

    @Bean
    public Queue orderGroupSettlementDelayQueue7() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupSettlementDelayQueue(7), true, false, false, arguments);
    }

    @Bean
    public Binding orderGroupSettlementDelayBinding7() {
        return BindingBuilder.bind(orderGroupSettlementDelayQueue7()).to(orderGroupSettlementFanoutDelayExchange7());
    }

    @Bean
    public FanoutExchange orderGroupSettlementFanoutDelayExchange8() {
        return new FanoutExchange(RabbitMessageQueue.getOrderGroupSettlementFanoutDelayExchange(8), true, false);
    }

    @Bean
    public Queue orderGroupSettlementDelayQueue8() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupSettlementDelayQueue(8), true, false, false, arguments);
    }

    @Bean
    public Binding orderGroupSettlementDelayBinding8() {
        return BindingBuilder.bind(orderGroupSettlementDelayQueue8()).to(orderGroupSettlementFanoutDelayExchange8());
    }

    @Bean
    public FanoutExchange orderGroupSettlementFanoutDelayExchange9() {
        return new FanoutExchange(RabbitMessageQueue.getOrderGroupSettlementFanoutDelayExchange(9), true, false);
    }

    @Bean
    public Queue orderGroupSettlementDelayQueue9() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getOrderGroupSettlementDelayQueue(9), true, false, false, arguments);
    }

    @Bean
    public Binding orderGroupSettlementDelayBinding9() {
        return BindingBuilder.bind(orderGroupSettlementDelayQueue9()).to(orderGroupSettlementFanoutDelayExchange9());
    }

    /********************* 订单分组结算END **********************/

    /********************* 跑分订单结算START **********************/

    @Bean("runOrderSettlementQueue")
    public Queue runOrderSettlementQueue() {
        return new Queue(RabbitMessageQueue.QUEUE_RUN_ORDER_SETTLEMENT, true);
    }

    @Bean
    public Binding runOrderSettlementQueueBinding(@Qualifier("runOrderSettlementQueue") Queue queue) {
        return BindingBuilder.bind(queue).to(exchangeDirectDefault).with(RabbitMessageQueue.QUEUE_RUN_ORDER_SETTLEMENT);
    }

    @Bean
    public FanoutExchange runOrderSettlementFanoutDelayExchange0() {
        return new FanoutExchange(RabbitMessageQueue.getRunOrderSettlementFanoutDelayExchange(0), true, false);
    }

    @Bean
    public Queue runOrderSettlementDelayQueue0() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", RabbitMessageQueue.EXCHANGE_DIRECT_DEFAULT);
        return new Queue(RabbitMessageQueue.getRunOrderSettlementDelayQueue(0), true, false, false, arguments);
    }

    @Bean
    public Binding runOrderSettlementDelayBinding0() {
        return BindingBuilder.bind(runOrderSettlementDelayQueue0()).to(runOrderSettlementFanoutDelayExchange0());
    }

    /********************* 跑分订单结算END **********************/

    /********************* 跑分订单结算延迟队列START **********************/
    @Bean("runOrderDelayQueue")
    public Queue runOrderDelayQueue() {
        return new Queue(RabbitMessageQueue.QUEUE_RUN_ORDER_DELAY, true);
    }

    @Bean("runOrderDelayExchange")
    public CustomExchange runOrderDelayExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(RabbitMessageQueue.EXCHANGE_RUN_ORDER_DELAY, "x-delayed-message", true, false, args);
    }

    @Bean
    public Binding runOrderDelayBinding(@Qualifier("runOrderDelayQueue") Queue queue,
                                        @Qualifier("runOrderDelayExchange") CustomExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(RabbitMessageQueue.QUEUE_RUN_ORDER_DELAY).noargs();
    }

    /********************* 跑分订单结算延迟队列END **********************/

}
