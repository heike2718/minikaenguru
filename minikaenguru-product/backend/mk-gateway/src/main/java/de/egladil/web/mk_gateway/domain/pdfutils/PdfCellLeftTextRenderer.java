// =====================================================
// Projekt: mk-gateway
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_gateway.domain.pdfutils;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;

/**
 * PdfCellLeftTextRenderer
 */
public class PdfCellLeftTextRenderer implements PdfCellRenderer {

	@Override
	public PdfPCell createCell(final Font font, final String text, final int colspan) {

		final PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setFixedHeight(CELL_HEIGHT);
		cell.setColspan(colspan);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		return cell;
	}
}
