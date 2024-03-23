// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden;

import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.urkunden.Urkundenmotiv;
import de.egladil.web.mk_gateway.domain.urkunden.daten.AbstractDatenUrkunde;
import de.egladil.web.mk_gateway.domain.urkunden.daten.KaengurusprungurkundePrivatDaten;

/**
 * CreateDatenKaengurusprungPrivatStrategie
 */
public class CreateDatenKaengurusprungPrivatStrategie implements CreateDatenUrkundeStrategy {

	@Override
	public AbstractDatenUrkunde createDatenUrkunde(final Kind kind, final Loesungszettel loesungszettel, final String datum, final Urkundenmotiv urkundenmotiv) {

		String theDatum = loesungszettel.sprache() == Sprache.en ? datum.replaceAll("\\.", "/") : datum;

		return new KaengurusprungurkundePrivatDaten(loesungszettel)
			.withDatum(theDatum)
			.withUrkundenmotiv(urkundenmotiv)
			.withFullName(kind.nameUrkunde());
	}

}
