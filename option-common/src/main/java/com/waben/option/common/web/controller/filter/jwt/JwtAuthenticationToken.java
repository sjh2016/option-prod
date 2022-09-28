package com.waben.option.common.web.controller.filter.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

/**
 * Implements the org.springframework.security.core.Authentication interface.
 * The constructor is set with the authentication JWT
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
	
	private JWTAuthenticatedUserPrincipal principal;
	private final String jwt;

	public JwtAuthenticationToken(String jwt) {
		super(null);
		this.jwt = jwt;
		setAuthenticated(false);
	}

	public JwtAuthenticationToken(JWTAuthenticatedUserPrincipal principal, String jwt,
			Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.jwt = jwt;
		super.setAuthenticated(true); // must use super, as we override
	}

	// ~ Methods
	// ========================================================================================================

	public String getCredentials() {
		return this.jwt;
	}

	public JWTAuthenticatedUserPrincipal getPrincipal() {
		return this.principal;
	}

	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		if (isAuthenticated) {
			throw new IllegalArgumentException(
					"Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		}

		super.setAuthenticated(false);
	}
}
