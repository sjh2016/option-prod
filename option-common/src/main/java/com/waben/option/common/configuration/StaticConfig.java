package com.waben.option.common.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.waben.option.common.model.enums.CurrencyEnum;

@Component
public class StaticConfig {

	@Value("${isContract:false}")
	private boolean isContract;

	@Value("${defaultCurrency:NGN}")
	private String defaultCurrency;

	public boolean isContract() {
		return isContract;
	}

	public CurrencyEnum getDefaultCurrency() {
		return CurrencyEnum.valueOf(defaultCurrency);
	}

}
