// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.messaging;

/**
 * MkRestException
 */
public class MkRestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MkRestException(final String message) {

		super(message);

	}

}
