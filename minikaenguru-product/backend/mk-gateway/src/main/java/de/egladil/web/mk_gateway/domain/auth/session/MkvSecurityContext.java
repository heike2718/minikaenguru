// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.session;

import java.security.Principal;

import jakarta.ws.rs.core.SecurityContext;

/**
 * MkvSecurityContext
 */
public class MkvSecurityContext implements SecurityContext {

	private final Session session;

	private final boolean secure;

	/**
	 * @param session
	 */
	public MkvSecurityContext(final Session session, final boolean secure) {

		if (session == null) {

			throw new IllegalArgumentException("session darf nicht null sein");
		}

		if (session.user() == null) {

			throw new IllegalStateException("session darf nicht anonym sein");
		}

		this.session = session;
		this.secure = secure;
	}

	@Override
	public Principal getUserPrincipal() {

		return session.user();
	}

	@Override
	public boolean isUserInRole(final String role) {

		LoggedInUser user = session.user();
		String rolle = user.rolle().toString();

		boolean result = rolle.equalsIgnoreCase(role);

		return result;
	}

	@Override
	public boolean isSecure() {

		return this.secure;
	}

	@Override
	public String getAuthenticationScheme() {

		return "Bearer";
	}

}
