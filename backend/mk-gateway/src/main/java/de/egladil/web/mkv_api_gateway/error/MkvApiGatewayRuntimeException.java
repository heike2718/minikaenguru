// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_api_gateway.error;

/**
 * MkvApiGatewayRuntimeException
 */
public class MkvApiGatewayRuntimeException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public MkvApiGatewayRuntimeException(final String message, final Throwable cause) {

		super(message, cause);
	}

	/**
	 * @param message
	 */
	public MkvApiGatewayRuntimeException(final String message) {

		super(message);
	}

}
