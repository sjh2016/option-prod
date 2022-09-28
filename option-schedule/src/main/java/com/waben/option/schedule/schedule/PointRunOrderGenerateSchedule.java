package com.waben.option.schedule.schedule;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.interfaces.point.PointProductOrderAPI;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
public class PointRunOrderGenerateSchedule {

	@Resource
	private PointProductOrderAPI pointProductOrderAPI;

	@Resource
	private StaticConfig staticConfig;

	/**
	 * 每天0点执行一次
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	public void schedule() {
		if (!staticConfig.isContract()) {
			log.info("======= start PointRunOrderGenerateSchedule =======");
			try {
				pointProductOrderAPI.generateRunOrderSchedule();
			} catch (Exception ex) {
				log.error("do PointRunOrderGenerateSchedule exception!", ex);
			}
			log.info("======= end PointRunOrderGenerateSchedule =======");
		}
	}

}
