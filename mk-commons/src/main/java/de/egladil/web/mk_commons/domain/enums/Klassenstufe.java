// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Klassenstufe
 */
public enum Klassenstufe {

	IKID("Inklusion", 0, Arrays.asList(new String[] { "A-1", "A-2", "B-1", "B-2", "C-1", "C-2" })),
	EINS("Klasse 1", 1, Arrays
		.asList(new String[] { "A-1", "A-2", "A-3", "A-4", "B-1", "B-2", "B-3", "B-4", "C-1", "C-2", "C-3", "C-4" })),
	ZWEI("Klasse 2", 2, Arrays.asList(
		new String[] { "A-1", "A-2", "A-3", "A-4", "A-5", "B-1", "B-2", "B-3", "B-4", "B-5", "C-1", "C-2", "C-3", "C-4", "C-5" }));

	private final String label;

	private final int nummer;

	private final List<String> aufgabennummern;

	/**
	 * Erzeugt eine Instanz von Klassenstufe
	 */
	private Klassenstufe(final String label, final int nummer, final List<String> aufgabennummern) {

		this.label = label;
		this.nummer = nummer;
		this.aufgabennummern = aufgabennummern;
	}

	public String getLabel() {

		return label;
	}

	public int getAnzahlAufgaben() {

		return aufgabennummern.size();
	}

	/**
	 * @param  jahr
	 *                                   in den Jahren 2010 - 2013 gab es keine Klassenstufe 1, in den Jahren 2014 - 2016 war
	 *                                   Klassenstufe 1 =
	 *                                   Klassenstufe 2
	 * @return                           List
	 * @throws IllegalArgumentException,
	 *                                   wenn es zur Kombination aus Klassennummer und Jahr keine Auswertung gibt.
	 */
	public List<String> getAufgabennummern(final String jahr) {

		final Integer year = Integer.valueOf(jahr);

		if (nummer == 1 && 2010 <= year && year <= 2013) {

			throw new IllegalArgumentException("Im Jahr " + jahr + " gibt es keine Auswertung für Klassenstufe 1");
		}

		if (nummer == 1 && 2014 <= year && year <= 2016) {

			return Arrays.asList(new String[] { "A-1", "A-2", "A-3", "A-4", "A-5", "B-1", "B-2", "B-3", "B-4", "B-5", "C-1", "C-2",
				"C-3", "C-4", "C-5" });
		}
		return aufgabennummern;
	}

	/**
	 * @param  jahr
	 *              in den Jahren 2010 - 2013 gab es keine Klassenstufe 1, in den Jahren 2014 - 2016 war Klassenstufe 1 =
	 *              Klassenstufe 2
	 * @return      int
	 */
	public int getAnzahlAufgaben(final String jahr) {

		final Integer year = Integer.valueOf(jahr);

		if (nummer == 1 && 2014 <= year && year <= 2016) {

			return 15;
		}
		return getAnzahlAufgaben();
	}

	/**
	 * @param  nummer
	 * @return                          Klassenstufe
	 * @throws IllegalArgumentException
	 *                                  falls es keine Klassenstufe mit der gegebenen Nummer gibt.
	 */
	public static Klassenstufe valueOfNummer(final int nummer) {

		for (final Klassenstufe kl : Klassenstufe.values()) {

			if (kl.nummer == nummer) {

				return kl;
			}
		}
		throw new IllegalArgumentException("keine Klassenstufe mit Nummer " + nummer + " vorhanden");
	}

	/**
	 * @param  label
	 *                                  String
	 * @return                          Klassenstufe
	 * @throws IllegalArgumentException
	 *                                  falls es keine Klassenstufe mit dem gegebenen label gibt.
	 */
	public static Klassenstufe valueOfLabel(final String label) {

		for (final Klassenstufe kl : Klassenstufe.values()) {

			if (kl.label.equals(label)) {

				return kl;
			}
		}
		throw new IllegalArgumentException("keine Klassenstufe mit label " + label + " vorhanden");
	}

	public String getStartguthaben() {

		return getAnzahlAufgaben() + ",00";
	}

	/**
	 * @param  dateiname
	 *                   String name einer Datei aus dem Downloadverzeichnis.
	 * @return           Klassenstufe kann auch null sein.
	 */
	public static Klassenstufe getKlasseFuerDownload(final String dateiname) {

		if (dateiname == null) {

			throw new NullPointerException("dateiname");
		}

		if (dateiname.toLowerCase().contains("klasse1")) {

			return Klassenstufe.EINS;
		}

		if (dateiname.toLowerCase().contains("klasse2")) {

			return Klassenstufe.ZWEI;
		}

		if (dateiname.toLowerCase().contains("inkl")) {

			return Klassenstufe.IKID;
		}
		return null;
	}

	public static Klassenstufe[] valuesSorted() {

		return new Klassenstufe[] { EINS, ZWEI, IKID };
	}

	public final int getNummer() {

		return nummer;
	}

}
