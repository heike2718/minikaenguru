// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.error;

/**
 * MessagingAuthException
 */
public class MessagingAuthException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MessagingAuthException() {

	}

	public MessagingAuthException(final String message) {

		super(message);

	}

}
