// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.katalogantrag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_kataloge.domain.apimodel.SchulkatalogAntrag;
import de.egladil.web.mk_kataloge.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_kataloge.domain.katalogantrag.KatalogAntragReceived;
import de.egladil.web.mk_kataloge.domain.katalogantrag.KatalogAntragService;

/**
 * KatalogAntragServiceTest
 */
public class KatalogAntragServiceTest {

	@Test
	void should_NotSendMail_when_HoneypotNotBlank() {

		// Arrange
		SchulkatalogAntrag antrag = SchulkatalogAntrag.createForTest("mail@web.de", "land", "ort", null, "schulname", null, "hagh");
		KatalogAntragService service = new KatalogAntragService();

		// Act
		boolean sent = service.validateAndSend(antrag);

		// Assert
		assertFalse(sent);
		SecurityIncidentRegistered eventObject = service.getSecurityIncident();
		assertNotNull(eventObject);
		assertNotNull(eventObject.occuredOn());
		assertEquals("KATALOG:SecurityIncidentRegistered", eventObject.typeName());
		assertEquals(
			"Honeypot des Schulkatalogantrags war nicht blank: SchulkatalogAntrag [email=mail@web.de, land=land, ort=ort, plz=null, schulname=schulname, strasseUndHausnummer=null, kleber=hagh]",
			eventObject.message());
	}

	@Test
	void should_SendMailAndFireEvent_when_AntragValid() throws JsonProcessingException {

		// Arrange
		SchulkatalogAntrag antrag = SchulkatalogAntrag.createForTest("mail@web.de", "land", "ort", null, "schulname", null,
			"");
		KatalogAntragService service = KatalogAntragService.createForTest();

		// Act
		boolean sent = service.validateAndSend(antrag);

		// Assert
		assertTrue(sent);
		assertNull(service.getSecurityIncident());

		KatalogAntragReceived eventObject = service.getKatalogAntragEventObject();
		assertNotNull(eventObject);
		assertNotNull(eventObject.occuredOn());
		assertEquals("KATALOG:KatalogAntragReceived", eventObject.typeName());

		String json = new ObjectMapper().writeValueAsString(eventObject.body());

		assertEquals(
			"{\"email\":\"mail@web.de\",\"land\":\"land\",\"ort\":\"ort\",\"plz\":null,\"schulname\":\"schulname\",\"strasseUndHausnummer\":null,\"kleber\":\"\"}",
			json);

	}

}
