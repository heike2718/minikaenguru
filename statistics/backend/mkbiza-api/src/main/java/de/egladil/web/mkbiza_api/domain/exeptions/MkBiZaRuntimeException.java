// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.exeptions;

/**
 * MkBiZaRuntimeException
 */
public class MkBiZaRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MkBiZaRuntimeException(final String message, final Throwable cause) {

		super(message, cause);

	}

	public MkBiZaRuntimeException(final String message) {

		super(message);

	}

}
