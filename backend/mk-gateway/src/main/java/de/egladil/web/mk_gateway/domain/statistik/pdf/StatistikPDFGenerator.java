// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import com.itextpdf.text.pdf.PdfWriter;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.pdfutils.FontProviderWrapper;
import de.egladil.web.mk_gateway.domain.pdfutils.FontTyp;
import de.egladil.web.mk_gateway.domain.pdfutils.PdfMerger;
import de.egladil.web.mk_gateway.domain.statistik.GesamtpunktverteilungKlassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * StatistikPDFGenerator
 */
public class StatistikPDFGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(StatistikPDFGenerator.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	private final AufgabenuebersichtPDFGenerator aufgabenuebersichtGenerator = new AufgabenuebersichtPDFGenerator();

	private final RohpunkteuebersichtPDFGenerator rohpunkteuebersichtGenerator = new RohpunkteuebersichtPDFGenerator();

	private final ProzentrangEinzeluebersichtPDFGenerator prozentrangEinzeluebersichtPDFGenerator = new ProzentrangEinzeluebersichtPDFGenerator();

	/**
	 * Generiert die Aufgabenübersichten je Klassenstufen.
	 *
	 * @param  verteilungenNachKlassenstufe
	 * @return
	 */
	public List<byte[]> generiereAufgabenUebersichtVeranstalter(final Map<Klassenstufe, GesamtpunktverteilungKlassenstufe> verteilungenNachKlassenstufe) {

		List<byte[]> result = new ArrayList<>();

		for (Klassenstufe klassenstufe : Klassenstufe.valuesSorted()) {

			GesamtpunktverteilungKlassenstufe verteilung = verteilungenNachKlassenstufe.get(klassenstufe);

			if (verteilung != null) {

				result.add(aufgabenuebersichtGenerator.generiereAufgabenuebersichtKlassenstufe(verteilung, false));
			}

		}

		return result;
	}

	/**
	 * Generiert die Tabellen je Klassenstufe.
	 *
	 * @param  verteilungenNachKlassenstufe
	 * @return
	 */
	public List<byte[]> generiereStatistikUebersichtVeranstalter(final Map<Klassenstufe, GesamtpunktverteilungKlassenstufe> verteilungenNachKlassenstufe) {

		List<byte[]> result = new ArrayList<>();

		for (Klassenstufe klassenstufe : Klassenstufe.valuesSorted()) {

			GesamtpunktverteilungKlassenstufe verteilung = verteilungenNachKlassenstufe.get(klassenstufe);

			if (verteilung != null) {

				result.add(aufgabenuebersichtGenerator.generiereAufgabenuebersichtKlassenstufe(verteilung, false));
				result.add(rohpunkteuebersichtGenerator.generiereRohpunktuebersichtKlassenstufe(verteilung));

			}

		}

		return result;
	}

	public byte[] generiereGesamtpunktverteilungWettbewerb(final WettbewerbID wettbewerbID, final Map<Klassenstufe, GesamtpunktverteilungKlassenstufe> verteilungenNachKlassenstufe) {

		List<byte[]> result = new ArrayList<>();

		if (verteilungenNachKlassenstufe.isEmpty()) {

			result.add(generiereDeckblattKeineDaten(wettbewerbID));

		} else {

			result.add(generiereDeckblatt(wettbewerbID));
		}

		for (Klassenstufe klassenstufe : Klassenstufe.valuesSorted()) {

			GesamtpunktverteilungKlassenstufe verteilung = verteilungenNachKlassenstufe.get(klassenstufe);

			if (verteilung != null) {

				result.add(aufgabenuebersichtGenerator.generiereAufgabenuebersichtKlassenstufe(verteilung, true));

				result.add(prozentrangEinzeluebersichtPDFGenerator.generiereProzentrangUebersicht(verteilung));

			}

		}

		PdfMerger pdfMerger = new PdfMerger();
		return pdfMerger.concatPdf(result);
	}

	byte[] generiereDeckblattKeineDaten(final WettbewerbID wettbewerbID) {

		final FontProviderWrapper fontProvider = new FontProviderWrapper();
		final Document doc = new Document(PageSize.A4);

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			PdfWriter.getInstance(doc, out);
			doc.open();

			addUeberschrift(wettbewerbID, fontProvider, doc);

			Font fontNormal = fontProvider.getFont(FontTyp.NORMAL, 11);
			doc.add(Chunk.NEWLINE);

			doc.add(new Paragraph(applicationMessages.getString("statistik.pdf.gesamtverteilung.keineDaten"),
				fontNormal));

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

	byte[] generiereDeckblatt(final WettbewerbID wettbewerbID) {

		final FontProviderWrapper fontProvider = new FontProviderWrapper();
		final Document doc = new Document(PageSize.A4);

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			PdfWriter.getInstance(doc, out);
			doc.open();

			addUeberschrift(wettbewerbID, fontProvider, doc);

			Font fontNormal = fontProvider.getFont(FontTyp.NORMAL, 11);
			doc.add(Chunk.NEWLINE);

			doc.add(new Paragraph(applicationMessages.getString("statistik.pdf.definitionen.inhaltDokument.gesamtverteilung"),
				fontNormal));
			doc.add(Chunk.NEWLINE);

			doc.add(new Paragraph(applicationMessages.getString("statistik.definitionen.prozentrang"),
				fontNormal));
			doc.add(Chunk.NEWLINE);

			doc.add(new Paragraph(applicationMessages.getString("statistik.definitionen.median.gesamtverteilung"),
				fontNormal));

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

	/**
	 * @param  wettbewerbID
	 * @param  fontProvider
	 * @param  doc
	 * @throws DocumentException
	 */
	private void addUeberschrift(final WettbewerbID wettbewerbID, final FontProviderWrapper fontProvider, final Document doc) throws DocumentException {

		final String link = applicationMessages.getString("statistik.pdf.autorin_und_url");
		Paragraph element = new Paragraph(link, fontProvider.getFont(FontTyp.NORMAL, 11));
		element.setAlignment(Element.ALIGN_LEFT);
		doc.add(element);
		doc.add(Chunk.NEWLINE);

		final String titel = MessageFormat.format(applicationMessages.getString("statistik.pdf.ueberschrift.wettbewerb"),
			new Object[] { wettbewerbID.toString() });
		element = new Paragraph(titel, fontProvider.getFont(FontTyp.BOLD, 14));
		element.setAlignment(Element.ALIGN_CENTER);
		doc.add(element);
	}
}
