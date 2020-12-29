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
 * TeilnahmeurkundePrivatDaten
 */
public class TeilnahmeurkundePrivatDaten extends AbstractDatenUrkunde {

	private final String value;

	public TeilnahmeurkundePrivatDaten(final String value, final Klassenstufe klassenstufe) {

		this.value = value;
		this.setFontSizeAndLinesSchulname(new FontSizeAndLines(new SplitSchulnameStrategie().getMaxFontSizeAbbreviatedText()));
		this.setNameKlasse(klassenstufe.getLabel());
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

	public TeilnahmeurkundePrivatDaten withWettbewerbsjahr(final String wettbewerbsjahr) {

		this.setWettbewerbsjahr(wettbewerbsjahr);
		return this;
	}

}
