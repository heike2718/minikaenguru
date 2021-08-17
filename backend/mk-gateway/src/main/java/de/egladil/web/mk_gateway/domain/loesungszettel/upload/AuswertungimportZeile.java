// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

/**
 * AuswertungimportZeile
 */
public class AuswertungimportZeile {

	private int index;

	private String fehlermeldung;

	private String rohdaten;

	public int getIndex() {

		return index;
	}

	public AuswertungimportZeile withIndex(final int index) {

		this.index = index;
		return this;
	}

	public String getFehlermeldung() {

		return fehlermeldung;
	}

	public void setFehlermeldung(final String fehlermeldung) {

		this.fehlermeldung = fehlermeldung;
	}

	public String getRohdaten() {

		return rohdaten;
	}

	public AuswertungimportZeile withRohdaten(final String rohdaten) {

		this.rohdaten = rohdaten;
		return this;
	}

	public String getFehlerreportItem() {

		return "Fehler Zeile " + this.index + "! [" + this.fehlermeldung + "] (Rohdaten=" + this.rohdaten + ")";

	}

	@Override
	public String toString() {

		return "[index=" + index + ", rohdaten=" + rohdaten + "]";
	}
}
