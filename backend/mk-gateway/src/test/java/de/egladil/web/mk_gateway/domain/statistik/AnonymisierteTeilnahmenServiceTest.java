// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import static org.junit.Assert.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;

/**
 * AnonymisierteTeilnahmenServiceTest
 */
public class AnonymisierteTeilnahmenServiceTest {

	private AuthorizationService authService;

	private LoesungszettelRepository loesungszettelRepository;

	private TeilnahmenRepository teilnahmenRepository;

	private AnonymisierteTeilnahmenService service;

	@BeforeEach
	void setUp() throws Exception {

		authService = Mockito.mock(AuthorizationService.class);
		loesungszettelRepository = Mockito.mock(LoesungszettelRepository.class);
		teilnahmenRepository = Mockito.mock(TeilnahmenRepository.class);

		service = AnonymisierteTeilnahmenService.createForTest(authService, teilnahmenRepository, loesungszettelRepository);

	}

	@Test
	void should_loadAnonymisierteTeilnahmen_call_AuthService() {

		// Arrange
		String teilnahmenummer = "ashichw";
		String userUuid = "ucgwug";
		Identifier userIdentifier = new Identifier(userUuid);
		Identifier teilnahmeID = new Identifier(teilnahmenummer);

		Mockito.when(authService.checkPermissionForTeilnahmenummer(userIdentifier, teilnahmeID))
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
