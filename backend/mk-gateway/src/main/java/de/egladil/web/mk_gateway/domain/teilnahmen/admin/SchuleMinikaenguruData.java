// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleDetails;

/**
 * SchuleMinikaenguruData
 */
public class SchuleMinikaenguruData {

	@JsonProperty
	private boolean aktuellAngemeldet;

	@JsonProperty
	private String kollegen;

	@JsonProperty
	private int anzahlTeilnahmen;

	@JsonProperty
	private boolean hatAdv;

	public static SchuleMinikaenguruData createFromSchuleDetails(final SchuleDetails details) {

		SchuleMinikaenguruData result = new SchuleMinikaenguruData();
		result.aktuellAngemeldet = details.aktuelleTeilnahme() != null;
		result.anzahlTeilnahmen = details.anzahlTeilnahmen();
		result.hatAdv = details.hatAdv();
		result.kollegen = details.kollegen();
		return result;
	}

	public boolean aktuellAngemeldet() {

		return aktuellAngemeldet;
	}

	public SchuleMinikaenguruData withAktuellAngemeldet(final boolean aktuellAngemeldet) {

		this.aktuellAngemeldet = aktuellAngemeldet;
		return this;
	}

	public String kollegen() {

		return kollegen;
	}

	public SchuleMinikaenguruData withKollegen(final String kollegen) {

		this.kollegen = kollegen;
		return this;
	}

	public int anzahlTeilnahmen() {

		return anzahlTeilnahmen;
	}

	public SchuleMinikaenguruData withAnzahlTeilnahmen(final int anzahlTeilnahmen) {

		this.anzahlTeilnahmen = anzahlTeilnahmen;
		return this;
	}

	public boolean hatAdv() {

		return hatAdv;
	}

	public SchuleMinikaenguruData withHatAdv(final boolean hatAdv) {

		this.hatAdv = hatAdv;
		return this;
	}
}
