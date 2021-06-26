// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.kinder.api.KlasseAPIModel;

/**
 * KlassenlisteImportReport
 */
public class KlassenlisteImportReport {

	@JsonProperty
	private int anzahlKlassen;

	@JsonProperty
	private int anzahlKinderImportiert;

	@JsonProperty
	private long anzahlNichtImportiert;

	@JsonProperty
	private long anzahlKlassenstufeUnklar;

	@JsonProperty
	private long anzahlDubletten;

	@JsonProperty
	private String uuidImportReport;

	@JsonProperty
	private List<KlasseAPIModel> klassen;

	public String getUuidImportReport() {

		return uuidImportReport;
	}

	public List<KlasseAPIModel> getKlassen() {

		return klassen;
	}

	public KlassenlisteImportReport withKlassen(final List<KlasseAPIModel> klassen) {

		this.klassen = klassen;
		return this;
	}

	public void setUuidImportReport(final String uuidImportReport) {

		this.uuidImportReport = uuidImportReport;
	}

	public long getAnzahlNichtImportiert() {

		return anzahlNichtImportiert;
	}

	public KlassenlisteImportReport withAnzahlNichtImportiert(final long anzahlNichtImportiert) {

		this.anzahlNichtImportiert = anzahlNichtImportiert;
		return this;
	}

	public long getAnzahlKlassenstufeUnklar() {

		return anzahlKlassenstufeUnklar;
	}

	public KlassenlisteImportReport withAnzahlKlassenstufeUnklar(final long anzahlKlassenstufeUnklar) {

		this.anzahlKlassenstufeUnklar = anzahlKlassenstufeUnklar;
		return this;
	}

	public long getAnzahlDubletten() {

		return anzahlDubletten;
	}

	public KlassenlisteImportReport withAnzahlDubletten(final long anzahlDubletten) {

		this.anzahlDubletten = anzahlDubletten;
		return this;
	}

	public int getAnzahlKinderImportiert() {

		return anzahlKinderImportiert;
	}

	public KlassenlisteImportReport withAnzahlKinderImportiert(final int anzahlKinderImportiert) {

		this.anzahlKinderImportiert = anzahlKinderImportiert;
		return this;
	}

	public int getAnzahlKlassen() {

		return anzahlKlassen;
	}

	public KlassenlisteImportReport withAnzahlKlassen(final int anzahlKlassen) {

		this.anzahlKlassen = anzahlKlassen;
		return this;
	}

}
