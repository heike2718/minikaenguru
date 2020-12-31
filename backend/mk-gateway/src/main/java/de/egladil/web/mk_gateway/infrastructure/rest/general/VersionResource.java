// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;

/**
 * VersionResource
 */
@RequestScoped
@Path("version")
@Produces(MediaType.APPLICATION_JSON)
public class VersionResource {

	@ConfigProperty(name = "quarkus.application.version")
	String version;

	@GET
	public Response getVersion() {

		byte[] pdf = this.generatePdf();

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info(version))).build();

	}

	private byte[] generatePdf() {

		final Document doc = new Document(PageSize.A4);

		final String typ = "/fonts/FreeSans.ttf";

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			PdfWriter.getInstance(doc, out);
			doc.open();

			FontFactory.register(typ);
			final Font font = FontFactory.getFont(typ, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11);

			Paragraph element = new Paragraph("Vereinbarung zur Auftragsverarbeitung", font);
			element.setAlignment(Element.ALIGN_CENTER);
			doc.add(element);

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

}
