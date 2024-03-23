// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.urls;

import jakarta.ws.rs.core.Response;

/**
 * AuthLoginUrlService
 */
public interface AuthLoginUrlService {

	/**
	 * @return Response mit der autorisierten Login-Redirect-Url im Payload
	 */
	Response getLoginUrl();

}
