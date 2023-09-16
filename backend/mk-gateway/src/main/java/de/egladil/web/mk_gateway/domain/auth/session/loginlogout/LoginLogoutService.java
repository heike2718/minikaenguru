// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.session.loginlogout;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import de.egladil.web.mk_gateway.domain.auth.AuthMode;
import de.egladil.web.mk_gateway.domain.auth.AuthResult;

/**
 * LoginLogoutService
 */
public interface LoginLogoutService {

	/**
	 * Tauscht das authToken gegen ein JWT und erzeugt eine Session.<br>
	 * <br>
	 * <strong>Hinweis:</strong> Das wäre eleganter mit CDI Qualifiers, saber momentan (01.09.2020) werden Qualifiers anscheinend
	 * von Quarus ignoriert. Daher ist das sehr häßlich geworkarounded.
	 *
	 * @param  authResult
	 * @param  authMode
	 *                    AuthMode
	 * @return            Response
	 */
	Response login(final AuthResult authResult, AuthMode authMode);

	/**
	 * Logout in Prod, also mit Cookies.
	 *
	 * @param  sessionId
	 * @return           Response
	 */
	Response logout(final String sessionId);

	/**
	 * Logout in Dev, also mit sessionId-Header
	 *
	 * @param  sessionId
	 * @return           Response
	 */
	Response logoutDev(@PathParam(value = "sessionId") final String sessionId);

}
