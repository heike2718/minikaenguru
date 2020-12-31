// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.daten;

import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.urkunden.Urkundenmotiv;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.FontSizeAndLines;

/**
 * KaengurusprungurkundeSchuleDaten
 */
public class KaengurusprungurkundeSchuleDaten extends AbstractDatenUrkunde {

	private final String value;

	public KaengurusprungurkundeSchuleDaten(final Loesungszettel loesungszettel, final Klasse klasse) {

		this.value = "" + loesungszettel.laengeKaengurusprung();
		this.setNameKlasse(klasse.name());
		this.setKlasseUUID(klasse.identifier().identifier());
		this.setKlassenstufe(loesungszettel.klassenstufe());
		this.setSprache(loesungszettel.sprache());
		this.setUuid(loesungszettel.kindID().identifier());
		this.setWettbewerbsjahr(loesungszettel.teilnahmeIdentifier().wettbewerbID());

	}

	@Override
	public String punktvalue() {

		return value;
	}

	public KaengurusprungurkundeSchuleDaten withFullName(final String fullName) {

		this.setFullName(fullName);
		return this;
	}

	public KaengurusprungurkundeSchuleDaten withDatum(final String datum) {

		this.setDatum(datum);
		return this;
	}

	public KaengurusprungurkundeSchuleDaten withUrkundenmotiv(final Urkundenmotiv urkundenmotiv) {

		this.setUrkundenmotiv(urkundenmotiv);
		return this;
	}

	public KaengurusprungurkundeSchuleDaten withFontsizeAndLinesSchulname(final FontSizeAndLines fontSizeAndLinesSchulname) {

		this.setFontSizeAndLinesSchulname(fontSizeAndLinesSchulname);
		return this;
	}
}
