// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mkv_server.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;

import de.egladil.web.mk_commons.utils.UrkundenmotivProvider;

/**
 * UrkundeConstants
 */
public final class UrkundeConstants {

	public static int POINTS_BETWEEN_ROWS = 22;

	public static int INDENT_JURY = 30;

	public static int POINTS_BETWEEN_PARAGRAPHS = 35;

	public static int POINTS_OHNE_SCHULE = 32;

	public static int Y_TOP = 750;

	public static int Y_BOTTOM = 105;

	public static int X_LEFT = 145;// 120;

	public static int X_CENTER = 265;// 240;

	public static int X_RIGHT = 455;// 445;

	public static int MAXIMALE_ANZAHL_ZEICHEN_NAME = 54;

	/** maximale Breite einer Zeile in der rechten Spalte (315) */
	public static float MAX_WIDTH = 315.0f;

	/** 12 */
	public static int SIZE_TEXT_SMALL = 12;

	/** 14 */
	public static int SIZE_TEXT_NORMAL = 14;

	/** 16 */
	public static int SIZE_NAME_SMALL = 16;

	/** 18 */
	public static int SIZE_NAME_MEDIUM = 18;

	/** 20 */
	public static int SIZE_NAME_LARGE = 20;

	/** 34 */
	public static int SIZE_TITLE = 34;

	public static final String FONT_NORMAL = "/fonts/FreeSans.ttf";

	public static final String FONT_BOLD = "/fonts/FreeSansBold.ttf";

	public static final String FONT_OBLIQUE = "/fonts/FreeSansOblique.ttf";

	public static final String FONT_BOLD_OBLIQUE = "/fonts/FreeSansBoldOblique.ttf";

	public static final Integer[] TEILNEHMER_SIZES_DESCENDING = new Integer[] { SIZE_NAME_LARGE, SIZE_NAME_MEDIUM,
		SIZE_NAME_SMALL };

	public static final Integer[] SCHULNAME_SIZES_DESCENDING = new Integer[] { SIZE_TEXT_NORMAL };

	/**
	 * UrkundeConstants
	 */
	private UrkundeConstants() {

	}

	/**
	 * Gibt den Standardfont mit der gegebenen Größe zurück
	 *
	 * @param  size
	 *              int
	 * @return      Font
	 */
	private static Font getFont(final int size) {
		// final String resource = UrkundeConstants.class.getResource(FONT_BOLD).toString();
		// FontFactory.register(resource);

		// final Font font = FontFactory.getFont(resource, "Cp1250", BaseFont.EMBEDDED, size);
		// final Font font = new Font(FontFamily.HELVETICA, size, Font.BOLD);
		final Font font = new FontProviderWrapper().getFont(FontTyp.BOLD, size);
		return font;
	}

	/**
	 * Gibt den Standardfont mit der gegebenen Größe zurück
	 *
	 * @param  size
	 *              int
	 * @return      Font
	 */
	public static Font getFontBlack(final int size) {

		final Font font = getFont(size);
		font.setColor(BaseColor.BLACK);
		return font;
	}

	/**
	 * Gibt den Standardfont mit der gegebenen Größe zurück
	 *
	 * @param  size
	 *              int
	 * @return      Font
	 */
	public static Font getFont(final int size, final UrkundenmotivProvider backgroundAndColorProvider) {

		final Font font = getFont(size);
		font.setColor(backgroundAndColorProvider.getHeadlineColor());
		return font;
	}

	/**
	 * Gibt den Standardfont mit der gegebenen Größe zurück
	 *
	 * @param  size
	 *              int
	 * @return      Font
	 */
	public static Font getFont(final int size, final BaseColor color) {

		final Font font = getFont(size);
		font.setColor(color);
		return font;
	}

}
