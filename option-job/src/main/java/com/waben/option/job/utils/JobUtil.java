package com.waben.option.job.utils;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import java.util.Map;

public class JobUtil {
	
	public static JobDetailFactoryBean createJobDetail(Class<? extends Job> jobClass) {
		JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
		factoryBean.setJobClass(jobClass);
		// job has to be durable to be stored in DB:
		factoryBean.setDurability(true);
		return factoryBean;
	}

	public static SimpleTriggerFactoryBean createTrigger(JobDetail jobDetail, long pollFrequencyMs) {
		SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
		factoryBean.setJobDetail(jobDetail);
		factoryBean.setStartDelay(0L);
		factoryBean.setRepeatInterval(pollFrequencyMs);
		factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
		// in case of misfire, ignore all missed triggers and continue :
		factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
		return factoryBean;
	}

	// Use this method for creating cron triggers instead of simple triggers:
	public static CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, String cronExpression) {
		return createCronTrigger(jobDetail, cronExpression, null);
	}
	
	// Use this method for creating cron triggers instead of simple triggers:
	public static CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, String cronExpression,
			String calendarName) {
		return createCronTrigger(jobDetail, cronExpression, null, calendarName);
	}
	
	public static CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, String cronExpression,
			Map<String, Object> dataMap, String calendarName) {
		CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
		factoryBean.setJobDetail(jobDetail);
		factoryBean.setCronExpression(cronExpression);
		factoryBean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
		if (calendarName != null) {
			factoryBean.setCalendarName(calendarName);
		}
		if(dataMap != null) {
			jobDetail.getJobDataMap().putAll(dataMap);
		}
		return factoryBean;
	}
	
}
