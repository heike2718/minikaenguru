// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.aufgaben;

/**
 * StatistikAufgabe
 */
public class StatistikAufgabe {

	private long anzahlRichtig;

	private long anzahlFalsch;

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
