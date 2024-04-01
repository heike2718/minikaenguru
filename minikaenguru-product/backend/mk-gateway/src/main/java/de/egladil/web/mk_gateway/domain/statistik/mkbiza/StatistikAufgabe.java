// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.mkbiza;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * StatistikAufgabe
 */
public class StatistikAufgabe {

	@JsonProperty
	private long anzahlRichtig;

	@JsonProperty
	private long anzahlFalsch;

	@JsonProperty
	private long anzahlNicht;

	public long getAnzahlRichtig() {

		return anzahlRichtig;
	}

	public StatistikAufgabe withAnzahlRichtig(final long anzahlRichtig) {

		this.anzahlRichtig = anzahlRichtig;
		return this;
	}

	public long getAnzahlFalsch() {

		return anzahlFalsch;
	}

	public StatistikAufgabe withAnzahlFalsch(final long anzahlFalsch) {

		this.anzahlFalsch = anzahlFalsch;
		return this;
	}

	public long getAnzahlNicht() {

		return anzahlNicht;
	}

	public StatistikAufgabe withAnzahlNicht(final long anzahlNicht) {

		this.anzahlNicht = anzahlNicht;
		return this;
	}

}
