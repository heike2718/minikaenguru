// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.persistence.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.KatalogItem;
import de.egladil.web.mk_kataloge.infrastructure.rest.KatalogsucheResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

/**
 * KatalogsucheResourceTest
 */
@QuarkusTest
@TestHTTPEndpoint(value = KatalogsucheResource.class)
public class KatalogsucheResourceTest {

	@Test
	void should_findItemsWork_when_type_LAND() {

		// Act
		ResponsePayload responsePayload = given()
			.when().queryParam("search", "sachs").get("/global/LAND").then().statusCode(200).and().extract()
			.as(ResponsePayload.class);

		// Assert
		assertTrue(responsePayload.getMessage().isOk());

		@SuppressWarnings("unchecked")
		List<KatalogItem> trefferliste = (List<KatalogItem>) responsePayload.getData();

		assertEquals(2, trefferliste.size());

	}

	@Test
	void should_findItemsWork_when_type_ORT() {

		// Act
		ResponsePayload responsePayload = given()
			.when().queryParam("search", "wies").get("/global/ORT").then().statusCode(200).and().extract()
			.as(ResponsePayload.class);

		// Assert
		assertTrue(responsePayload.getMessage().isOk());

		@SuppressWarnings("unchecked")
		List<KatalogItem> trefferliste = (List<KatalogItem>) responsePayload.getData();

		assertEquals(15, trefferliste.size());

	}

	@Test
	void should_findItemsWork_when_type_SCHULE() {

		// Act
		ResponsePayload responsePayload = given()
			.when().queryParam("search", "dies").get("/global/SCHULE").then().statusCode(200).and().extract()
			.as(ResponsePayload.class);

		// Assert
		assertTrue(responsePayload.getMessage().isOk());

		@SuppressWarnings("unchecked")
		List<KatalogItem> trefferliste = (List<KatalogItem>) responsePayload.getData();

		assertEquals(51, trefferliste.size());

	}

	@Test
	void should_findItemsWork_when_type_HALLO() {

		// Act
		ResponsePayload responsePayload = given()
			.when().queryParam("search", "dies").get("/global/HALLO").then().statusCode(400).and().extract()
			.as(ResponsePayload.class);

		// Assert
		assertFalse(responsePayload.getMessage().isOk());
		assertEquals("Fehlerhafte URL: typ=HALLO", responsePayload.getMessage().getMessage());

	}

	@Test
	void should_findOrteInLandWork() {

		// Act
		ResponsePayload responsePayload = given()
			.when().get("/laender/DE-HE/orte?search=Bad").then().statusCode(200).and().extract()
			.as(ResponsePayload.class);

		// Assert
		System.out.println(responsePayload.getMessage().toString());
		assertTrue(responsePayload.getMessage().isOk());

		@SuppressWarnings("unchecked")
		List<KatalogItem> trefferliste = (List<KatalogItem>) responsePayload.getData();

		assertEquals(18, trefferliste.size());

	}

	@Test
	void should_findOrteInLandReturn400_when_zuVieleTreffer() {

		// Act
		given()
			.when().get("/laender/DE-HE/orte").then().statusCode(400);

	}

	@Test
	void should_findSchulenInLandWork() {

		// Act
		ResponsePayload responsePayload = given()
			.when().get("/orte/OQTZCILD/schulen?search=rich").then().statusCode(200).and().extract()
			.as(ResponsePayload.class);

		// Assert
		System.out.println(responsePayload.getMessage().toString());
		assertTrue(responsePayload.getMessage().isOk());

		@SuppressWarnings("unchecked")
		List<KatalogItem> trefferliste = (List<KatalogItem>) responsePayload.getData();

		assertEquals(4, trefferliste.size());

	}

	@Test
	void should_findSchulenReturn400_when_searchtermEmpty() {

		given()
			.when()
			.get(
				"/orte/OQTZCILD/schulen?search=")
			.then().statusCode(400);

	}

	@Test
	void should_findSchulenReturn400_when_searchtermTooLong() {

		// Act
		ResponsePayload responsePayload = given()
			.when()
			.get(
				"/orte/OQTZCILD/schulen?search=UnrecognizedPropertyExceptionUnrecognizedPropertyExceptionUnrecognizedPropertyExceptionUnrecognizedPropertyException")
			.then().statusCode(400).and().extract()
			.as(ResponsePayload.class);

		// Assert
		System.out.println(responsePayload.getMessage().toString());
		assertFalse(responsePayload.getMessage().isOk());
		assertEquals("Die Eingaben sind nicht korrekt.", responsePayload.getMessage().getMessage());

	}

}
