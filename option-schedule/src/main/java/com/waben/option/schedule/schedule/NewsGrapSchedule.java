package com.waben.option.schedule.schedule;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.interfaces.thirdparty.NewsGrabAPI;
import com.waben.option.common.model.enums.NewsTypeEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
public class NewsGrapSchedule {

	@Resource
	private NewsGrabAPI newsGrapAPI;

	@Resource
	private StaticConfig staticConfig;

	/**
	 * 每5分钟抓取一次
	 */
	@Scheduled(cron = "0 */5 * * * ?")
	public void schedule() {
		if (staticConfig.isContract()) {
			log.info("======= start NewsGrapSchedule =======");
			try {
				newsGrapAPI.grap(NewsTypeEnum.ECONOMY, null);
			} catch (Exception ex) {
				log.error("do NewsGrapSchedule exception!", ex);
			}
			log.info("======= end NewsGrapSchedule =======");
		}
	}

}
