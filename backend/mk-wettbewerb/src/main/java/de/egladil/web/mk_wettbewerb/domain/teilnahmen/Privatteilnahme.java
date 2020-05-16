// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.semantik.Aggregate;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbID;

/**
 * Privatteilnahme
 */
@Aggregate
public class Privatteilnahme extends Teilnahme {

	/**
	 * @param wettbewerbID
	 * @param teilnahmenummer
	 */
	public Privatteilnahme(final WettbewerbID wettbewerbID, final Identifier teilnahmenummer) {

		super(wettbewerbID, teilnahmenummer);

	}

	@Override
	public Teilnahmeart teilnahmeart() {

		return Teilnahmeart.PRIVAT;
	}

	@Override
	public String toString() {

		return "Privatteilnahme [wettbewerbID()=" + wettbewerbID().toString() + ", teilnahmekuerzel()="
			+ teilnahmenummer().toString() + "]";
	}

}
