// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator;

import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.urkunden.Urkundenmotiv;
import de.egladil.web.mk_gateway.domain.urkunden.daten.AbstractDatenUrkunde;
import de.egladil.web.mk_gateway.domain.urkunden.daten.KaengurusprungurkundeSchuleDaten;

/**
 * CreateDatenKaengurusprungSchuleStrategie
 */
public class CreateDatenKaengurusprungSchuleStrategie implements CreateDatenUrkundeStrategy {

	private final FontSizeAndLines fontSizeAndLinesSchulname;

	private final Klasse klasse;

	public CreateDatenKaengurusprungSchuleStrategie(final FontSizeAndLines fontSizeAndLinesSchulname, final Klasse klasse) {

		this.fontSizeAndLinesSchulname = fontSizeAndLinesSchulname;
		this.klasse = klasse;
	}

	@Override
	public AbstractDatenUrkunde createDatenUrkunde(final Kind kind, final Loesungszettel loesungszettel, final String datum, final Urkundenmotiv urkundenmotiv) {

		String theDatum = kind.sprache() == Sprache.en ? datum.replaceAll("\\.", "/") : datum;

		return new KaengurusprungurkundeSchuleDaten(loesungszettel.laengeKaengurusprung(), klasse)
			.withDatum(theDatum)
			.withUrkundenmotiv(urkundenmotiv)
			.withFullName(kind.nameUrkunde())
			.withFontsizeAndLinesSchulname(fontSizeAndLinesSchulname)
			.withWettbewerbsjahr(loesungszettel.teilnahmeIdentifier().wettbewerbID());
	}

}
