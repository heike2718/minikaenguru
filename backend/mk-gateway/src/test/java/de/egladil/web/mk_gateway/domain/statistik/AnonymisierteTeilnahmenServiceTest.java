// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import static org.junit.jupiter.api.Assertions.fail;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

/**
 * AnonymisierteTeilnahmenServiceTest
 */
@QuarkusTest
public class AnonymisierteTeilnahmenServiceTest {

	@InjectMock
	private AuthorizationService authService;

	@InjectMock
	private TeilnahmenRepository teilnahmenRepository;

	@InjectMock
	private ErfassungLoesungszettelInfoService erfassungLoesungszettelInoService;

	@Inject
	private AnonymisierteTeilnahmenService service;

	@Test
	void should_loadAnonymisierteTeilnahmen_call_AuthService() {

		// Arrange
		String teilnahmenummer = "ashichw";
		String userUuid = "ucgwug";
		Identifier userIdentifier = new Identifier(userUuid);
		Identifier teilnahmeID = new Identifier(teilnahmenummer);

		Mockito
			.when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(userIdentifier, teilnahmeID,
				"[loadAnonymisierteTeilnahmen - " + teilnahmenummer + "]"))
			.thenThrow(new AccessDeniedException());

		// Act
		try {

			service.loadAnonymisierteTeilnahmen(teilnahmenummer, userUuid);
			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			// nix
		}

	}

}
