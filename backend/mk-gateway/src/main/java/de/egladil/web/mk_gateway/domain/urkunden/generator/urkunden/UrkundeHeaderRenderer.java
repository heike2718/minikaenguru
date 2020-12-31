// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden;

import com.itextpdf.text.pdf.PdfContentByte;

/**
 * UrkundeHeaderRenderer
 */
public class UrkundeHeaderRenderer {

	/**
	 * @param     content
	 *                    PdfContentByte die Seite.
	 * @param     text
	 *                    String den zu druckenden Text
	 * @returnint         der Y-Vorschub für den folgenden Abschnitt.
	 */
	public int printSectionAndShiftVerticalPosition(final PdfContentByte content, final String text) {

		UrkundeLinePrinter.printTextLeft(content, text, UrkundePDFUtils.getFontBlack(UrkundePDFUtils.SIZE_TEXT_SMALL), 0);
		return 110;
	}

}
