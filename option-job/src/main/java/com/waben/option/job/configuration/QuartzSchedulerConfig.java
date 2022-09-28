package com.waben.option.job.configuration;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import javax.sql.DataSource;

import com.waben.option.job.quartz.AutowiringSpringBeanJobFactory;
import org.quartz.Calendar;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "quartz.enabled")
public class QuartzSchedulerConfig {

	@Bean
	public JobFactory jobFactory(final ApplicationContext applicationContext) {
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		return jobFactory;
	}

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean(final DataSource dataSource, final JobFactory jobFactory, final Optional<Map<String, Calendar>> calendarBeans,
													 final Optional<List<Trigger>> triggerBeans) throws IOException {
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		factory.setSchedulerName("quartz-scheduler");
		// this allows to update triggers in DB when updating settings in config file
		factory.setOverwriteExistingJobs(true);
		factory.setDataSource(dataSource);
		factory.setJobFactory(jobFactory);
		factory.setQuartzProperties(quartzProperties());
		// Here we will set all the calendar beans we have defined.
		if (calendarBeans.isPresent()) {
			Map<String, Calendar> calendars = calendarBeans.get();
			calendars.keySet().stream().forEach(key -> log.info("Schedule calendar {}", key));
			factory.setCalendars(calendars);
		}
		// Here we will set all the trigger beans we have defined.
		if (triggerBeans.isPresent()) {
			final List<Trigger> triggers = triggerBeans.get();
			triggers.stream().forEach(triggerBean -> log.info("Schedule trigger {}", triggerBean.getJobKey()));
			factory.setTriggers(triggers.toArray(new Trigger[triggers.size()]));
		}
		return factory;
	}

	@Bean
	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource("config/quartz.properties"));
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}

}