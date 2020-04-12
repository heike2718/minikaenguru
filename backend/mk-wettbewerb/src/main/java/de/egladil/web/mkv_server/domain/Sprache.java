// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server.domain;

/**
 * Sprache
 */
public enum Sprache {

	de("deutsch"),
	en("englisch");

	private final String label;

	/**
	 * Sprache
	 */
	private Sprache(final String label) {

		this.label = label;
	}

	public final String getLabel() {

		return label;
	}

}
