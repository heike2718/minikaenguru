// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.error;

/**
 * ImmutableObjectException
 */
public class ImmutableObjectException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public ImmutableObjectException(final String message) {

		super(message);

	}

}
