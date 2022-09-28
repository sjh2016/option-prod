package com.waben.option.schedule.schedule;

import javax.annotation.Resource;

import com.waben.option.common.interfaces.summary.UserDataAPI;

import lombok.extern.slf4j.Slf4j;

@Deprecated
@Slf4j
//@Component
//@EnableScheduling
public class UserDataSchedule {

	@Resource
	private UserDataAPI userDataAPI;

	/**
	 * 每天0点40分执行
	 */
//	@Scheduled(cron = "0 40 0 * * ?")
	public void schedule() {
		log.info("======= start UserDataSchedule =======");
		try {
			userDataAPI.create();
		} catch (Exception ex) {
			log.error("do UserDataSchedule exception!", ex);
		}
		log.info("======= end UserDataSchedule =======");
	}

}
