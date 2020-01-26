// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.session;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

/**
 * MkSecurityContext
 */
public class MkSecurityContext implements SecurityContext {

	private final Session session;

	/**
	 * @param session
	 */
	public MkSecurityContext(final Session session) {

		this.session = session;
	}

	@Override
	public Principal getUserPrincipal() {

		return session.getUser();
	}

	@Override
	public boolean isUserInRole(final String role) {

		return session.getUser() != null && session.getUser().getRolle().contains(role);
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
