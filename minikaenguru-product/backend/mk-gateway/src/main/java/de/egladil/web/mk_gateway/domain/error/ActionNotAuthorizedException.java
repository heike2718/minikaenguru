// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.error;

/**
 * ActionNotAuthorizedException
 */
public class ActionNotAuthorizedException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public ActionNotAuthorizedException(final String message) {

		super(message);

	}

}
