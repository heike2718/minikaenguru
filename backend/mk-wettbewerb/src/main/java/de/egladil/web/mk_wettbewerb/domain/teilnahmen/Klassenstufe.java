// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

/**
 * Klassenstufe
 */
public enum Klassenstufe {

	IKID("Inklusion"),
	EINS("Klasse 1"),
	ZWEI("Klasse 2");

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

}
