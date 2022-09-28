package com.waben.option.common.constants;

import java.text.MessageFormat;

public class RabbitMessageQueue {

    public static final String EXCHANGE_FANOUT_DELAY = "exchange.fanout.delay";
    public static final String EXCHANGE_FANOUT_RETRY = "exchange.fanout.retry";
    public static final String EXCHANGE_DIRECT_DEFAULT = "exchange.direct.default";

    public static final String QUEUE_DELAY = "queue.delay";
    public static final String QUEUE_RETRY = "queue.retry";
    public static final String QUEUE_ERROR = "queue.error";
    public static final String QUEUE_JOB = "queue.job";

    public static final String QUEUE_LOGGER_USER = "queue.logger.user";

    public static final String QUEUE_USER_LOGIN = "queue.user.login";
    public static final String QUEUE_USER_REGISTER = "queue.user.register";
    public static final String QUEUE_USER_BEREAL = "queue.user.bereal";

    public static final String QUEUE_USER_ACCOUNT_STATEMENT = "queue.user.account.statement";
    public static final String QUEUE_USER_MISSION_COMPLETE_STATEMENT = "queue.user.mission.complete.statement";


    public static final String QUEUE_SEND_EMAIL = "queue.send.email";
    public static final String QUEUE_SEND_GROUP_EMAIL = "queue.send.group.email";
    public static final String QUEUE_PUSH_SYSTEM_NOTICE = "queue.push.system.notice";

    // 订单分组结算
    public static final String QUEUE_ORDER_SETTLEMENT = "queue.order.settlement";
    public static final String EXCHANGE_FANOUT_ORDER_SETTLEMENT_DELAY = "exchange.fanout.delay.order.{0}.settlement";
    public static final String QUEUE_ORDER_SETTLEMENT_DELAY = "queue.delay.order.{0}.settlement";
    
    // 用户订单分组结算
    public static final String QUEUE_ORDER_GROUP_SETTLEMENT = "queue.order.group.settlement";
    public static final String EXCHANGE_FANOUT_DELAY_ORDER_GROUP_SETTLEMENT = "exchange.fanout.delay.order.group.{0}.settlement";
    public static final String QUEUE_DELAY_ORDER_GROUP_SETTLEMENT = "queue.delay.order.group.{0}.settlement";
    
    // 跑分订单分组结算
    public static final String QUEUE_RUN_ORDER_SETTLEMENT = "queue.run.order.settlement";
    public static final String EXCHANGE_FANOUT_DELAY_RUN_ORDER_SETTLEMENT = "exchange.fanout.delay.run.order.{0}.settlement";
    public static final String QUEUE_DELAY_RUN_ORDER_SETTLEMENT = "queue.delay.run.order.{0}.settlement";
    
    // 跑分订单延迟结算
    public static final String QUEUE_RUN_ORDER_DELAY = "queue.run.order.delay";
    public static final String EXCHANGE_RUN_ORDER_DELAY = "exchange.run.order.delay";

    //投资上分收益
    public static final String QUEUE_ORDER_GROUP_WAGER = "queue.order.group.wager";
    public static final String EXCHANGE_FANOUT_DELAY_ORDER_GROUP_WAGER = "exchange.fanout.delay.order.group.{0}.wager";
    public static final String QUEUE_DELAY_ORDER_GROUP_WAGER = "queue.delay.order.group.{0}.wager";

    public static String getExchangeFanoutSettlementDelay(int type) {
        return MessageFormat.format(EXCHANGE_FANOUT_ORDER_SETTLEMENT_DELAY, type);
    }

    public static String getSettlementDelay(int type) {
        return MessageFormat.format(QUEUE_ORDER_SETTLEMENT_DELAY, type);
    }
    
    public static String getOrderGroupSettlementFanoutDelayExchange(int group) {
        return MessageFormat.format(EXCHANGE_FANOUT_DELAY_ORDER_GROUP_SETTLEMENT, group);
    }

    public static String getOrderGroupWagerFanoutDelayExchange(int group) {
        return MessageFormat.format(EXCHANGE_FANOUT_DELAY_ORDER_GROUP_WAGER, group);
    }

    public static String getOrderGroupSettlementDelayQueue(int group) {
        return MessageFormat.format(QUEUE_DELAY_ORDER_GROUP_SETTLEMENT, group);
    }

    public static String getOrderGroupWagerDelayQueue(int group) {
        return MessageFormat.format(QUEUE_DELAY_ORDER_GROUP_WAGER, group);
    }
    
    public static String getRunOrderSettlementFanoutDelayExchange(int group) {
        return MessageFormat.format(EXCHANGE_FANOUT_DELAY_RUN_ORDER_SETTLEMENT, group);
    }

    public static String getRunOrderSettlementDelayQueue(int group) {
        return MessageFormat.format(QUEUE_DELAY_RUN_ORDER_SETTLEMENT, group);
    }
    
}
