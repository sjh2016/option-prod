package com.waben.option.core.configuration;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import com.waben.option.common.model.enums.TransactionEnum;

@RefreshScope
@Component
public class RefreshConfig {

	@Value("${registerEmailAllow:}")
	private String registerEmailAllow;

	@Value("${blackAllowFlowType:}")
	private String blackAllowFlowType;

	public boolean checkFlowAllow(TransactionEnum type, Boolean isBlack) {
		if (isBlack != null && isBlack && !StringUtils.isBlank(blackAllowFlowType)) {
			List<String> typeList = Arrays.asList(blackAllowFlowType.trim().split(","));
			if (typeList.contains(type.name())) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

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
