// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.aufgaben;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * StatistikAufgabe
 */
@Schema(description = "Statistik zu einer Aufgabe")
public class StatistikAufgabe {

	@JsonProperty
	@Schema(description = "Anzahl richtiger Lösungen", example = "245")
	private long anzahlRichtig;

	@JsonProperty
	@Schema(description = "Anzahl falscher Lösungen", example = "524")
	private long anzahlFalsch;

	@JsonProperty
	@Schema(description = "Anzahl Aufgabe ausgelassen", example = "102")
	private long anzahlNicht;

	public long getAnzahlRichtig() {

		return anzahlRichtig;
	}

	public void setAnzahlRichtig(final long anzahlRichtig) {

		this.anzahlRichtig = anzahlRichtig;
	}

	public long getAnzahlFalsch() {

		return anzahlFalsch;
	}

	public void setAnzahlFalsch(final long anzahlFalsch) {

		this.anzahlFalsch = anzahlFalsch;
	}

	public long getAnzahlNicht() {

		return anzahlNicht;
	}

	public void setAnzahlNicht(final long anzahlNicht) {

		this.anzahlNicht = anzahlNicht;
	}

}
