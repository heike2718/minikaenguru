// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.teilnahmen.SchuleDetailsService;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleDetails;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;

/**
 * CheckCanRemoveSchuleServiceTest
 */
@QuarkusTest
public class CheckCanRemoveSchuleServiceTest {

	@InjectMock
	AuthorizationService veranstalterAuthService;

	@InjectMock
	SchuleDetailsService schuleDetailsService;

	@Inject
	CheckCanRemoveSchuleService service;

	@Test
	void should_kannLehrerVonSchuleAbmeldenCallTheAuthService() {

		// Arrange
		Identifier schuleID = new Identifier("SCHULE");
		Identifier lehrerID = new Identifier("uuid-lehrer");

		when(veranstalterAuthService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(),
			any()))
				.thenThrow(new AccessDeniedException("nö"));

		// Act
		try {

			service.kannLehrerVonSchuleAbmelden(lehrerID, schuleID);

			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			verify(veranstalterAuthService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(),
				any());
			assertEquals("nö", e.getMessage());
		}
	}

	@Test
	void should_kannLehrerVonSchuleAbmeldenReturnTrue_when_schuleNichtAngemeldet() {

		// Arrange
		Identifier schuleID = new Identifier("SCHULE");
		Identifier lehrerID = new Identifier("uuid-lehrer");

		when(veranstalterAuthService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(),
			any()))
				.thenReturn(Rolle.LEHRER);

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

		when(veranstalterAuthService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(),
			any()))
				.thenReturn(Rolle.LEHRER);

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

		when(veranstalterAuthService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(),
			any()))
				.thenReturn(Rolle.LEHRER);

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
