package com.waben.option.job.schedule;

import com.waben.option.common.interfaces.summary.UserDataAPI;
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
public class UserDataJob extends BaseJobSupporter {

    @Resource
    private UserDataAPI userDataAPI;

    @Configuration
    static class JobConfig {
        @Bean(name = "UserDataDetail")
        public JobDetailFactoryBean dayJob() {
            return JobUtil.createJobDetail(UserDataJob.class);
        }

        @Bean(name = "UserDataJobCronTrigger")
        public CronTriggerFactoryBean jobTriggerDay(@Qualifier("UserDataDetail") JobDetail jobDetail) {
            // 每天凌晨过0点过分点执行
            return JobUtil.createCronTrigger(jobDetail, "0 40 0 * * ? *");
        }
    }

    public UserDataJob() {
        super(true);
    }

    @Override
    protected void doExecute(JobExecutionContext jobExecutionContext) throws Exception {
        log.info("======= start UserDataJob =======");
        userDataAPI.create();
        log.info("======= end UserDataJob =======");
    }
}
