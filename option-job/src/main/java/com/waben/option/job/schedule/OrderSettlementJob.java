package com.waben.option.job.schedule;

import com.waben.option.common.interfaces.order.OrderAPI;
import com.waben.option.job.quartz.support.BaseJobSupporter;
import com.waben.option.job.utils.JobUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

import javax.annotation.Resource;

@Slf4j
public class OrderSettlementJob extends BaseJobSupporter {

    @Resource
    private OrderAPI orderAPI;

    @Value("${order.settlement.count:10}")
    private Integer count;

    @Configuration
    static class JobConfig {
        @Bean(name = "OrderSettlementDetail")
        public JobDetailFactoryBean dayJob() {
            return JobUtil.createJobDetail(OrderSettlementJob.class);
        }

        @Bean(name = "OrderSettlementJobCronTrigger")
        public CronTriggerFactoryBean jobTriggerDay(@Qualifier("OrderSettlementDetail") JobDetail jobDetail) {
            // 每小时执行
            return JobUtil.createCronTrigger(jobDetail, "0 0 */1 * * ?");
        }
    }

    public OrderSettlementJob() {
        super(false);
    }

    @Override
    protected void doExecute(JobExecutionContext jobExecutionContext) throws Exception {
        log.info("======= start StatisticsJob =======");
        orderAPI.orderSettlement(count);
        log.info("======= end StatisticsJob =======");
    }
}
