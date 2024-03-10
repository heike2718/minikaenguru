// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.profiles.FullDatabaseTestProfile;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;

/**
 * HeartbeatResourceTest
 */
@QuarkusTest
@TestProfile(value = FullDatabaseTestProfile.class)
@TestHTTPEndpoint(value = HeartbeatResource.class)
public class HeartbeatResourceTest {

	@ConfigProperty(name = "heartbeat.id")
	String secret;

	@Test
	void should_checkReturn401_when_invalidCredentials() {

		// Act
		ResponsePayload responsePayload = given()
			.when()
			.header("X-HEARTBEAT-ID", "abcdef-123450")
			.accept(ContentType.JSON)
			.get().then().statusCode(401).and().extract()
			.as(ResponsePayload.class);

		// Assert
		System.out.println(responsePayload.getMessage().toString());
		assertFalse(responsePayload.getMessage().isOk());

		assertEquals("keine Berechtigung für diese Resource", responsePayload.getMessage().getMessage());

	}

	@Test
	void should_checkReturn200_when_allesOK() {

		// Act
		ResponsePayload responsePayload = given()
			.when()
			.header("X-HEARTBEAT-ID", secret)
			.accept(ContentType.JSON)
			.get().then().statusCode(200).and().extract()
			.as(ResponsePayload.class);

		// Assert
		System.out.println(responsePayload.getMessage().toString());
		assertTrue(responsePayload.getMessage().isOk());

	}
}
