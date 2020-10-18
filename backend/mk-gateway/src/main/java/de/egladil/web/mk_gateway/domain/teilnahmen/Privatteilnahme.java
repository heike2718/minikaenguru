// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.semantik.AggregateRoot;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * Privatteilnahme
 */
@AggregateRoot
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

		return "Privatteilnahme [wettbewerbID=" + wettbewerbID().toString() + ", teilnahmekuerzel="
			+ teilnahmenummer().toString() + "]";
	}

}
