package com.waben.option.schedule.schedule;

import java.time.LocalDate;

import javax.annotation.Resource;

import com.waben.option.common.interfaces.activity.ActivityAPI;

import lombok.extern.slf4j.Slf4j;

@Deprecated
@Slf4j
//@Component
//@EnableScheduling
public class UserMissionSchedule {

	@Resource
	private ActivityAPI activityAPI;

	/**
	 * 每天23点50分执行
	 */
	// @Scheduled(cron = "0 50 23 * * ?")
	public void schedule() {
		log.info("======= start UserMissionSchedule =======");
		try {
			activityAPI.inviteReceive(LocalDate.now().toString());
		} catch (Exception ex) {
			log.error("do UserMissionSchedule exception!", ex);
		}
		log.info("======= end UserMissionSchedule =======");
	}

}
