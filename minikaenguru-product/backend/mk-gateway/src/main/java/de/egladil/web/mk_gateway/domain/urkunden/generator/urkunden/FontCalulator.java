// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden;

import java.io.IOException;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;

import de.egladil.web.mk_gateway.domain.pdfutils.FontProviderWrapper;
import de.egladil.web.mk_gateway.domain.pdfutils.FontTyp;

/**
 * FontCalulator
 */
public class FontCalulator {

	/**
	 * Berechnet die Breite eines Strings mit dem Standardfont der gegebenen size.
	 *
	 * @param  name
	 * @param  fontSize
	 * @return                   float
	 * @throws IOException
	 * @throws DocumentException
	 */
	public float berechneHoehe(final String name, final int fontSize) {

		final Font font = new FontProviderWrapper().getFont(FontTyp.BOLD, fontSize);
		final BaseFont bf = font.getCalculatedBaseFont(true);
		final float ascent = bf.getAscentPoint(name, fontSize);
		final float descent = bf.getDescentPoint(name, fontSize);
		return ascent - descent;
	}

	/**
	 * Berechnet die Breite eines Strings mit dem Standardfont der gegebenen size.
	 *
	 * @param  name
	 * @param  fontSize
	 * @return          float
	 */
	public float berechneBreite(final String name, final int fontSize) {

		final Font font = UrkundePDFUtils.getFontBlack(fontSize);
		final Chunk chunk = new Chunk(name, font);
		return chunk.getWidthPoint();
	}

	/**
	 * @param  text
	 * @param  deltaY
	 * @param  sIZE_TITLE
	 * @return
	 */
	public int berechneDeltaY(final String text, final int deltaYAktuell, final int fontSize) {

		final float hoehe = berechneHoehe(text, fontSize);
		final float result = hoehe + deltaYAktuell;
		return Math.round(result) + UrkundePDFUtils.POINTS_BETWEEN_ROWS;
	}

}
