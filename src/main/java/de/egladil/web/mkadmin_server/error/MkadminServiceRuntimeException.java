// =====================================================
// Project: mkadmin-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkadmin_server.error;

/**
 * MkadminServiceRuntimeException
 */
public class MkadminServiceRuntimeException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public MkadminServiceRuntimeException(final String message, final Throwable cause) {

		super(message, cause);
	}

	/**
	 * @param message
	 */
	public MkadminServiceRuntimeException(final String message) {

		super(message);
	}

}
