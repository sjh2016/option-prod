package com.waben.option;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.waben.option.common.web.socket.TcpApplication;

import java.time.ZoneId;
import java.util.TimeZone;

@EnableDiscoveryClient
@AutoConfigurationPackage
@EnableFeignClients(basePackages = { "com.waben.option.common.interfaces", "com.waben.option.common.interfacesadmin" })
@SpringBootApplication(scanBasePackages = { "com.waben.option" }, exclude = { JtaAutoConfiguration.class })
public class NoneAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(NoneAdminApplication.class);
		TcpApplication.run(NoneAdminApplication.class);
	}

}
