// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.error;

/**
 * MkGatewayRuntimeException
 */
public class MkGatewayRuntimeException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public MkGatewayRuntimeException(final String message, final Throwable cause) {

		super(message, cause);
	}

	/**
	 * @param message
	 */
	public MkGatewayRuntimeException(final String message) {

		super(message);
	}

}
