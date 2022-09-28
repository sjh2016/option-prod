package com.waben.option.schedule.schedule;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.interfaces.order.OrderDynamicAPI;
import com.waben.option.common.interfaces.point.PointRunOrderDynamicAPI;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
public class GenerateDynamicOrderSchedule {

	@Resource
	private StaticConfig staticConfig;

	@Resource
	private PointRunOrderDynamicAPI pointRunOrderDynamicAPI;

	@Resource
	private OrderDynamicAPI orderDynamicAPI;

	/**
	 * 每5分钟执行一次
	 */
	@Scheduled(cron = "0 */5 * * * ?")
	public void schedule() {
		if (!staticConfig.isContract()) {
			pointSchedule();
		} else {
			contractSchedule();
		}
	}

	private void pointSchedule() {
		log.info("======= start point GenerateDynamicOrderSchedule =======");
		try {
			pointRunOrderDynamicAPI.generate();
		} catch (Exception ex) {
			log.error("do point GenerateDynamicOrderSchedule exception!", ex);
		}
		log.info("======= end point GenerateDynamicOrderSchedule =======");
	}

	private void contractSchedule() {
		log.info("======= start contract GenerateDynamicOrderSchedule =======");
		try {
			orderDynamicAPI.generate(2);
		} catch (Exception ex) {
			log.error("do contract GenerateDynamicOrderSchedule exception!", ex);
		}
		log.info("======= end contract GenerateDynamicOrderSchedule =======");
	}

}