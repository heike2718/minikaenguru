// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel.veranstalter;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.apimodel.teilnahmen.PrivatteilnahmeAPIModel;

/**
 * PrivatveranstalterAPIModel
 */
public class PrivatveranstalterAPIModel {

	@JsonProperty
	private String teilnahmenummer;

	@JsonProperty
	private boolean hatZugangZuUnterlangen;

	@JsonProperty
	private int anzahlTeilnahmen = 0;

	@JsonProperty
	private boolean aktuellAngemeldet;

	@JsonProperty
	private PrivatteilnahmeAPIModel aktuelleTeilnahme;

	public static PrivatveranstalterAPIModel create(final boolean zugangZuUnterlagen) {

		PrivatveranstalterAPIModel result = new PrivatveranstalterAPIModel();
		result.hatZugangZuUnterlangen = zugangZuUnterlagen;
		return result;
	}

	PrivatveranstalterAPIModel() {

	}

	public PrivatveranstalterAPIModel withAnzahlTeilnahmen(final int anzahlTeilnahmen) {

		this.anzahlTeilnahmen = anzahlTeilnahmen;
		return this;
	}

	public PrivatveranstalterAPIModel withTeilnahme(final PrivatteilnahmeAPIModel aktuelleTeilnahme) {

		this.aktuelleTeilnahme = aktuelleTeilnahme;
		this.aktuellAngemeldet = aktuelleTeilnahme != null;
		return this;
	}

	public boolean hatZugangZuUnterlangen() {

		return hatZugangZuUnterlangen;
	}

	public int anzahlVergangeneTeilnahmen() {

		return anzahlTeilnahmen;
	}

	public boolean aktuellAngemeldet() {

		return aktuellAngemeldet;
	}

	public PrivatteilnahmeAPIModel aktuelleTeilnahme() {

		return aktuelleTeilnahme;
	}

	public String teilnahmenummer() {

		return teilnahmenummer;
	}

	public PrivatveranstalterAPIModel withTeilnahmenummer(final String teilnahmenummer) {

		this.teilnahmenummer = teilnahmenummer;
		return this;
	}
}
