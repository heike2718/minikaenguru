// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.daten;

import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.urkunden.Urkundenmotiv;

/**
 * TeilnahmeurkundeSchuleDaten
 */
public class TeilnahmeurkundeSchuleDaten extends AbstractDatenUrkunde {

	private final String value;

	public TeilnahmeurkundeSchuleDaten(final String value, final Schulteilnahme schulteilnahme, final Klasse klasse) {

		this.value = value;
		this.setNameSchule(schulteilnahme.nameSchule());
		this.setNameKlasse(klasse.name());
	}

	@Override
	public String punktvalue() {

		return value;
	}

	public TeilnahmeurkundeSchuleDaten withFullName(final String fullName) {

		this.setFullName(fullName);
		return this;
	}

	public TeilnahmeurkundeSchuleDaten withDatum(final String datum) {

		this.setDatum(datum);
		return this;
	}

	public TeilnahmeurkundeSchuleDaten withUrkundenmotiv(final Urkundenmotiv urkundenmotiv) {

		this.setUrkundenmotiv(urkundenmotiv);
		return this;
	}

	public TeilnahmeurkundeSchuleDaten withWettbewerbsjahr(final String wettbewerbsjahr) {

		this.setWettbewerbsjahr(wettbewerbsjahr);
		return this;
	}

}