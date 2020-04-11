// =====================================================
// Project: mkv-api-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_api_gateway.error;

/**
 * PendingRegistrationException
 */
public class PendingRegistrationException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public PendingRegistrationException() {

	}

	/**
	 * @param message
	 */
	public PendingRegistrationException(final String message) {

		super(message);

	}

}
