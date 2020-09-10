// =====================================================
// Projekt: de.egladil.mkv.auswertungen
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_gateway.domain.pdfutils;

import com.itextpdf.text.Font;

/**
 * UebersichtFontProvider stellt die Fonts für eine Übersichtsseite zur Verfügung. Default size ist hier 11.
 */
public class UebersichtFontProvider {

	private static final int SIZE = 11;

	private final Font fontBold;

	private final Font fontNormal;

	/**
	 * UebersichtFontProvider
	 */
	public UebersichtFontProvider() {

		final FontProviderWrapper fpw = new FontProviderWrapper();
		fontBold = fpw.getFont(FontTyp.BOLD, SIZE);
		fontNormal = fpw.getFont(FontTyp.NORMAL, 11);
	}

	public final Font getFontBold() {

		return fontBold;
	}

	public final Font getFontBold(final int size) {

		final FontProviderWrapper fpw = new FontProviderWrapper();
		return fpw.getFont(FontTyp.BOLD, size);
	}

	public final Font getFontNormal() {

		return fontNormal;
	}

}
