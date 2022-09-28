package com.waben.option.core.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
public class RefreshConfig {

	@Value("${registerEmailAllow:}")
	private String registerEmailAllow;

	public boolean checkRegisterEmailAllow(String email) {
		if (!StringUtils.isBlank(registerEmailAllow)) {
			boolean result = false;
			String[] allowList = registerEmailAllow.split(",");
			for (String allow : allowList) {
				if (!StringUtils.isBlank(allow) && email.trim().endsWith(allow.trim())) {
					result = true;
					break;
				}
			}
			return result;
		} else {
			return true;
		}
	}

	public String getRegisterEmailAllow() {
		return registerEmailAllow;
	}

	public void setRegisterEmailAllow(String registerEmailAllow) {
		this.registerEmailAllow = registerEmailAllow;
	}

}
