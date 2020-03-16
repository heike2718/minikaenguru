// =====================================================
// Project: mk-kataloge-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge_admin.persistence.error;

/**
 * KatalogeAdminRuntimeException
 */
public class KatalogeAdminRuntimeException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public KatalogeAdminRuntimeException(final String message, final Throwable cause) {

		super(message, cause);

	}

	/**
	 * @param message
	 */
	public KatalogeAdminRuntimeException(final String message) {

		super(message);

	}

}
