// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator;

import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfContentByte;

/**
 * UrkundeFooterRenderer
 */
public class UrkundeFooterRenderer {

	/**
	 * Druckt den Text für das Datum und die Jury auf die Seite.
	 *
	 * @param content
	 * @param datum
	 * @param textJury
	 */
	public void printSectionAndShiftVerticalPosition(final PdfContentByte content, final String datum, final String textJury) {

		final Font font = UrkundePDFUtils.getFontBlack(UrkundePDFUtils.SIZE_TEXT_SMALL);
		int deltaY = UrkundePDFUtils.Y_TOP - UrkundePDFUtils.Y_BOTTOM;

		UrkundeLinePrinter.printTextLeft(content, datum, font, deltaY);

		deltaY += UrkundePDFUtils.POINTS_BETWEEN_ROWS;

		UrkundeLinePrinter.printText(content, textJury, font, UrkundePDFUtils.X_RIGHT, deltaY);
	}

}
