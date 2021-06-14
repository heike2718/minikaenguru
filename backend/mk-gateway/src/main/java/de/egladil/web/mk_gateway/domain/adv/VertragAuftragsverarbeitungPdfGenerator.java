// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.adv;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.pdfutils.PdfMerger;
import de.egladil.web.mk_gateway.domain.pdfutils.UebersichtFontProvider;

/**
 * VertragAuftragsverarbeitungPdfGenerator
 */
@RequestScoped
public class VertragAuftragsverarbeitungPdfGenerator {

	private static final String PATH_SUBDIR_ADV_TEXTE = "/adv/";

	private final UebersichtFontProvider fontProvider = new UebersichtFontProvider();

	@ConfigProperty(name = "path.external.files")
	String pathAdvTexteDir;

	@ConfigProperty(name = "adv.auftragnehmer.strasse-hausnummer", defaultValue = "Schultheißweg 25")
	String strasseHausnummerAuftragnehmer;

	@ConfigProperty(name = "adv.auftragnehmer.plz-ort-ausland", defaultValue = "DE - 55252 Mainz-Kastel")
	String plzOrtAuftragnehmerAusland;

	@ConfigProperty(name = "adv.auftragnehmer.plz-ort-inland", defaultValue = "55252 Mainz-Kastel")
	String plzOrtAuftragnehmerInland;

	/**
	 * @param  vertrag
	 * @return
	 */
	public byte[] generatePdf(final VertragAuftragsdatenverarbeitung vertrag) {

		String path = pathAdvTexteDir + PATH_SUBDIR_ADV_TEXTE + vertrag.vertragstext().dateiname();

		List<byte[]> seiten = new ArrayList<>();

		try {

			final byte[] pdfAllgemein = MkGatewayFileUtils.readBytesFromFile(path);
			final byte[] deckblatt = generiereDeckblatt(vertrag, vertrag.vertragstext().versionsnummer());

			seiten.add(deckblatt);
			seiten.add(pdfAllgemein);

			final byte[] pdfs = new PdfMerger().concatPdf(seiten);

			return pdfs;
		} finally {

			// Memory-Leak
			seiten.clear();
			seiten = null;
		}
	}

	private byte[] generiereDeckblatt(final VertragAuftragsdatenverarbeitung vertrag, final String textVersionsnummer) {

		final Document doc = new Document(PageSize.A4);

		final Anschrift anschrift = vertrag.anschrift();
		final boolean inland = this.auftraggeberInland(anschrift);

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			PdfWriter.getInstance(doc, out);
			doc.open();

			Font font = fontProvider.getFontBold(14);
			Paragraph element = new Paragraph("Vertrag zur Auftragsdatenverarbeitung", font);
			element.setAlignment(Element.ALIGN_CENTER);
			doc.add(element);

			font = fontProvider.getFontBold(12);
			element = new Paragraph("nach Art. 28 Abs. 3 Datenschutz-Grundverordnung (DSGVO)", font);
			element.setAlignment(Element.ALIGN_CENTER);
			doc.add(element);

			font = fontProvider.getFontNormal();

			doc.add(new Paragraph("Version " + textVersionsnummer, font));

			doc.add(Chunk.NEWLINE);

			doc.add(new Paragraph("zwischen", font));

			doc.add(Chunk.NEWLINE);

			doc.add(new Paragraph(anschrift.schulname(), font));
			doc.add(new Paragraph(anschrift.strasse() + " " + anschrift.hausnummer(), font));

			if (inland) {

				doc.add(new Paragraph(anschrift.plz() + " " + anschrift.ort(), font));

			} else {

				doc.add(
					new Paragraph(anschrift.laendercode() + "-" + anschrift.plz() + " " + anschrift.ort(), font));
			}

			doc.add(Chunk.NEWLINE);

			doc.add(new Paragraph("als Auftraggeber - nachfolgend Auftraggeber -", font));

			doc.add(Chunk.NEWLINE);

			doc.add(new Paragraph("und", font));

			doc.add(Chunk.NEWLINE);

			doc.add(new Paragraph("Dr.Heike Winkelvoß", font));
			doc.add(new Paragraph(strasseHausnummerAuftragnehmer, font));

			if (inland) {

				doc.add(new Paragraph(plzOrtAuftragnehmerInland, font));
			} else {

				doc.add(new Paragraph(plzOrtAuftragnehmerAusland, font));
			}

			doc.add(Chunk.NEWLINE);

			doc.add(new Paragraph("als Auftragnehmer- nachfolgend Auftragnehmer -", font));

			doc.add(Chunk.NEWLINE);

			doc.add(new Paragraph("abgeschlossen am " + vertrag.unterzeichnetAmDruck(), font));

			doc.close();
			return out.toByteArray();
		} catch (final IOException e) {

			throw new MkGatewayRuntimeException("konnte keinen ByteArrayOutputStream erzeugen: " + e.getMessage(), e);
		} catch (final DocumentException e) {

			throw new MkGatewayRuntimeException("konnte keinen PdfWriter erzeugen: " + e.getMessage(), e);
		} finally {

			if (doc != null && doc.isOpen()) {

				doc.close();
			}

		}
	}

	private boolean auftraggeberInland(final Anschrift anschrift) {

		return "DE".equalsIgnoreCase(anschrift.laendercode());

	}

}
