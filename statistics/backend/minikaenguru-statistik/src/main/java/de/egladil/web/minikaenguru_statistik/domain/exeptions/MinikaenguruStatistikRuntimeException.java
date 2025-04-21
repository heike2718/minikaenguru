// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.domain.exeptions;

/**
 * MinikaenguruStatistikRuntimeException
 */
public class MinikaenguruStatistikRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MinikaenguruStatistikRuntimeException(final String message, final Throwable cause) {

		super(message, cause);

	}

	public MinikaenguruStatistikRuntimeException(final String message) {

		super(message);

	}

}
