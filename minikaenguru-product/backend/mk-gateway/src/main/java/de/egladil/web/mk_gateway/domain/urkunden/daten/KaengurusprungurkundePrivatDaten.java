// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.daten;

import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.urkunden.Urkundenmotiv;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.FontSizeAndLines;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.SplitSchulnameStrategie;

/**
 * KaengurusprungurkundePrivatDaten
 */
public class KaengurusprungurkundePrivatDaten extends AbstractDatenUrkunde {

	private final String value;

	public KaengurusprungurkundePrivatDaten(final Loesungszettel loesungszettel) {

		this.value = "" + loesungszettel.laengeKaengurusprung();
		this.setFontSizeAndLinesSchulname(new FontSizeAndLines(new SplitSchulnameStrategie().getMaxFontSizeAbbreviatedText()));
		this.setKlassenstufe(loesungszettel.klassenstufe());
		this.setNameKlasse(loesungszettel.klassenstufe().getLabel());
		this.setUuid(loesungszettel.kindID().identifier());
		this.setSprache(loesungszettel.sprache());
		this.setWettbewerbsjahr(loesungszettel.teilnahmeIdentifier().wettbewerbID());
	}

	@Override
	public String punktvalue() {

		return value;
	}

	public KaengurusprungurkundePrivatDaten withFullName(final String fullName) {

		this.setFullName(fullName);
		return this;
	}

	public KaengurusprungurkundePrivatDaten withDatum(final String datum) {

		this.setDatum(datum);
		return this;
	}

	public KaengurusprungurkundePrivatDaten withUrkundenmotiv(final Urkundenmotiv urkundenmotiv) {

		this.setUrkundenmotiv(urkundenmotiv);
		return this;
	}
}
