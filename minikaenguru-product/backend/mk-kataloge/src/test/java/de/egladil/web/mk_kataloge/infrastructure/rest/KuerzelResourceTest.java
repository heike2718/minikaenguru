// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.KatalogAPIApp;
import de.egladil.web.mk_kataloge.infrastructure.rest.KuerzelResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

/**
 * KuerzelResourceTest
 */
@QuarkusTest
@TestHTTPEndpoint(value = KuerzelResource.class)
public class KuerzelResourceTest {

	@ConfigProperty(name = "admin.secret")
	String adminSecret;

	@Test
	void should_generateKuerzelFuerSchuleUndOrtWork() {

		// Act
		ResponsePayload responsePayload = given()
			.when().header(KatalogAPIApp.SECRET_HEADER_NAME, adminSecret).get().then().statusCode(200).and().extract()
			.as(ResponsePayload.class);

		// Assert
		@SuppressWarnings("unchecked")
		Map<String, Object> data = (Map<String, Object>) responsePayload.getData();

		String kuerzelOrt = data.get("kuerzelOrt").toString();
		String kuerzelSchule = data.get("kuerzelSchule").toString();

		assertEquals(8, kuerzelOrt.length());
		assertEquals(8, kuerzelSchule.length());

		System.out.println(data.toString());

	}

	@Test
	void should_generateKuerzelFuerSchuleUndOrtReturn403_when_invalidSecret() {

		// Act
		ResponsePayload responsePayload = given()
			.when().header(KatalogAPIApp.SECRET_HEADER_NAME, "birne987").get().then().statusCode(403).and().extract()
			.as(ResponsePayload.class);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertFalse(responsePayload.getMessage().isOk());
		assertEquals("Netter Versuch, aber hierfür fehlt dir Ihnen die Berechtigung.", messagePayload.getMessage());
	}

}
