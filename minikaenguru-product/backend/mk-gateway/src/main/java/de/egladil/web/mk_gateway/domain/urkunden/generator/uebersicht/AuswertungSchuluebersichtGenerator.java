// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator.uebersicht;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
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
import de.egladil.web.mk_gateway.domain.statistik.functions.PunkteStringMapper;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.urkunden.daten.AuswertungDatenRepository;
import de.egladil.web.mk_gateway.domain.urkunden.daten.KinddatenUebersicht;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * AuswertungSchuluebersichtGenerator
 */
public class AuswertungSchuluebersichtGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(AuswertungSchuluebersichtGenerator.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	/**
	 * Generiert die Übersichtsseite(n) für die Schulauswertung.
	 *
	 * @param  schulteilnahme
	 * @param  datenRepository
	 * @return
	 */
	public byte[] generiereSchuluebersicht(final Schulteilnahme schulteilnahme, final AuswertungDatenRepository datenRepository) {

		final FontProviderWrapper fontProvider = new FontProviderWrapper();
		final Document doc = new Document(PageSize.A4);

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			PdfWriter.getInstance(doc, out);
			doc.open();

			// Überschrift
			final String titel = getTitel(schulteilnahme.wettbewerbID());
			Paragraph element = new Paragraph(titel, fontProvider.getFont(FontTyp.BOLD, 14));
			element.setAlignment(Element.ALIGN_CENTER);
			doc.add(element);

			// Schulname
			doc.add(Chunk.NEWLINE);
			element = new Paragraph(schulteilnahme.nameSchule(), fontProvider.getFont(FontTyp.BOLD, 14));
			element.setAlignment(Element.ALIGN_CENTER);
			doc.add(element);

			Font fontBold = fontProvider.getFont(FontTyp.BOLD, 11);
			Font fontNormal = fontProvider.getFont(FontTyp.NORMAL, 11);

			for (Klassenstufe klassenstufe : Klassenstufe.valuesSorted()) {

				int platzierung = 1;
				int countGleichePunktzahl = 1;

				List<KinddatenUebersicht> datenUebersicht = datenRepository.uebersichtsdatenSorted(klassenstufe);

				if (datenUebersicht.isEmpty()) {

					continue;
				}

				doc.add(Chunk.NEWLINE);

				// Überschrift section Klassenstufe
				String text = MessageFormat.format(
					applicationMessages.getString("schulauswertung.pdf.uebersicht.section.klassenstufe"),
					new Object[] { klassenstufe.getLabel() });

				doc.add(new Paragraph(text, fontProvider.getFont(FontTyp.BOLD, 12)));
				doc.add(Chunk.NEWLINE);

				int aktuellePunktzahl = datenUebersicht.get(0).punkte();

				int lengthPlatzierung = getLengthPlatzierung(datenUebersicht.size());
				String textPlatzierung = "";

				for (int index = 0; index < datenUebersicht.size(); index++) {

					KinddatenUebersicht kindDaten = datenUebersicht.get(index);

					String punkte = new PunkteStringMapper().apply(kindDaten.punkte());

					if (kindDaten.punkte() < aktuellePunktzahl) {

						platzierung += countGleichePunktzahl;
						textPlatzierung = StringUtils.leftPad(platzierung + ". ", lengthPlatzierung);
						countGleichePunktzahl = 1;
					} else {

						if (index == 0) {

							textPlatzierung = StringUtils.leftPad(platzierung + ". ", lengthPlatzierung);
						} else {

							textPlatzierung = " ";

							for (int i = 0; i < lengthPlatzierung; i++) {

								textPlatzierung += " ";
							}
							countGleichePunktzahl++;
						}
					}

					aktuellePunktzahl = kindDaten.punkte();

					text = MessageFormat.format(applicationMessages.getString("schulauswertung.pdf.uebersicht.platz_und_kind"),
						new Object[] { textPlatzierung, kindDaten.fullName(), kindDaten.nameKlasse(), punkte });

					doc.add(new Paragraph(text, fontNormal));
					// doc.add(Chunk.NEWLINE);
				}

				doc.add(Chunk.NEWLINE);
				List<KinddatenUebersicht> kaengurugewinner = datenRepository.getKaengurugewinner(klassenstufe);

				switch (kaengurugewinner.size()) {

				case 0:

					break;

				case 1: {

					text = applicationMessages.getString("schulauswertung.pdf.uebersicht.section.gewinner.kaengurusprung");
					doc.add(new Paragraph(text, fontBold));
					doc.add(Chunk.NEWLINE);

					KinddatenUebersicht kindDaten = kaengurugewinner.get(0);

					text = MessageFormat.format(
						applicationMessages.getString("schulauswertung.pdf.uebersicht.gewinner.kaengurusprung"),
						new Object[] { kindDaten.fullName(),
							kindDaten.nameKlasse(), Integer.valueOf(kindDaten.laengeKaengurusprung()) });

					doc.add(new Paragraph(text, fontNormal));
					doc.add(Chunk.NEWLINE);
					break;
				}

				default: {

					text = MessageFormat.format(
						applicationMessages.getString("schulauswertung.pdf.uebersicht.mehrere_kaenguruspruenge"),
						new Object[] { Integer.valueOf(kaengurugewinner.size()),
							Integer.valueOf(kaengurugewinner.get(0).laengeKaengurusprung()) });

					doc.add(new Paragraph(text, fontBold));
					doc.add(Chunk.NEWLINE);

					for (KinddatenUebersicht kindDaten : kaengurugewinner) {

						text = MessageFormat.format(
							applicationMessages.getString("schulauswertung.pdf.uebersicht.gewinner.kaengurusprung"),
							new Object[] { kindDaten.fullName(),
								kindDaten.nameKlasse(), Integer.valueOf(kindDaten.laengeKaengurusprung()) });

						doc.add(new Paragraph(text, fontNormal));
						// doc.add(Chunk.NEWLINE);
					}
				}
					break;
				}
			}

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

	private int getLengthPlatzierung(final int anzahlTeilnehmer) {

		String str = "" + anzahlTeilnehmer + ". ";

		return str.length();

	}

	/**
	 * @param  wettbewerbID
	 * @param  optSchule
	 * @return
	 */
	private String getTitel(final WettbewerbID wettbewerbID) {

		return MessageFormat.format(applicationMessages.getString("statistik.pdf.ueberschrift.schule.nameUnbekannt"),
			new Object[] { wettbewerbID.toString() });
	}

}
