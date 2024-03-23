// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klassenstufe
 */
public enum Klassenstufe {

	IKID("Inklusion", "Inklusion") {

		@Override
		public List<String> defaultAufgabennummern() {

			return Arrays.asList(new String[] { "A-1", "A-2", "B-1", "B-2", "C-1", "C-2" });
		}

		@Override
		public int getStartguthaben() {

			return 1200;
		}

		@Override
		public int getAnzahlAufgabenInKategorien() {

			return 2;
		}

		@Override
		public int getAnzahlSpalten() {

			return 3;
		}

		@Override
		public int getMaximalpunktzahlMal100() {

			return 3600;
		}

		@Override
		public int getMinimalesWettbewerbsjahrMitStatistik() {

			return 2019;
		}

	},
	EINS("Klasse 1", "1") {
		@Override
		public List<String> defaultAufgabennummern() {

			return Arrays
				.asList(new String[] { "A-1", "A-2", "A-3", "A-4", "B-1", "B-2", "B-3", "B-4", "C-1", "C-2", "C-3", "C-4" });
		}

		@Override
		public int getStartguthaben() {

			return 1200;
		}

		@Override
		public int getAnzahlAufgabenInKategorien() {

			return 4;
		}

		@Override
		public int getMaximalpunktzahlMal100() {

			return 6000;
		}

		@Override
		public int getMinimalesWettbewerbsjahrMitStatistik() {

			return 2014;
		}

	},
	ZWEI("Klasse 2", "2") {
		@Override
		public List<String> defaultAufgabennummern() {

			return Arrays.asList(
				new String[] { "A-1", "A-2", "A-3", "A-4", "A-5", "B-1", "B-2", "B-3", "B-4", "B-5", "C-1", "C-2", "C-3", "C-4",
					"C-5" });
		}

		@Override
		public int getStartguthaben() {

			return 1500;
		}

		@Override
		public int getAnzahlAufgabenInKategorien() {

			return 5;
		}

		@Override
		public int getMaximalpunktzahlMal100() {

			return 7500;
		}

		@Override
		public int getMinimalesWettbewerbsjahrMitStatistik() {

			return 2010;
		}

	};

	private final String label;

	private final String labelSuffix;

	/**
	 * @param label
	 */
	private Klassenstufe(final String label, final String labelSuffix) {

		this.label = label;
		this.labelSuffix = labelSuffix;
	}

	public String getLabel() {

		return label;
	}

	public String getLabelSuffix() {

		return labelSuffix;
	}

	public abstract int getStartguthaben();

	public abstract int getAnzahlAufgabenInKategorien();

	public abstract int getMaximalpunktzahlMal100();

	public abstract int getMinimalesWettbewerbsjahrMitStatistik();

	public int getAnzahlSpalten() {

		return 5;
	}

	public static Klassenstufe[] valuesSorted() {

		return new Klassenstufe[] { IKID, EINS, ZWEI };
	}

	abstract List<String> defaultAufgabennummern();

	/**
	 * @param  jahr
	 *                                   in den Jahren 2010 - 2013 gab es keine Klassenstufe 1, in den Jahren 2014 - 2016 waren die
	 *                                   Aufgaben für EINS die gleichen wie für ZWEI, wurden aber bereits getrennt ausgewertet
	 * @return                           List
	 * @throws IllegalArgumentException,
	 *                                   wenn es zur Kombination aus Klassennummer und Jahr keine Auswertung gibt.
	 */
	public List<String> getAufgabennummern(final Integer jahr) {

		if (this == Klassenstufe.IKID) {

			if (jahr <= 2017) {

				return new ArrayList<>();
			}
		}

		if (this == Klassenstufe.EINS) {

			if (jahr >= 2010 && jahr <= 2013) {

				return new ArrayList<>();
			}

			if (jahr >= 2014 && jahr <= 2016) {

				return Klassenstufe.ZWEI.defaultAufgabennummern();
			}
		}

		return defaultAufgabennummern();
	}

	/**
	 * Im Ergebnis sind die Aufgabennummern ihrem index in einem Wertungscode-String-char-Array zugeordnet.
	 *
	 * @param  jahr
	 *              Integer: das Wettbewerbsjahr
	 * @return      Map
	 */
	public Map<String, Integer> getAufgabennummernWithWertungscodeIndex(final Integer jahr) {

		List<String> aufgabennummern = this.getAufgabennummern(jahr);
		Map<String, Integer> result = new HashMap<>();

		for (int index = 0; index < aufgabennummern.size(); index++) {

			result.put(aufgabennummern.get(index), Integer.valueOf(index));
		}

		return result;

	}

	public String getStartguthaben(final Integer jahr) {

		List<String> aufgabennummern = this.getAufgabennummern(jahr);

		if (aufgabennummern.isEmpty()) {

			return null;
		}
		return aufgabennummern.size() + ",00";
	}

	public static Klassenstufe valueOfNumericString(final String value) {

		int intVal = 0;

		try {

			intVal = Integer.valueOf(value);
		} catch (NumberFormatException e) {

			return null;
		}

		switch (intVal) {

		case 0:
			return Klassenstufe.IKID;

		case 1:
			return Klassenstufe.EINS;

		case 2:
			return Klassenstufe.ZWEI;

		default:
			return null;
		}

	}

}
