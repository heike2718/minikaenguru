// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.persistence.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.infrastructure.rest.VersionResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

/**
 * VersionResourceTest
 */
@QuarkusTest
@TestHTTPEndpoint(value = VersionResource.class)
public class VersionResourceTest {

	@ConfigProperty(name = "quarkus.application.version")
	String version;

	@Test
	void should_getVersionWork() {

		// Arrange
		String expected = version + " - dev";

		// Act
		ResponsePayload responsePayload = given()
			.when().get("/").then().statusCode(200).and().extract().as(ResponsePayload.class);

		MessagePayload messagePayload = responsePayload.getMessage();

		// Assert
		assertTrue(messagePayload.isOk());
		assertEquals(expected, messagePayload.getMessage());

	}

}
