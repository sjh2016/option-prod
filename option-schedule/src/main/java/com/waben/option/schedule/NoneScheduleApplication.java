package com.waben.option.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@AutoConfigurationPackage
@EnableFeignClients(basePackages = { "com.waben.option.common.interfaces", "com.waben.option.common.interfacesadmin" })
@SpringBootApplication(scanBasePackages = { "com.waben.option" }, exclude = JtaAutoConfiguration.class)
public class NoneScheduleApplication {

	public static void main(String[] args) {
		SpringApplication.run(NoneScheduleApplication.class);
	}

}
