// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.teilnahmen.SchuleDetailsService;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleDetails;

/**
 * CheckCanRemoveSchuleServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class CheckCanRemoveSchuleServiceTest {

	@Mock
	AuthorizationService veranstalterAuthService;

	@Mock
	SchuleDetailsService schuleDetailsService;

	@InjectMocks
	CheckCanRemoveSchuleService service;

	@Test
	void should_kannLehrerVonSchuleAbmeldenCallTheAuthService() {

		// Arrange
		Identifier schuleID = new Identifier("SCHULE");
		Identifier lehrerID = new Identifier("uuid-lehrer");

		when(veranstalterAuthService.checkPermissionForTeilnahmenummer(any(), any(),
			any()))
				.thenThrow(new AccessDeniedException("nö"));

		// Act
		try {

			service.kannLehrerVonSchuleAbmelden(lehrerID, schuleID);

			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			verify(veranstalterAuthService, times(1)).checkPermissionForTeilnahmenummer(any(), any(),
				any());
			assertEquals("nö", e.getMessage());
		}
	}

	@Test
	void should_kannLehrerVonSchuleAbmeldenReturnTrue_when_schuleNichtAngemeldet() {

		// Arrange
		Identifier schuleID = new Identifier("SCHULE");
		Identifier lehrerID = new Identifier("uuid-lehrer");

		when(veranstalterAuthService.checkPermissionForTeilnahmenummer(any(), any(),
			any()))
				.thenReturn(Boolean.TRUE);

		SchuleDetails details = new SchuleDetails(schuleID.identifier());

		when(schuleDetailsService.ermittleSchuldetails(schuleID, lehrerID)).thenReturn(details);

		// Act
		boolean result = service.kannLehrerVonSchuleAbmelden(lehrerID, schuleID);

		// Assert
		assertTrue(result);
	}

	@Test
	void should_kannLehrerVonSchuleAbmeldenReturnTrue_when_schuleAngemeldetAberKollegen() {

		// Arrange
		Identifier schuleID = new Identifier("SCHULE");
		Identifier lehrerID = new Identifier("uuid-lehrer");

		when(veranstalterAuthService.checkPermissionForTeilnahmenummer(any(), any(),
			any()))
				.thenReturn(Boolean.TRUE);

		Kollege kollege = new Kollege(lehrerID.identifier(), "ich");
		List<Kollege> kollegen = new ArrayList<>();
		kollegen.add(new Kollege("1", "bjdh"));

		SchuleDetails details = new SchuleDetails(schuleID.identifier()).withAngemeldetDurch(kollege).withKollegen(kollegen);

		when(schuleDetailsService.ermittleSchuldetails(schuleID, lehrerID)).thenReturn(details);

		// Act
		boolean result = service.kannLehrerVonSchuleAbmelden(lehrerID, schuleID);

		// Assert
		assertTrue(result);
	}

	@Test
	void should_kannLehrerVonSchuleAbmeldenReturnFalse_when_schuleAngemeldetKeineKollegen() {

		// Arrange
		Identifier schuleID = new Identifier("SCHULE");
		Identifier lehrerID = new Identifier("uuid-lehrer");

		when(veranstalterAuthService.checkPermissionForTeilnahmenummer(any(), any(),
			any()))
				.thenReturn(Boolean.TRUE);

		Kollege kollege = new Kollege(lehrerID.identifier(), "ich");
		List<Kollege> kollegen = new ArrayList<>();

		SchuleDetails details = new SchuleDetails(schuleID.identifier()).withAngemeldetDurch(kollege).withKollegen(kollegen);

		when(schuleDetailsService.ermittleSchuldetails(schuleID, lehrerID)).thenReturn(details);

		// Act
		boolean result = service.kannLehrerVonSchuleAbmelden(lehrerID, schuleID);

		// Assert
		assertFalse(result);
	}

}
