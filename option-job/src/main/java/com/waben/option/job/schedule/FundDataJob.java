package com.waben.option.job.schedule;

import com.waben.option.common.interfaces.summary.FundDataAPI;
import com.waben.option.job.quartz.support.BaseJobSupporter;
import com.waben.option.job.utils.JobUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

import javax.annotation.Resource;

@Slf4j
public class FundDataJob extends BaseJobSupporter {

    @Resource
    private FundDataAPI fundDataAPI;

    @Configuration
    static class JobConfig {
        @Bean(name = "FundDataDetail")
        public JobDetailFactoryBean dayJob() {
            return JobUtil.createJobDetail(FundDataJob.class);
        }

        @Bean(name = "FundDataJobCronTrigger")
        public CronTriggerFactoryBean jobTriggerDay(@Qualifier("FundDataDetail") JobDetail jobDetail) {
            // 每天凌晨过0点过20分点执行
            return JobUtil.createCronTrigger(jobDetail, "0 35 0 * * ? *");
        }
    }

    public FundDataJob() {
        super(true);
    }

    @Override
    protected void doExecute(JobExecutionContext jobExecutionContext) throws Exception {
        log.info("======= start FundDataJob =======");
        fundDataAPI.create(null);
        log.info("======= end FundDataJob =======");
    }

}
