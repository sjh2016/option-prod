package com.waben.option.job.schedule;

import com.waben.option.common.interfaces.user.UserMissionAPI;
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
import java.time.LocalDate;

@Slf4j
public class UserMissionJob extends BaseJobSupporter {

    @Resource
    private UserMissionAPI userMissionAPI;

    @Configuration
    static class JobConfig {
        @Bean(name = "UserMissionJobDetail")
        public JobDetailFactoryBean dayJob() {
            return JobUtil.createJobDetail(UserMissionJob.class);
        }

        @Bean(name = "UserMissionJobJobCronTrigger")
        public CronTriggerFactoryBean jobTriggerDay(@Qualifier("UserMissionJobDetail") JobDetail jobDetail) {
            // 每天20点执行
            return JobUtil.createCronTrigger(jobDetail, "0 50 23 * * ? *");
        }
    }

    public UserMissionJob() {
        super(false);
    }

    @Override
    protected void doExecute(JobExecutionContext jobExecutionContext) throws Exception {
        log.info("======= start userMissionJob =======");
        userMissionAPI.autoAward(LocalDate.now().toString());
        log.info("======= end userMissionJob =======");
    }
}
