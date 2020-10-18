// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.semantik.AggregateRoot;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * Schulteilnahme
 */
@AggregateRoot
public class Schulteilnahme extends Teilnahme {

	private final String nameSchule;

	private final Identifier angemeldetDurchVeranstalterId;

	/**
	 * @param wettbewerbID
	 * @param teilnahmenummer
	 */
	public Schulteilnahme(final WettbewerbID wettbewerbID, final Identifier teilnahmenummer, final String nameSchule, final Identifier veranstalterId) {

		super(wettbewerbID, teilnahmenummer);

		if (StringUtils.isBlank(nameSchule)) {

			throw new IllegalArgumentException("nameSchule darf nicht blank sein");
		}

		this.nameSchule = nameSchule;
		this.angemeldetDurchVeranstalterId = veranstalterId;
	}

	@Override
	public Teilnahmeart teilnahmeart() {

		return Teilnahmeart.SCHULE;
	}

	public String nameSchule() {

		return this.nameSchule;
	}

	@Override
	public String toString() {

		return "Schulteilnahme [wettbewerbID=" + wettbewerbID().toString() + ", teilnahmekuerzel="
			+ teilnahmenummer().toString() + ", name=" + this.nameSchule + ", angemeldetDurch=" + angemeldetDurchVeranstalterId
			+ "]";
	}

	public Identifier angemeldetDurchVeranstalterId() {

		return angemeldetDurchVeranstalterId;
	}
}
