// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mkv_server.pdf;

import java.io.IOException;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;

/**
 * FontCalulator
 */
public class FontCalulator {

	/**
	 * Berechnet die Fontgröße, wobei davon ausgegangen wird, das es maximal 2 Zeilen werden.
	 *
	 * @param  name
	 *              String der bereits normalisierte Name.
	 * @return      int eine der 3 FontSizes.
	 */
	public int maximaleFontSizeTeilnehmername(final String name) {

		if (name == null) {

			throw new IllegalArgumentException("name null");
		}
		new LengthTester().checkTooLongAndThrow(name, UrkundeConstants.SIZE_NAME_SMALL);
		final Integer[] sizes = new Integer[] { UrkundeConstants.SIZE_NAME_LARGE, UrkundeConstants.SIZE_NAME_MEDIUM,
			UrkundeConstants.SIZE_NAME_SMALL };

		for (final int size : sizes) {

			final float breite = berechneBreite(name, size);

			if (breite <= UrkundeConstants.MAX_WIDTH) {

				return size;
			}
		}
		return UrkundeConstants.SIZE_NAME_SMALL;
	}

	/**
	 * Berechnet die Breite eines Strings mit dem Standardfont der gegebenen size.
	 *
	 * @param  name
	 * @param  fontSize
	 * @return          float
	 */
	public float berechneBreite(final String name, final int fontSize) {

		final Font font = UrkundeConstants.getFontBlack(fontSize);
		final Chunk chunk = new Chunk(name, font);
		return chunk.getWidthPoint();
	}

	/**
	 * Berechnet die Breite eines Strings mit dem Standardfont der gegebenen size.
	 *
	 * @param  name
	 * @param  fontSize
	 * @return                   float
	 * @throws IOException
	 * @throws DocumentException
	 */
	public float berechneHoehe(final String name, final int fontSize) throws DocumentException, IOException {

		final Font font = new FontProviderWrapper().getFont(FontTyp.BOLD, fontSize);
		final BaseFont bf = font.getCalculatedBaseFont(true);
		final float ascent = bf.getAscentPoint(name, fontSize);
		final float descent = bf.getDescentPoint(name, fontSize);
		return ascent - descent;
	}

	/**
	 * Berechnet ausgehend von der aktuellen Verschiebung die neue Verschiebung nach unten für den Text mit der
	 * gegebenen fontSize.
	 *
	 * @param  text
	 *                                      String der nächste zu druckende Text
	 * @param  aktuelleVerschiebungVertikal
	 *                                      int die aktuelle Verschiebung relativ zum Y_TOP.
	 * @param  fontSize
	 *                                      int die Höhe des Fonts, mit dem text edruckt werden soll.
	 * @return                              int die neue vertikale Vershiebung
	 * @throws DocumentException
	 * @throws IOException
	 */
	public int berechneDeltaY(final String text, final int aktuelleVerschiebungVertikal, final int fontSize) throws DocumentException, IOException {

		final float hoehe = berechneHoehe(text, fontSize);
		final float result = hoehe + aktuelleVerschiebungVertikal;
		return Math.round(result) + UrkundeConstants.POINTS_BETWEEN_ROWS;
	}
}
