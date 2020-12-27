// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator;

import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.urkunden.Urkundenmotiv;
import de.egladil.web.mk_gateway.domain.urkunden.daten.AbstractDatenUrkunde;
import de.egladil.web.mk_gateway.domain.urkunden.daten.TeilnahmeurkundePrivatDaten;

/**
 * CreateDatenTeilnahmePrivatStrategie
 */
public class CreateDatenTeilnahmePrivatStrategie implements CreateDatenUrkundeStrategy {

	@Override
	public AbstractDatenUrkunde createDatenUrkunde(final Kind kind, final Loesungszettel loesungszettel, final String datum, final Urkundenmotiv urkundenmotiv) {

		return new TeilnahmeurkundePrivatDaten(loesungszettel.punkteAsString(), loesungszettel.klassenstufe())
			.withDatum(datum)
			.withUrkundenmotiv(urkundenmotiv)
			.withFullName(kind.nameUrkunde())
			.withWettbewerbsjahr(loesungszettel.teilnahmeIdentifier().wettbewerbID());
	}

}
