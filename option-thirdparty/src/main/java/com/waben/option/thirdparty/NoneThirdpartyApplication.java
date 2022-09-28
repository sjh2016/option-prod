package com.waben.option.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@AutoConfigurationPackage
@EnableFeignClients(basePackages = { "com.waben.option.common.interfaces" })
@SpringBootApplication(scanBasePackages = { "com.waben.option" }, exclude = { JtaAutoConfiguration.class })
public class NoneThirdpartyApplication {

	public static void main(String[] args) {
		SpringApplication.run(NoneThirdpartyApplication.class, args);
	}

}
