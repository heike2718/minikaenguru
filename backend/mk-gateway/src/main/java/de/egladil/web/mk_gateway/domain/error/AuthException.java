// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.error;

/**
 * AuthException
 */
public class AuthException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public AuthException() {

	}

	/**
	 * @param message
	 */
	public AuthException(final String message) {

		super(message);
	}

}
