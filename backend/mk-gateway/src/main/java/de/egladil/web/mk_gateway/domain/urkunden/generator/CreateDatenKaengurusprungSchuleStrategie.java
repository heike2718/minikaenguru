// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator;

import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.urkunden.Urkundenmotiv;
import de.egladil.web.mk_gateway.domain.urkunden.daten.AbstractDatenUrkunde;
import de.egladil.web.mk_gateway.domain.urkunden.daten.KaengurusprungurkundeSchuleDaten;

/**
 * CreateDatenKaengurusprungSchuleStrategie
 */
public class CreateDatenKaengurusprungSchuleStrategie implements CreateDatenUrkundeStrategy {

	private final Schulteilnahme schulteilnahme;

	private final Klasse klasse;

	public CreateDatenKaengurusprungSchuleStrategie(final Schulteilnahme schulteilnahme, final Klasse klasse) {

		this.schulteilnahme = schulteilnahme;
		this.klasse = klasse;
	}

	@Override
	public AbstractDatenUrkunde createDatenUrkunde(final Kind kind, final Loesungszettel loesungszettel, final String datum, final Urkundenmotiv urkundenmotiv) {

		return new KaengurusprungurkundeSchuleDaten(loesungszettel.laengeKaengurusprung(), schulteilnahme, klasse)
			.withDatum(datum)
			.withUrkundenmotiv(urkundenmotiv)
			.withFullName(kind.nameUrkunde())
			.withWettbewerbsjahr(loesungszettel.teilnahmeIdentifier().wettbewerbID());
	}

}
