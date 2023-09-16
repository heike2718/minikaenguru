// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.GuiVersionService;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

/**
 * GuiVersionResourceTest
 */
@QuarkusTest
@TestHTTPEndpoint(GuiVersionResource.class)
public class GuiVersionResourceTest {

	@InjectMock
	GuiVersionService guiVersionService;

	@InjectMock
	DevDelayService delayService;

	@Test
	void should_returnTheExpectedVersion() {

		// Arrange
		String expectedValue = "3.1.4";
		when(guiVersionService.getExcpectedGuiVersion()).thenReturn(expectedValue);
		doNothing().when(delayService).pause();

		// Act
		ResponsePayload responsePayload = given()
			.when().get("/").then().statusCode(200).and().extract().as(ResponsePayload.class);

		MessagePayload messagePayload = responsePayload.getMessage();

		// Assert
		assertTrue(messagePayload.isOk());
		assertEquals(expectedValue, messagePayload.getMessage());
	}

}
