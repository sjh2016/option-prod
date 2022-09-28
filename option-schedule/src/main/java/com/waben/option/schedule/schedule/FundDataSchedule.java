package com.waben.option.schedule.schedule;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.waben.option.common.interfacesadmin.summary.AdminFundDataAPI;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
public class FundDataSchedule {

	@Resource
	private AdminFundDataAPI adminFundDataAPI;

	/**
	 * 每天0点35分执行
	 */
	@Scheduled(cron = "0 35 0 * * ?")
	public void schedule() {
		log.info("======= start FundDataSchedule =======");
		try {
			adminFundDataAPI.create(null);
		} catch (Exception ex) {
			log.error("do FundDataSchedule exception!", ex);
		}
		log.info("======= end FundDataSchedule =======");
	}

}
