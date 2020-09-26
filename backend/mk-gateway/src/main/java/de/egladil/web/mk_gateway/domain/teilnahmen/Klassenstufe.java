// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klassenstufe
 */
public enum Klassenstufe {

	IKID("Inklusion") {

		@Override
		public List<String> defaultAufgabennummern() {

			return Arrays.asList(new String[] { "A-1", "A-2", "B-1", "B-2", "C-1", "C-2" });
		}

	},
	EINS("Klasse 1") {
		@Override
		public List<String> defaultAufgabennummern() {

			return Arrays
				.asList(new String[] { "A-1", "A-2", "A-3", "A-4", "B-1", "B-2", "B-3", "B-4", "C-1", "C-2", "C-3", "C-4" });
		}

	},
	ZWEI("Klasse 2") {
		@Override
		public List<String> defaultAufgabennummern() {

			return Arrays.asList(
				new String[] { "A-1", "A-2", "A-3", "A-4", "A-5", "B-1", "B-2", "B-3", "B-4", "B-5", "C-1", "C-2", "C-3", "C-4",
					"C-5" });
		}

	};

	private final String label;

	/**
	 * @param label
	 */
	private Klassenstufe(final String label) {

		this.label = label;
	}

	public String getLabel() {

		return label;
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
	private List<String> getAufgabennummern(final Integer jahr) {

		if (this == Klassenstufe.EINS) {

			if (jahr >= 2010 && jahr <= 2013) {

				throw new IllegalArgumentException("Im Jahr " + jahr + " gibt es keine Auswertung für " + this.label);
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

		return this.getAufgabennummern(jahr).size() + ",00";
	}

}
