// =====================================================
// Project: commons-validation
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import jakarta.xml.bind.JAXBContext;

/**
 * ResponsePayloadXmlTest
 */
public class ResponsePayloadXmlTest {

	@Test
	void should_marshall() throws Exception {

		// Arrange
		ResponsePayload responsePayload = ResponsePayload
			.messageOnly(MessagePayload.error("Es ist ein Fehler aufgetreten. Bitte senden Sie eine Mail an info@egladil.de."));

		JAXBContext jaxbContext = JAXBContext.newInstance(ResponsePayload.class);

		// Act
		jaxbContext.createMarshaller().marshal(responsePayload, System.out);
	}

}
