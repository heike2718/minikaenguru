// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.error;

import jakarta.ws.rs.core.Response;

/**
 * MkGatewayWebApplicationException
 */
public class MkGatewayWebApplicationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final Response response;

	/**
	 * @param response
	 */
	public MkGatewayWebApplicationException(final Response response) {

		super();
		this.response = response;
	}

	public Response getResponse() {

		return response;
	}

}
