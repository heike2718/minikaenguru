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

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.KatalogAPIApp;
import de.egladil.web.mk_kataloge.domain.KatalogItem;
import de.egladil.web.mk_kataloge.infrastructure.rest.KatalogeResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

/**
 * KatalogeResourceTest
 */
@QuarkusTest
@TestHTTPEndpoint(value = KatalogeResource.class)
public class KatalogeResourceTest {

	@ConfigProperty(name = "admin.secret")
	String adminSecret;

	@Test
	void should_loadLaenderWork() {

		// Act
		ResponsePayload responsePayload = given()
			.when()
			.header(KatalogAPIApp.UUID_HEADER_NAME, "abcdef-123450")
			.header(KatalogAPIApp.SECRET_HEADER_NAME, adminSecret)
			.get("laender").then().statusCode(200).and().extract()
			.as(ResponsePayload.class);

		// Assert
		System.out.println(responsePayload.getMessage().toString());
		assertTrue(responsePayload.getMessage().isOk());

		@SuppressWarnings("unchecked")
		List<KatalogItem> trefferliste = (List<KatalogItem>) responsePayload.getData();

		assertEquals(27, trefferliste.size());

	}

	@Test
	void should_loadLaenderReturn_when_secretFalse() {

		// Act
		ResponsePayload responsePayload = given()
			.when()
			.header(KatalogAPIApp.UUID_HEADER_NAME, "abcdef-123450")
			.header(KatalogAPIApp.SECRET_HEADER_NAME, "birne123")
			.get("laender").then().statusCode(403).and().extract()
			.as(ResponsePayload.class);

		// Assert
		System.out.println(responsePayload.getMessage().toString());
		assertFalse(responsePayload.getMessage().isOk());

		assertEquals("Netter Versuch, aber leider keine Berechtigung", responsePayload.getMessage().getMessage());
	}

}
