package com.waben.option.core.task;

import com.waben.option.core.service.settlement.SettlementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class RefreshOrderCacheTask {

    @Resource
    private SettlementService settlementService;

     @Scheduled(cron = "0 0 23 * * ?")
    //@Scheduled(cron = "0 */10 * * * ?")
    public void schedule() {
        settlementService.settlement();
    }

}
