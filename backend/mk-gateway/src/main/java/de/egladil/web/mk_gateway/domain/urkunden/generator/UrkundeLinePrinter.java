// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;

/**
 * UrkundeLinePrinter druckt eine einzelne Zeile in das PDF.
 */
public final class UrkundeLinePrinter {

	private UrkundeLinePrinter() {

	}

	public static void printTextLeft(final PdfContentByte content, final String text, final Font font, final int deltaY) {

		printText(content, text, font, UrkundePDFUtils.X_LEFT, deltaY);
	}

	public static void printTextCenter(final PdfContentByte content, final String text, final Font font, final int deltaY) {

		printText(content, text, font, UrkundePDFUtils.X_CENTER, deltaY);
	}

	public static void printText(final PdfContentByte content, final String text, final Font font, final int xPos, final int deltaY) {

		final Phrase p = new Phrase(text, font);
		final int yAbsolut = UrkundePDFUtils.Y_TOP - deltaY;
		ColumnText.showTextAligned(content, Element.ALIGN_LEFT, p, xPos, yAbsolut, 0);
	}

}
