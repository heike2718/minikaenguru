// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.enums;

/**
 * Farbschema ist der Name eines durch mich vordefinierten Farbschemas. Es definiert ein Hintergrundbild und die Farbe der
 * Überschrift.
 */
public enum Farbschema {

	BLUE("blau", "/overlay_blue.png"),
	ORANGE("orange", "/overlay_orange.png"),
	GREEN("grün", "/overlay_green.png");

	private final String label;

	private final String thumbnailResourcePath;

	/**
	 * Farbschema
	 */
	private Farbschema(final String label, final String thumbnailName) {

		this.thumbnailResourcePath = thumbnailName;
		this.label = label;
	}

	public final String getThumbnailResourcePath() {

		return thumbnailResourcePath;
	}

	public final String getLabel() {

		return label;
	}
}
