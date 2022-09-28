package com.waben.option.common.configuration;

import com.waben.option.common.configuration.properties.WebConfigProperties;
import com.waben.option.common.service.JwtService;
import com.waben.option.common.web.controller.filter.CorsFilter;
import com.waben.option.common.web.controller.filter.jwt.JwtAuthenticationFilter;
import com.waben.option.common.web.controller.provider.JwtAuthenticationProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnProperty(name = "web.config.enable", matchIfMissing = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Resource
	private JwtService jwtService;
	
	@Resource
	private AuthenticationEntryPoint unauthenticationEntryPoint;

	@Resource
	private WebConfigProperties webConfigProperties;
	
	@Bean(name = "jwtAuthenticationProvider")
	public JwtAuthenticationProvider jwtAuthenticationProvider() {
		final JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider();
		authenticationProvider.setJwtService(jwtService);
		return authenticationProvider;
	}

	@Bean
	public JwtService jwtService() {
		return new JwtService();
	}
	
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter();
	}

	@Bean
	public Converter<String, String> StringConvert() {
		return new Converter<String, String>() {
			@Override
			public String convert(String source) {
				return StringUtils.trimToNull(source);
			}
		};
	}

	@Bean
	public Converter<String, LocalDate> LocalDateConvert() {
		return new Converter<String, LocalDate>() {
			@Override
			public LocalDate convert(String source) {
				if (StringUtils.isBlank(source)) {
					return null;
				}
				return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			}

		};
	}

	@Bean
	public Converter<String, LocalDateTime> LocalDateTimeConvert() {
		return new Converter<String, LocalDateTime>() {
			@Override
			public LocalDateTime convert(String source) {
				if (StringUtils.isBlank(source)) {
					return null;
				}
				return LocalDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			}

		};
	}

	@Bean
	public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration(JwtAuthenticationFilter filter) {
		FilterRegistrationBean<JwtAuthenticationFilter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(filter);
		filterRegistrationBean.setEnabled(false);
		filterRegistrationBean.setOrder(1);
		return filterRegistrationBean;
	}

	@Bean
	public CorsFilter corsFilterFilter() {
		return new CorsFilter();
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.authenticationProvider(jwtAuthenticationProvider());
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
	}
    
	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.exceptionHandling().authenticationEntryPoint(this.unauthenticationEntryPoint);
		http.csrf().disable();
		http.headers().frameOptions().disable();
		if(webConfigProperties.isAuthServer()) {
			http.addFilterBefore(jwtAuthenticationFilter(), BasicAuthenticationFilter.class);
			List<String> anonList = webConfigProperties.getAnon();
			for(String anon : anonList) {
				http.authorizeRequests().antMatchers(anon).permitAll();
			}
			http.authorizeRequests().antMatchers("/**").authenticated();
		} else {
			http.authorizeRequests().antMatchers("/**").permitAll();
		}
	}
	
}
