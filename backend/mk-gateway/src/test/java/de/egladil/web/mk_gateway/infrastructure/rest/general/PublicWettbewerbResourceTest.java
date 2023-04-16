// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.response.Response;

/**
 * PublicWettbewerbResourceTest
 */
@QuarkusTest
@TestHTTPEndpoint(PublicWettbewerbResource.class)
public class PublicWettbewerbResourceTest {

	@InjectMock
	WettbewerbService wettbewerbService;

	@Test
	void should_getAktuellenWettbewerbThrowNotFound_when_noWettbewerb() {

		// Arrange
		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.empty());

		// Act
		Response response = given()
			.when().get("/aktueller");

		// Assert
		assertEquals(404, response.getStatusCode());

	}

}
