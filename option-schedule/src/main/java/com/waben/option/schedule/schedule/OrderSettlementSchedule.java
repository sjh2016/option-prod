package com.waben.option.schedule.schedule;

import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.interfaces.order.OrderAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
@EnableScheduling
public class OrderSettlementSchedule {

    @Resource
    private OrderAPI orderAPI;

    @Value("${order.settlement.count:10}")
    private Integer count;

    @Resource
    private StaticConfig staticConfig;

    /**
     * 每天22点执行一次
     */
    // @Scheduled(cron = "0 0 */1 * * ?")
//    @Scheduled(cron = "0 0 0/1 * * ?")
    public void schedule() {
        if (staticConfig.isContract()) {
            log.info("======= start OrderSettlementSchedule =======");
            try {
                // orderAPI.orderSettlement(count);
//                orderAPI.settlement();
            } catch (Exception ex) {
                log.error("do OrderSettlementSchedule exception!", ex);
            }
            log.info("======= end OrderSettlementSchedule =======");
        }
    }

}
