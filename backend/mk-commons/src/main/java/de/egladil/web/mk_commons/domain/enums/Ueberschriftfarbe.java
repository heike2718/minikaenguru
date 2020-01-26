// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.enums;

/**
 * Ueberschriftfarbe
 */
public enum Ueberschriftfarbe {

	BLACK(0, 0, 0),
	MKV_BLUE(0, 0, 255),
	MKV_ORANGE(255, 128, 0),
	MKV_GREEN(0, 102, 0);

	private final int rgbRed;

	private final int rgbGreen;

	private final int rgbBlue;

	/**
	 * Ueberschriftfarbe
	 */
	private Ueberschriftfarbe(final int rgbRed, final int rgbGreen, final int rgbBlue) {

		this.rgbRed = rgbRed;
		this.rgbGreen = rgbGreen;
		this.rgbBlue = rgbBlue;
	}

	public final int getRgbRed() {

		return rgbRed;
	}

	public final int getRgbGreen() {

		return rgbGreen;
	}

	public final int getRgbBlue() {

		return rgbBlue;
	}

}
