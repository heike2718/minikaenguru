// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.model.teilnahmen;

import de.egladil.web.mk_wettbewerb.domain.model.wettbewerb.WettbewerbID;

/**
 * Privatteilnahme
 */
public class Privatteilnahme extends Teilnahme {

	/**
	 * @param wettbewerbID
	 * @param teilnahmekuerzel
	 */
	public Privatteilnahme(final WettbewerbID wettbewerbID, final Teilnahmekuerzel teilnahmekuerzel) {

		super(wettbewerbID, teilnahmekuerzel);

	}

	@Override
	protected boolean teilnahmekuerzelErlaubt(final Teilnahmekuerzel teilnahmekuerzel) {

		return teilnahmekuerzel instanceof Privatteilnahmekuerzel;
	}

	@Override
	public String toString() {

		return "Privatteilnahme [wettbewerbID()=" + wettbewerbID().toString() + ", teilnahmekuerzel()="
			+ teilnahmekuerzel().toString() + "]";
	}

}
