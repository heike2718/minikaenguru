// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.error;

/**
 * InaccessableEndpointException
 */
public class InaccessableEndpointException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public InaccessableEndpointException(final String message) {

		super(message);

	}

}
