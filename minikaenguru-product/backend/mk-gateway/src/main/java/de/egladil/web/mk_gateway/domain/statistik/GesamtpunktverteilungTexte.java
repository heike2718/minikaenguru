// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

/**
 * GesamtpunktverteilungTexte
 */
public class GesamtpunktverteilungTexte {

	private static final String DEFAULT_SECTION_TEXT = "Lösungen je Aufgabe";

	private String titel;

	private String basis;

	private String bewertung;

	private String sectionEinzelergebnisse = DEFAULT_SECTION_TEXT;

	public String titel() {

		return titel;
	}

	public GesamtpunktverteilungTexte withTitel(final String titel) {

		this.titel = titel;
		return this;
	}

	public String basis() {

		return basis;
	}

	public GesamtpunktverteilungTexte withBasis(final String basis) {

		this.basis = basis;
		return this;
	}

	public String bewertung() {

		return bewertung;
	}

	public GesamtpunktverteilungTexte withBewertung(final String bewertung) {

		this.bewertung = bewertung;
		return this;
	}

	public String sectionEinzelergebnisse() {

		return sectionEinzelergebnisse;
	}

	public GesamtpunktverteilungTexte withSectionEinzelergebnisse(final String sectionEinzelergebnisse) {

		this.sectionEinzelergebnisse = sectionEinzelergebnisse;
		return this;
	}

}
