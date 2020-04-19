// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.model.teilnahmen;

import de.egladil.web.mk_wettbewerb.domain.model.Identifier;
import de.egladil.web.mk_wettbewerb.domain.model.wettbewerb.WettbewerbID;

/**
 * Privatteilnahme
 */
public class Privatteilnahme extends Teilnahme {

	/**
	 * @param wettbewerbID
	 * @param teilnahmekuerzel
	 */
	public Privatteilnahme(final WettbewerbID wettbewerbID, final Identifier teilnahmekuerzel) {

		super(wettbewerbID, teilnahmekuerzel);

	}

	@Override
	public String toString() {

		return "Privatteilnahme [wettbewerbID()=" + wettbewerbID().toString() + ", teilnahmekuerzel()="
			+ teilnahmekuerzel().toString() + "]";
	}

}
