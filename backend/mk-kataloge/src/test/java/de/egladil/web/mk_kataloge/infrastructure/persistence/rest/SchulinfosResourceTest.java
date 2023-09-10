// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.persistence.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.apimodel.SchuleAPIModel;
import de.egladil.web.mk_kataloge.infrastructure.rest.SchulinfosResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

/**
 * SchulinfosResourceTest
 */
@QuarkusTest
@TestHTTPEndpoint(value = SchulinfosResource.class)
public class SchulinfosResourceTest {

	@Test
	void should_findSchulenMitKuerzelWork() {

		// Act
		ResponsePayload responsePayload = given()
			.when().get("/0058XLE5,00PNGSLB").then().statusCode(200).and().extract().as(ResponsePayload.class);

		// Assert
		assertTrue(responsePayload.getMessage().isOk());

		@SuppressWarnings("unchecked")
		List<SchuleAPIModel> trefferliste = (List<SchuleAPIModel>) responsePayload.getData();

		assertEquals(2, trefferliste.size());
	}

	@Test
	void should_loadSchulenMitKuerzelWork() {

		// Act
		ResponsePayload responsePayload = given().contentType(ContentType.TEXT)
			.when().body("0058XLE5,00PNGSLB").post().then().statusCode(200).and().extract().as(ResponsePayload.class);

		// Assert
		assertTrue(responsePayload.getMessage().isOk());

		@SuppressWarnings("unchecked")
		List<SchuleAPIModel> trefferliste = (List<SchuleAPIModel>) responsePayload.getData();

		assertEquals(2, trefferliste.size());
	}

}
