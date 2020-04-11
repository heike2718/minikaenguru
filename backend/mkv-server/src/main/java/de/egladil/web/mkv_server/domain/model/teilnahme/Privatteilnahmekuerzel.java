// =====================================================
// Project: mkv-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server.domain.model.teilnahme;

/**
 * Privatteilnahmekuerzel
 */
public class Privatteilnahmekuerzel extends Teilnahmekuerzel {

	/**
	 * @param kuerzel
	 */
	public Privatteilnahmekuerzel(final String kuerzel) {

		super(kuerzel);

	}

	@Override
	protected String classIdentifier() {

		return Privatteilnahmekuerzel.class.getSimpleName();
	}

}
