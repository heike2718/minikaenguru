// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.persistence.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.apimodel.SchulkatalogAntrag;
import de.egladil.web.mk_kataloge.domain.katalogantrag.KatalogAntragService;
import de.egladil.web.mk_kataloge.infrastructure.rest.KatalogAntragResource;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

/**
 * KatalogAntragResourceTest
 */
@QuarkusTest
@TestHTTPEndpoint(value = KatalogAntragResource.class)
public class KatalogAntragResourceTest {

	@InjectMock
	KatalogAntragService service;

	@Test
	void should_submitSchuleWork() {

		// Arrange
		when(service.validateAndSend(any(SchulkatalogAntrag.class))).thenReturn(Boolean.TRUE);
		SchulkatalogAntrag antrag = new SchulkatalogAntrag().withEmail("ich@web.de").withKleber("").withLand("Hessen").withOrt("Wiesbaden").withSchulname("Hans-Walter-Schule").withPlz("55252").withStrasseUndHausnummer("Färcherweg 6");

		// Act
		ResponsePayload responsePayload = given().contentType(ContentType.JSON)
			.when().body(antrag).post().then().statusCode(200).and().extract().as(ResponsePayload.class);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertTrue(messagePayload.isOk());
		assertEquals("Vielen Dank. Der Katalogeintrag wird zeitnah erstellt. Sie wurden per Email informiert. Bitte schauen Sie in Ihrem Postfach nach.", messagePayload.getMessage());
	}

	@Test
	void should_submitSchuleReturn200_when_invalidPaylaod() {

		// Arrange
		when(service.validateAndSend(any(SchulkatalogAntrag.class))).thenReturn(Boolean.FALSE);
		SchulkatalogAntrag antrag = new SchulkatalogAntrag().withEmail("ich@web.de").withKleber("").withLand("Hessen").withOrt("Wiesbaden").withSchulname("Hans-Walter-Schule").withPlz("55252").withStrasseUndHausnummer("Färcherweg 6");

		// Act
		ResponsePayload responsePayload = given().contentType(ContentType.JSON)
			.when().body(antrag).post().then().statusCode(200).and().extract().as(ResponsePayload.class);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertTrue(messagePayload.isOk());
		assertEquals("Vielen Dank. Der Katalogeintrag wird zeitnah erstellt. Sie wurden per Email informiert. Bitte schauen Sie in Ihrem Postfach nach.", messagePayload.getMessage());
	}
}
