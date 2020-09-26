// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.pdfutils.FontProviderWrapper;
import de.egladil.web.mk_gateway.domain.pdfutils.FontTyp;
import de.egladil.web.mk_gateway.domain.pdfutils.PdfCellCenteredTextRenderer;
import de.egladil.web.mk_gateway.domain.pdfutils.PdfCellRenderer;
import de.egladil.web.mk_gateway.domain.pdfutils.PdfCellRightTextRenderer;
import de.egladil.web.mk_gateway.domain.statistik.GesamtpunktverteilungKlassenstufe;
import de.egladil.web.mk_gateway.domain.statistik.RohpunktItem;

/**
 * ProzentrangEinzeluebersichtPDFGenerator
 */
public class ProzentrangEinzeluebersichtPDFGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(ProzentrangEinzeluebersichtPDFGenerator.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	private final FontProviderWrapper fontProvider = new FontProviderWrapper();

	private final PdfCellRenderer cellRendererRight = new PdfCellRightTextRenderer();

	public byte[] generiereProzentrangUebersicht(final GesamtpunktverteilungKlassenstufe verteilung) {

		final Document doc = new Document(PageSize.A4);
		final int numCols = 2;

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			PdfWriter.getInstance(doc, out);
			doc.open();

			final PdfPTable table = new PdfPTable(numCols);
			String text = MessageFormat.format(applicationMessages.getString("statistik.pdf.prozentranguebersicht.header"),
				new Object[] { verteilung.klassenstufe().getLabel() });
			PdfPCell cell = new PdfCellCenteredTextRenderer().createCell(fontProvider.getFont(FontTyp.BOLD, 11), text, numCols);
			table.addCell(cell);

			addTableHeaders(table);

			for (RohpunktItem item : verteilung.rohpunktItems()) {

				this.addRow(table, item);
			}

			doc.add(table);
			doc.close();
			return out.toByteArray();

		} catch (IOException e) {

			String msg = "konnte keinen ByteArrayOutputStream erzeugen: " + e.getMessage();
			LOG.error(msg, e);

			throw new MkGatewayRuntimeException(msg, e);
		} catch (DocumentException e) {

			String msg = "konnte keinen PdfWriter erzeugen: " + e.getMessage();
			LOG.error(msg, e);
			throw new MkGatewayRuntimeException(msg, e);
		}

	}

	private void addTableHeaders(final PdfPTable table) {

		final Font font = fontProvider.getFont(FontTyp.BOLD, 11);

		final String[] headings = new String[] { "Punkte", "Prozentrang" };

		for (final String text : headings) {

			final PdfPCell cell = new PdfCellCenteredTextRenderer().createCell(font, text, 1);
			table.addCell(cell);
		}
	}

	private void addRow(final PdfPTable table, final RohpunktItem item) {

		final Font font = fontProvider.getFont(FontTyp.NORMAL, 11);
		table.addCell(cellRendererRight.createCell(font, item.getPunkteText(), 1));
		table.addCell(cellRendererRight.createCell(font, String.valueOf(item.getProzentrangText()), 1));
	}

}
