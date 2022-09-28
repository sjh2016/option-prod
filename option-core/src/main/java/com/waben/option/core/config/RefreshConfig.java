package com.waben.option.core.config;

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
	/**
	 * 是否自动领取赠送订单
	 */
	@Value("${autoGiveOrder:true}")
	private boolean autoGiveOrder;
	/**
	 * 邀请数量门槛
	 */
	@Value("${inviteThreshold:100}")
	private Integer inviteThreshold;
	/**
	 * 邀请数量达到门槛后，归属平台的概率，-1~100
	 */
	@Value("${inviteHiddenValue:-1}")
	private Integer inviteHiddenValue;
	/**
	 * 同一个邀请链接，相同ip注册最小时间间隔（分钟）
	 */
	@Value("${inviteIpInterval:60}")
	private Integer inviteIpInterval;

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

	public boolean isAutoGiveOrder() {
		return autoGiveOrder;
	}

	public Integer getInviteThreshold() {
		return inviteThreshold;
	}

	public Integer getInviteHiddenValue() {
		return inviteHiddenValue;
	}

	public Integer getInviteIpInterval() {
		return inviteIpInterval;
	}

}
