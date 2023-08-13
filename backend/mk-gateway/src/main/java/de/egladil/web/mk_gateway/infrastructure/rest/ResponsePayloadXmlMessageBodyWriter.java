// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

/**
 * ResponsePayloadXmlMessageBodyWriter
 */
// @Provider
// @Produces("application/xml;charset=UTF-8")
@Deprecated // ist anscheinend nicht mehr erforderlich
public class ResponsePayloadXmlMessageBodyWriter implements MessageBodyWriter<ResponsePayload> {

	private static final Logger LOG = LoggerFactory.getLogger(ResponsePayloadXmlMessageBodyWriter.class);

	@Override
	public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {

		return type == ResponsePayload.class && MediaType.APPLICATION_XML_TYPE == mediaType;
	}

	@Override
	public void writeTo(final ResponsePayload responsePayload, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException, WebApplicationException {

		try (Writer writer = new PrintWriter(entityStream)) {

			JAXBContext jaxbContext = JAXBContext.newInstance(ResponsePayload.class);

			jaxbContext.createMarshaller().marshal(responsePayload, writer);

			writer.flush();

		} catch (JAXBException e) {

			LOG.error("Konnten ResponsePayload nicht in XML umwandeln:" + e.getMessage(), e);
			this.writeRecoveryXmlQuietly(entityStream);

		}

	}

	private void writeRecoveryXmlQuietly(final OutputStream entityStream) {

		try (Writer writer = new PrintWriter(entityStream)) {

			writer.append(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response-payload><message><level>ERROR</level><message>Es ist ein Fehler aufgetreten. Bitte senden Sie eine Mail an info@egladil.de.</message></message></response-payload>");

			writer.flush();
		} catch (IOException e) {

			LOG.error("Können nicht einmal das recovery-xml schreiben: " + e.getMessage(), e);
		}

	}

}
