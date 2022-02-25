// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;

/**
 * AnonymisierteTeilnahmenServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class AnonymisierteTeilnahmenServiceTest {

	@Mock
	private AuthorizationService authService;

	@Mock
	private TeilnahmenRepository teilnahmenRepository;

	@Mock
	private ErfassungLoesungszettelInfoService erfassungLoesungszettelInoService;

	@InjectMocks
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
