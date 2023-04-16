// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.auth.session.LoggedInUser;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.LehrerService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.response.Response;

/**
 * LehrerResourceTest
 */
@QuarkusTest
@TestHTTPEndpoint(LehrerResource.class)
public class LehrerResourceTest {

	/**
	 *
	 */
	private static final String LEHRER_UUID = "hklahcha";

	LoggedInUser loggedInUser;

	@Context
	SecurityContext securityContext;

	@InjectMock
	AuthorizationService veranstalterAuthService;

	@InjectMock
	private LehrerService lehrerService;

	@BeforeEach
	void setUp() {

		loggedInUser = LoggedInUser.create(LEHRER_UUID, Rolle.LEHRER, "Alter Verwalter", "dgqwudugö");

	}

	@Test
	void should_getSchuleDetails_callAuthService() {

		// Arrange
		String schulkuerzel = "bjkasgca";

		Identifier lehrerId = new Identifier(LEHRER_UUID);
		Identifier teilnahmeId = new Identifier(schulkuerzel);

		when(veranstalterAuthService.checkPermissionForTeilnahmenummerAndReturnRolle(lehrerId, teilnahmeId,
			"[getSchuleDetails - " + schulkuerzel + "]"))
				.thenThrow(new AccessDeniedException());

		Response response = given()
			.when().get("schulen/bjkasgca/details");

		// Assert
		assertEquals(401, response.getStatusCode());

	}

}
