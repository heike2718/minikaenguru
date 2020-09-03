// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import static org.junit.Assert.fail;

import javax.ws.rs.core.SecurityContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.auth.session.LoggedInUser;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterAuthorizationService;

/**
 * VeranstalterLehrerResourceTest
 */
public class VeranstalterLehrerResourceTest {

	/**
	 *
	 */
	private static final String LEHRER_UUID = "hklahcha";

	private VeranstalterAuthorizationService authService;

	private VeranstalterLehrerResource resource;

	@BeforeEach
	void setUp() {

		authService = Mockito.mock(VeranstalterAuthorizationService.class);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);

		LoggedInUser loggedInUser = LoggedInUser.create(LEHRER_UUID, Rolle.LEHRER, "Alter Verwalter", "dgqwudugö");
		Mockito.when(securityContext.getUserPrincipal()).thenReturn(loggedInUser);

		resource = VeranstalterLehrerResource.createForPermissionTest(authService, securityContext);

	}

	@Test
	void should_getSchuleDetails_callAuthService() {

		// Arrange
		String schulkuerzel = "bjkasgca";

		Identifier lehrerId = new Identifier(LEHRER_UUID);
		Identifier teilnahmeId = new Identifier(schulkuerzel);

		Mockito.when(authService.checkPermissionForTeilnahmenummer(lehrerId, teilnahmeId)).thenThrow(new AccessDeniedException());

		// Act
		try {

			resource.getSchuleDetails(schulkuerzel);
			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			// nüscht
		}

	}

}
