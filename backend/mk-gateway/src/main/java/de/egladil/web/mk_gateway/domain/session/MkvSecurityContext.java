// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.session;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

/**
 * MkvSecurityContext
 */
public class MkvSecurityContext implements SecurityContext {

	private final Session session;

	/**
	 * @param session
	 */
	public MkvSecurityContext(final Session session) {

		this.session = session;
	}

	@Override
	public Principal getUserPrincipal() {

		return session.user();
	}

	@Override
	public boolean isUserInRole(final String role) {

		return false;
	}

	@Override
	public boolean isSecure() {

		return false;
	}

	@Override
	public String getAuthenticationScheme() {

		return null;
	}

}
