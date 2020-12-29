// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden;

/**
 * Ueberschriftfarbe
 */
public enum Ueberschriftfarbe {

	BLUE(0, 0, 255),
	ORANGE(255, 128, 0),
	GREEN(0, 102, 0);

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

	public final int rgbRed() {

		return rgbRed;
	}

	public final int rgbGreen() {

		return rgbGreen;
	}

	public final int rgbBlue() {

		return rgbBlue;
	}
}
