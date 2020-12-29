// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.daten;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.urkunden.Urkundenmotiv;
import de.egladil.web.mk_gateway.domain.urkunden.generator.FontSizeAndLines;
import de.egladil.web.mk_gateway.domain.urkunden.generator.SplitSchulnameStrategie;

/**
 * KaengurusprungurkundePrivatDaten
 */
public class KaengurusprungurkundePrivatDaten extends AbstractDatenUrkunde {

	private final String value;

	public KaengurusprungurkundePrivatDaten(final int value, final Klassenstufe klassenstufe) {

		this.value = "" + value;
		this.setFontSizeAndLinesSchulname(new FontSizeAndLines(new SplitSchulnameStrategie().getMaxFontSizeAbbreviatedText()));
		this.setNameKlasse(klassenstufe.getLabel());
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

	public KaengurusprungurkundePrivatDaten withWettbewerbsjahr(final String wettbewerbsjahr) {

		this.setWettbewerbsjahr(wettbewerbsjahr);
		return this;
	}
}
