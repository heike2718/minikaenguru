// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.model.teilnahmen;

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
