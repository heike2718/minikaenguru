// =====================================================
// Projekt: mk-gateway
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_gateway.domain.pdfutils;

import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;

/**
 * PdfCellRenderer
 */
public interface PdfCellRenderer {

	int CELL_HEIGHT = 25;

	/**
	 * Erzeugt eine PdfCell mit fester Höhe
	 *
	 * @param  font
	 *                 Font
	 * @param  text
	 *                 String der zu zeichnende Text
	 * @param  colspan
	 *                 int
	 * @return
	 */
	PdfPCell createCell(final Font font, final String text, final int colspan);
}
