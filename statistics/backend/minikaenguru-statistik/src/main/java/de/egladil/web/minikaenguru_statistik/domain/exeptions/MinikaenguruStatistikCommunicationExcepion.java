// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.domain.exeptions;

/**
 * MinikaenguruStatistikCommunicationExcepion ist eine WrapperException für Dinge, die bei der REST-Kommunikation auftreten können.
 */
public class MinikaenguruStatistikCommunicationExcepion extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MinikaenguruStatistikCommunicationExcepion(final String message, final Throwable cause) {

		super(message, cause);

	}

}
