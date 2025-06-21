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
import java.util.Optional;
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

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.pdfutils.FontProviderWrapper;
import de.egladil.web.mk_gateway.domain.pdfutils.FontTyp;
import de.egladil.web.mk_gateway.domain.pdfutils.PdfMerger;
import de.egladil.web.mk_gateway.domain.statistik.GesamtpunktverteilungKlassenstufe;
import de.egladil.web.mk_gateway.domain.statistik.api.MedianAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.api.MedianeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * PrivatteilnahmenuebersichtPDFGenerator
 */
public class PrivatteilnahmenuebersichtPDFGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(PrivatteilnahmenuebersichtPDFGenerator.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	/**
	 * Erzeugt das PDF für eine Privatteilnahme.
	 *
	 * @param  wettbewerbID
	 *                                      Die WettbewerbID
	 * @param  verteilungenNachKlassenstufe
	 *                                      Map die Auswertungsdaten und Texte nach Klassenstufe.
	 * @return
	 */
	public DownloadData generierePdf(final WettbewerbID wettbewerbID, final Map<Klassenstufe, GesamtpunktverteilungKlassenstufe> verteilungenNachKlassenstufe, final MedianeAPIModel gesamtmediane) {

		List<byte[]> seiten = new ArrayList<>();

		try {

			byte[] deckblatt = this.generiereDeckblatt(wettbewerbID, verteilungenNachKlassenstufe, gesamtmediane);
			seiten.add(deckblatt);

			List<byte[]> statistiken = new StatistikPDFGenerator()
				.generiereStatistikUebersichtVeranstalter(verteilungenNachKlassenstufe);
			seiten.addAll(statistiken);

			final byte[] result = new PdfMerger().concatPdf(seiten);

			String dateiname = MessageFormat.format(applicationMessages.getString("statistik.pdf.dateiname.privat"),
				new Object[] { wettbewerbID.toString() });

			return new DownloadData(dateiname, result);
		} finally {

			// Memory-Leak
			seiten.clear();
			seiten = null;
		}

	}

	byte[] generiereDeckblatt(final WettbewerbID wettbewerbID, final Map<Klassenstufe, GesamtpunktverteilungKlassenstufe> verteilungenNachKlassenstufe, final MedianeAPIModel gesamtmediane) {

		final FontProviderWrapper fontProvider = new FontProviderWrapper();
		final Document doc = new Document(PageSize.A4);

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			PdfWriter.getInstance(doc, out);
			doc.open();

			final String titel = getTitel(wettbewerbID);
			Paragraph element = new Paragraph(titel, fontProvider.getFont(FontTyp.BOLD, 14));
			element.setAlignment(Element.ALIGN_CENTER);
			doc.add(element);

			Font fontNormal = fontProvider.getFont(FontTyp.NORMAL, 11);
			doc.add(Chunk.NEWLINE);

			for (Klassenstufe klassenstufe : Klassenstufe.valuesSorted()) {

				GesamtpunktverteilungKlassenstufe verteilung = verteilungenNachKlassenstufe.get(klassenstufe);

				if (verteilung != null) {

					Optional<MedianAPIModel> optMedian = gesamtmediane.findMedian(klassenstufe);

					String gesamtmedian = optMedian.isPresent() ? optMedian.get().getMedian() : "";

					String text = MessageFormat.format(applicationMessages.getString("statistik.pdf.median.privat.text"),
						new Object[] { klassenstufe.getLabel(), verteilung.getMedian() });

					if (!gesamtmedian.isBlank()) {

						text += " ";
						text += MessageFormat.format(applicationMessages.getString("statistik.pdf.median.wettbewerb"),
							new Object[] { wettbewerbID.toString(), gesamtmedian });
					}
					doc.add(new Paragraph(text, fontNormal));
				}
			}

			doc.add(Chunk.NEWLINE);
			doc.add(new Paragraph(applicationMessages.getString("statistik.definitionen.median.veranstalter"),
				fontNormal));

			doc.add(Chunk.NEWLINE);
			doc.add(new Paragraph(applicationMessages.getString("statistik.pdf.definitionen.inhaltDokument.veranstalter"),
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
	 * @return
	 */
	private String getTitel(final WettbewerbID wettbewerbID) {

		return MessageFormat.format(applicationMessages.getString("statistik.pdf.ueberschrift.privatveranstalter"),
			new Object[] { wettbewerbID.toString() });
	}

}
