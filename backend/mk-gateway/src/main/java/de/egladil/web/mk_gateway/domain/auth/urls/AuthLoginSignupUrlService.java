// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.urls;

import javax.ws.rs.core.Response;

/**
 * AuthLoginSignupUrlService
 */
public interface AuthLoginSignupUrlService {

	/**
	 * @return Response mit der autorisierten Login-Redirect-Url im Payload
	 */
	Response getLoginUrl();

	/**
	 * @return Response mit der autorisierten Signup-Lehrer-Redirect-Url im Payload
	 */
	Response getSignupLehrerUrl(final String schulkuerzel, final String newsletterAbonnieren);

	/**
	 * @return Response mit der autorisierten Signup-Privat-Redirect-Url im Payload
	 */
	Response getSignupPrivatUrl(final String newsletterAbonnieren);

}
