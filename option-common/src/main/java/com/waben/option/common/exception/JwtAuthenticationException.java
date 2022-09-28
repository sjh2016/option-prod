package com.waben.option.common.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {

	private static final long serialVersionUID = -8179864866844206328L;

	public JwtAuthenticationException(String msg) {
		super(msg);
	}

	public JwtAuthenticationException(String msg, Throwable t) {
		super(msg, t);
	}

	public JwtAuthenticationException(Exception e) {
		super(e.getMessage(), e);
	}

}
