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
 * TeilnahmeurkundePrivatDaten
 */
public class TeilnahmeurkundePrivatDaten extends AbstractDatenUrkunde {

	private final String value;

	public TeilnahmeurkundePrivatDaten(final Loesungszettel loesungszettel) {

		this.value = loesungszettel.punkteAsString();
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

	public TeilnahmeurkundePrivatDaten withFullName(final String fullName) {

		this.setFullName(fullName);
		return this;
	}

	public TeilnahmeurkundePrivatDaten withDatum(final String datum) {

		this.setDatum(datum);
		return this;
	}

	public TeilnahmeurkundePrivatDaten withUrkundenmotiv(final Urkundenmotiv urkundenmotiv) {

		this.setUrkundenmotiv(urkundenmotiv);
		return this;
	}
}
