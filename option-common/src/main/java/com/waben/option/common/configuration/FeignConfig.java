package com.waben.option.common.configuration;

import org.springframework.context.annotation.Configuration;

import com.waben.option.common.component.LocaleContext;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Configuration
public class FeignConfig implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		String locale = LocaleContext.get();
		template.header("locale", locale);
	}

}
