// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.domain.exeptions;

/**
 * MkBiZaCommunicationExcepion ist eine WrapperException für Dinge, die bei der REST-Kommunikation auftreten können.
 */
public class MkBiZaCommunicationExcepion extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MkBiZaCommunicationExcepion(final String message, final Throwable cause) {

		super(message, cause);

	}

}
