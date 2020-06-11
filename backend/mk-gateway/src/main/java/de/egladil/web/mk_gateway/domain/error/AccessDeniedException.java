// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.error;

/**
 * AccessDeniedException
 */
public class AccessDeniedException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public AccessDeniedException() {

	}

	public AccessDeniedException(final String message) {

		super(message);

	}

}
