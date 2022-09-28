package com.waben.option.common.web.controller.provider;

import com.waben.option.common.exception.JwtAuthenticationException;
import com.waben.option.common.service.JwtService;
import com.waben.option.common.web.controller.filter.jwt.JWTAuthenticatedUserPrincipal;
import com.waben.option.common.web.controller.filter.jwt.JwtAuthenticationToken;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

/**
 * Class that verifies the JWT token and when valid, it will set the userdetails
 * in the authentication object
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {

	private JwtService jwtService;

	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Assert.isInstanceOf(JwtAuthenticationToken.class, authentication, "Only JwtAuthenticationToken is supported");

		if (authentication.getCredentials() == null) {
			throw new JwtAuthenticationException("Bad jwt credentials");
		}

		Claims claims = jwtService.verify(authentication.getCredentials().toString());
		JWTAuthenticatedUserPrincipal principal = new JWTAuthenticatedUserPrincipal(claims);
		JwtAuthenticationToken result = new JwtAuthenticationToken(principal,
				authentication.getCredentials().toString(), null);
		result.setDetails(authentication.getDetails());
		return result;
	}

	public boolean supports(Class<?> authentication) {
		return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
	}

	public JwtService getJwtService() {
		return jwtService;
	}

	public void setJwtService(JwtService jwtService) {
		this.jwtService = jwtService;
	}
	
}
