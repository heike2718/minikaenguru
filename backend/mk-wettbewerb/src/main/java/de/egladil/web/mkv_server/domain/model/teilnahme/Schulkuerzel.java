// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server.domain.model.teilnahme;

/**
 * Schulkuerzel
 */
public class Schulkuerzel extends Teilnahmekuerzel {

	/**
	 * @param kuerzel
	 */
	public Schulkuerzel(final String kuerzel) {

		super(kuerzel);

	}

	@Override
	protected String classIdentifier() {

		return Schulkuerzel.class.getSimpleName();
	}

}
