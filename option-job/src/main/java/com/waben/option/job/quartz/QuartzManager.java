package com.waben.option.job.quartz;

import com.google.common.collect.Sets;
import org.quartz.*;

import java.util.Map;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class QuartzManager {

    private static String JOB_GROUP_NAME = "DEFAULT";

    private static String TRIGGER_GROUP_NAME = "DEFAULT";

    /**
     * @param sched   调度器
     * @param jobName 任务名
     * @param cls     任务
     * @param time    时间设置，参考quartz说明文档
     * @Description: 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
     * @Title: QuartzManager.java
     */
    public static void addJob(Scheduler sched, String jobName, String triggerName, Class<? extends Job> cls,
                              Map<String, Object> dataMap, String time) {
        addJob(sched, jobName, JOB_GROUP_NAME, triggerName, TRIGGER_GROUP_NAME, cls, dataMap, time);
    }

    /**
     * @param sched            调度器
     * @param jobName          任务名
     * @param jobGroupName     任务组名
     * @param triggerName      触发器名
     * @param triggerGroupName 触发器组名
     * @param jobClass         任务
     * @param time             时间设置，参考quartz说明文档
     * @Description: 添加一个定时任务
     * @Title: QuartzManager.java
     */
    public static void addJob(Scheduler sched, String jobName, String jobGroupName, String triggerName,
                              String triggerGroupName, Class<? extends Job> jobClass, Map<String, Object> dataMap, String time) {
        try {
            JobKey jobKey = new JobKey(jobName, jobGroupName);
            JobDetail jobDetail = newJob(jobClass).storeDurably(true).withIdentity(jobKey)
                    .setJobData(new JobDataMap(dataMap)).build();
            // 触发器
            TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroupName);
            Trigger trigger = newTrigger().withIdentity(triggerKey).withSchedule(cronSchedule(time)).build();
            sched.scheduleJob(jobDetail, Sets.newHashSet(trigger), true);
            if (!sched.isShutdown()) {
                sched.start();// 启动
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param sched   调度器
     * @param jobName
     * @param time
     * @Description: 修改一个任务的触发时间(使用默认的任务组名 ， 触发器名 ， 触发器组名)
     * @Title: QuartzManager.java
     */
    public static void modifyJobTime(Scheduler sched, String jobName, String time) {
        modifyJobTime(sched, jobName, JOB_GROUP_NAME, time);
    }

    /**
     * @param sched            调度器 *
     * @param sched            调度器
     * @param triggerName
     * @param triggerGroupName
     * @param time
     * @Description: 修改一个任务的触发时间
     * @Title: QuartzManager.java
     */
    public static void modifyJobTime(Scheduler sched, String triggerName, String triggerGroupName, String time) {
        try {
            TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroupName);
            CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(time)) {
                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
                triggerBuilder.withIdentity(triggerName, triggerGroupName);
                triggerBuilder.withSchedule(cronSchedule(time));
                CronTrigger newTrigger = (CronTrigger) triggerBuilder.build();
                sched.rescheduleJob(triggerKey, newTrigger);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param sched   调度器
     * @param jobName
     * @Description: 移除一个任务(使用默认的任务组名 ， 触发器名 ， 触发器组名)
     * @Title: QuartzManager.java
     */
    public static void removeJob(Scheduler sched, String jobName) {
        removeJob(sched, jobName, JOB_GROUP_NAME, jobName, TRIGGER_GROUP_NAME);
    }

    public static void removeJob(Scheduler sched, String jobName, String triggerName) {
        removeJob(sched, jobName, JOB_GROUP_NAME, triggerName, TRIGGER_GROUP_NAME);
    }

    /**
     * @param sched            调度器
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     * @Description: 移除一个任务
     * @Title: QuartzManager.java
     */
    public static void removeJob(Scheduler sched, String jobName, String jobGroupName, String triggerName,
                                 String triggerGroupName) {
        try {
            TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroupName);
            sched.pauseTrigger(triggerKey);// 停止触发器
            sched.unscheduleJob(triggerKey);// 移除触发器
            JobKey jobKey = new JobKey(jobName, jobGroupName);
            sched.deleteJob(jobKey);// 删除任务
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param sched 调度器
     * @Description:启动所有定时任务
     * @Title: QuartzManager.java
     */
    public static void startJobs(Scheduler sched) {
        try {
            sched.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param sched 调度器
     * @Description:关闭所有定时任务
     */
    public static void shutdownJobs(Scheduler sched) {
        try {
            if (!sched.isShutdown()) {
                sched.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void addJob(Scheduler scheduler, JobDetail jobDetail, Trigger trigger) {
        try {
            scheduler.scheduleJob(jobDetail, Sets.newHashSet(trigger), true);
            if (!scheduler.isShutdown()) {
                scheduler.start();// 启动
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
