package com.waben.option.job.service;

import com.waben.option.job.quartz.QuartzManager;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class JobService {

    @Resource
    private SchedulerFactoryBean schedulerFactory;

    /*public void updateNoticeJob(String jobName, String triggerName, LocalDateTime time, Long noticeId, TimeLogoEnum timeLogo) {
        Map<String, Object> dateMap = new HashMap<>();
        dateMap.put("noticeId", noticeId);
        dateMap.put("timeLogo", timeLogo);
        addJob(jobName, triggerName, NoticeJob.class, dateMap, convertDateTimeCronExpression(time));
    }

    public Pair<String, String> getNoticeJobName(TimeLogoEnum timeLogo, Long noticeId) {
        String jobName = timeLogo + "Notice" + noticeId + "JobDetail";
        String triggerName = timeLogo + "Notice" + noticeId + "CronTrigger";
        return ImmutablePair.of(jobName, triggerName);
    }*/

    public void addJob(String jobName, String triggerName, Class<? extends Job> clazz, Map<String, Object> dataMap,
                       String conExpression) {
        Scheduler scheduler = schedulerFactory.getScheduler();
        QuartzManager.addJob(scheduler, jobName, triggerName, clazz, dataMap, conExpression);
    }

    public void removeJob(String jobName) {
        Scheduler scheduler = schedulerFactory.getScheduler();
        QuartzManager.removeJob(scheduler, jobName);
    }

    public void removeJob(String jobName, String triggerName) {
        Scheduler scheduler = schedulerFactory.getScheduler();
        QuartzManager.removeJob(scheduler, jobName, triggerName);
    }

    private DateTimeFormatter df1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateTimeFormatter df2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateTimeFormatter df3 = DateTimeFormatter.ofPattern("HH:mm:ss");

    public String convertDateTimeCronExpression(String time) {
        LocalDateTime dateTime = LocalDateTime.parse(time, df1);
        return convertDateTimeCronExpression(dateTime);
    }

    public String convertDateTimeCronExpression(LocalDateTime dateTime) {
        return dateTime.getSecond() + " " + dateTime.getMinute() + " " + dateTime.getHour() + " "
                + dateTime.getDayOfMonth() + " " + dateTime.getMonthValue() + " ? " + dateTime.getYear();
    }

    public String convertDateCronExpression(String time) {
        LocalDate date = LocalDate.parse(time, df2);
        return convertDateCronExpression(date);
    }

    public String convertDateCronExpression(LocalDate date) {
        return "0 0 0 " + date.getDayOfMonth() + " " + date.getMonthValue() + " ? " + date.getYear();
    }

    public String convertTimeCronExpression(String time) {
        LocalTime localTime = LocalTime.parse(time, df3);
        return convertTimeCronExpression(localTime);
    }

    public String convertTimeCronExpression(LocalTime time) {
        return time.getSecond() + " " + time.getMinute() + " " + time.getHour() + " ? * * *";
    }

}
