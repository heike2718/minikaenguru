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

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.pdfutils.FontProviderWrapper;
import de.egladil.web.mk_gateway.domain.pdfutils.FontTyp;
import de.egladil.web.mk_gateway.domain.pdfutils.PdfCellCenteredTextRenderer;
import de.egladil.web.mk_gateway.domain.pdfutils.PdfCellRenderer;
import de.egladil.web.mk_gateway.domain.pdfutils.PdfCellRightTextRenderer;
import de.egladil.web.mk_gateway.domain.statistik.AufgabeErgebnisItem;
import de.egladil.web.mk_gateway.domain.statistik.GesamtpunktverteilungKlassenstufe;

/**
 * AufgabenuebersichtPDFGenerator
 */
public class AufgabenuebersichtPDFGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(AufgabenuebersichtPDFGenerator.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	private final FontProviderWrapper fontProvider = new FontProviderWrapper();

	private final PdfCellRenderer cellRendererRight = new PdfCellRightTextRenderer();

	/**
	 * @param  verteilung
	 * @return
	 */
	public byte[] generiereAufgabenuebersichtKlassenstufe(final GesamtpunktverteilungKlassenstufe verteilung, final boolean fuerGesamtstatistik) {

		final Document doc = new Document(PageSize.A4);
		final int numCols = 7;

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			PdfWriter.getInstance(doc, out);
			doc.open();

			if (fuerGesamtstatistik) {

				final String titel = MessageFormat.format(applicationMessages.getString("statistik.pdf.ueberschrift.klassenstufe"),
					new Object[] { verteilung.klassenstufe().getLabel() });
				Paragraph element = new Paragraph(titel, fontProvider.getFont(FontTyp.BOLD, 14));
				element.setAlignment(Element.ALIGN_CENTER);
				doc.add(element);

				Font fontNormal = fontProvider.getFont(FontTyp.NORMAL, 11);
				doc.add(Chunk.NEWLINE);

				String gesamtmedian = verteilung.getMedian();

				String text = MessageFormat.format(applicationMessages.getString("statistik.pdf.median.klassenstufe.text"),
					new Object[] { verteilung.klassenstufe().getLabel(), gesamtmedian });
				doc.add(new Paragraph(text, fontNormal));
				doc.add(Chunk.NEWLINE);
			}

			final PdfPTable table = new PdfPTable(numCols);

			String text = MessageFormat.format(applicationMessages.getString("statistik.pdf.aufgabenuebersicht.header"),
				new Object[] { verteilung.klassenstufe().getLabel(), "" + verteilung.anzahlTeilnehmer() });
			PdfPCell cell = new PdfCellCenteredTextRenderer().createCell(fontProvider.getFont(FontTyp.BOLD, 11), text, numCols);
			table.addCell(cell);

			addTableHeaders(table);

			for (AufgabeErgebnisItem item : verteilung.aufgabeErgebnisItems()) {

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
		} finally {

			if (doc != null && doc.isOpen()) {

				doc.close();
			}

		}

	}

	private void addTableHeaders(final PdfPTable table) {

		final Font font = fontProvider.getFont(FontTyp.BOLD, 11);

		final String[] headings = new String[] { "Aufgabe", "richtig", "% richtig", "falsch", "% falsch", "nicht",
			"% nicht" };

		for (final String text : headings) {

			final PdfPCell cell = new PdfCellCenteredTextRenderer().createCell(font, text, 1);
			table.addCell(cell);
		}
	}

	private void addRow(final PdfPTable table, final AufgabeErgebnisItem item) {

		final Font font = fontProvider.getFont(FontTyp.NORMAL, 11);

		PdfPCell cell = cellRendererRight.createCell(font, item.getNummer(), 1);
		table.addCell(cell);

		cell = cellRendererRight.createCell(font, String.valueOf(item.anzahlRichtigGeloest()), 1);
		table.addCell(cell);

		cell = cellRendererRight.createCell(font, item.anteilRichtigText(), 1);
		table.addCell(cell);

		cell = cellRendererRight.createCell(font, String.valueOf(item.anzahlFalschGeloest()), 1);
		table.addCell(cell);

		cell = cellRendererRight.createCell(font, item.anteilFalschText(), 1);
		table.addCell(cell);

		cell = cellRendererRight.createCell(font, String.valueOf(item.anzahlNichtGeloest()), 1);
		table.addCell(cell);

		cell = cellRendererRight.createCell(font, item.anteilNichtGeloestText(), 1);
		table.addCell(cell);

	}

}
