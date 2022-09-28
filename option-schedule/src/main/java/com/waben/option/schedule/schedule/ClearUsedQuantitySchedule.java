package com.waben.option.schedule.schedule;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.interfaces.point.PointProductAPI;
import com.waben.option.common.interfaces.resource.CommodityAPI;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
public class ClearUsedQuantitySchedule {

	@Resource
	private CommodityAPI commodityAPI;

	@Resource
	private PointProductAPI pointProductAPI;

	@Resource
	private StaticConfig staticConfig;

	/**
	 * 每天0点执行一次
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	public void schedule() {
		if (staticConfig.isContract()) {
			contractSchedule();
		} else {
			pointSchedule();
		}
	}

	private void contractSchedule() {
		log.info("======= start contract ClearUsedQuantitySchedule =======");
		try {
			commodityAPI.clearUsedQuantity();
		} catch (Exception ex) {
			log.error("do contract ClearUsedQuantitySchedule exception!", ex);
		}
		log.info("======= end contract ClearUsedQuantitySchedule =======");
	}

	private void pointSchedule() {
		log.info("======= start point ClearUsedQuantitySchedule =======");
		try {
			pointProductAPI.clearSchedule();
		} catch (Exception ex) {
			log.error("do point ClearUsedQuantitySchedule exception!", ex);
		}
		log.info("======= end point ClearUsedQuantitySchedule =======");
	}

}