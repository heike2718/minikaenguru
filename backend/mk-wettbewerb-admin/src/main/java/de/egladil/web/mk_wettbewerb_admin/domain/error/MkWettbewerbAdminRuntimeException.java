// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.error;

/**
 * MkWettbewerbAdminRuntimeException
 */
public class MkWettbewerbAdminRuntimeException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public MkWettbewerbAdminRuntimeException(final String message, final Throwable cause) {

		super(message, cause);

	}

	/**
	 * @param message
	 */
	public MkWettbewerbAdminRuntimeException(final String message) {

		super(message);

	}

}
