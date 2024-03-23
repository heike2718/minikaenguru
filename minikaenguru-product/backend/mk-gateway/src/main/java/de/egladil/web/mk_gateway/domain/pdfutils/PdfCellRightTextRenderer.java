// =====================================================
// Projekt: mk-gateway
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_gateway.domain.pdfutils;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;

/**
 * PdfCellRightTextRenderer
 */
public class PdfCellRightTextRenderer implements PdfCellRenderer {

	@Override
	public PdfPCell createCell(final Font font, final String text, final int colspan) {

		final PdfPCell cell = new PdfCellLeftTextRenderer().createCell(font, text, colspan);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		return cell;
	}
}
