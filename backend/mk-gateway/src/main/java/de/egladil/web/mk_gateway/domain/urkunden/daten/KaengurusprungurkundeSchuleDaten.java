// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.daten;

import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.urkunden.Urkundenmotiv;

/**
 * KaengurusprungurkundeSchuleDaten
 */
public class KaengurusprungurkundeSchuleDaten extends AbstractDatenUrkunde {

	private final String value;

	public KaengurusprungurkundeSchuleDaten(final int value, final Schulteilnahme schulteilnahme, final Klasse klasse) {

		this.value = "" + value;
		this.setNameSchule(schulteilnahme.nameSchule());
		this.setNameKlasse(klasse.name());
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

	public KaengurusprungurkundeSchuleDaten withWettbewerbsjahr(final String wettbewerbsjahr) {

		this.setWettbewerbsjahr(wettbewerbsjahr);
		return this;
	}
}
