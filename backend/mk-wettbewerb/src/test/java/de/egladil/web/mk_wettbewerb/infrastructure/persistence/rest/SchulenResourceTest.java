// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.rest;

import static org.junit.Assert.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.error.AccessDeniedException;
import de.egladil.web.mk_wettbewerb.domain.personen.VeranstalterAuthorizationService;
import de.egladil.web.mk_wettbewerb.infrastructure.rest.SchulenResource;

/**
 * SchulenResourceTest
 */
public class SchulenResourceTest {

	private VeranstalterAuthorizationService authService;

	private SchulenResource resource;

	@BeforeEach
	void setUp() {

		authService = Mockito.mock(VeranstalterAuthorizationService.class);
		resource = SchulenResource.createForPermissionTest(authService);

	}

	@Test
	void should_getSchuleDetails_callAuthService() {

		// Arrange
		String schulkuerzel = "bjkasgca";
		String uuid = "hklahcha";

		Identifier lehrerId = new Identifier(uuid);
		Identifier teilnahmeId = new Identifier(schulkuerzel);

		Mockito.when(authService.checkPermissionForTeilnahmenummer(lehrerId, teilnahmeId)).thenThrow(new AccessDeniedException());

		// Act
		try {

			resource.getSchuleDetails(schulkuerzel, uuid);
			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			// nüscht
		}

	}

}
