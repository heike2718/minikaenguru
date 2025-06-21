// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest;

import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

/**
 * XmlSerializer
 */
public class XmlSerializer {

	private static final Logger LOG = LoggerFactory.getLogger(XmlSerializer.class);

	private JAXBContext jaxbContext;

	private static XmlSerializer instance;

	public static XmlSerializer getInstance() {

		if (instance == null) {

			instance = new XmlSerializer();
			instance.postConstruct();
		}
		return instance;
	}

	public void postConstruct() {

		try {

			this.jaxbContext = JAXBContext.newInstance(new Class[] { ResponsePayload.class });
		} catch (JAXBException e) {

			throw new MkGatewayRuntimeException("Kann jaxbContext nicht erzeugen: " + e.getMessage(), e);

		}

	}

	public <T> String writeAsString(final Class<T> clazz, final T entity) {

		try (StringWriter sw = new StringWriter()) {

			jaxbContext.createMarshaller().marshal(entity, sw);

			return sw.toString();

		} catch (Exception e) {

			LOG.error("Fehler beim Serialisieren von {}: {}", clazz.getName(), e.getMessage(), e);

			return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response-payload><message><level>ERROR</level><message>Es ist ein Fehler aufgetreten. Bitte senden Sie eine Mail an info@egladil.de.</message></message></response-payload>";
		}

	}

}
