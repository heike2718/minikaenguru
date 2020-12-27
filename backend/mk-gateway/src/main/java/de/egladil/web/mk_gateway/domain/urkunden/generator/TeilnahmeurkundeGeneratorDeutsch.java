// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.urkunden.daten.AbstractDatenUrkunde;

/**
 * TeilnahmeurkundeGeneratorDeutsch
 */
public class TeilnahmeurkundeGeneratorDeutsch implements UrkundeGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(TeilnahmeurkundeGeneratorDeutsch.class);

	private UrkundeHeaderRenderer headerRenderer = new UrkundeHeaderRenderer();

	private UrkundeFooterRenderer footerRenderer = new UrkundeFooterRenderer();

	private TeilnahmeurkundeHauptabschnittRendererDeutsch hauptabschnittRenderer = new TeilnahmeurkundeHauptabschnittRendererDeutsch();

	@Override
	public byte[] generiereUrkunde(final AbstractDatenUrkunde datenUrkunde) {

		PdfReader reader = null;
		PdfStamper stamper = null;

		try (final ByteArrayOutputStream os = new ByteArrayOutputStream()) {

			reader = new PdfReader(datenUrkunde.urkundenmotiv().data());
			stamper = new PdfStamper(reader, os);

			final PdfContentByte content = stamper.getOverContent(1);

			// header
			String text = "Beim Mathewettbewerb";
			int verschiebungVonTop = headerRenderer.printSectionAndShiftVerticalPosition(content, text);

			// hauptabschnitt
			verschiebungVonTop = hauptabschnittRenderer.printAbschnittAndShiftVerticalPosition(content, datenUrkunde,
				verschiebungVonTop);

			// footer
			footerRenderer.printSectionAndShiftVerticalPosition(content, datenUrkunde.datum(), "(Jury)");

			stamper.close();
			reader.close();

			final byte[] data = os.toByteArray();
			return data;

		} catch (IOException | DocumentException e) {

			LOG.error("Exception beim Generierem einer Teilnehmerurkunde de: " + e.getMessage(), e);
			throw new MkGatewayRuntimeException("Fehler beim Verarbeiten des Hintergrundbildes.");
		} finally {

			closeStamperQuietly(stamper);
			closeReaderQuietly(reader);
		}
	}

	private void closeStamperQuietly(final PdfStamper stamper) {

		if (stamper != null) {

			try {

				stamper.close();
			} catch (DocumentException | IOException e) {

				LOG.warn("stamper konnte nicht freigegeben werden: {}", e.getMessage(), e);
			}
		}
	}

	private void closeReaderQuietly(final PdfReader reader) {

		if (reader != null) {

			reader.close();
		}
	}

}
