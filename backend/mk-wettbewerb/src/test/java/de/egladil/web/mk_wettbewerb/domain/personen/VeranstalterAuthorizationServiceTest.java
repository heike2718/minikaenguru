// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.error.AccessDeniedException;

/**
 * VeranstalterAuthorizationServiceTest
 */
public class VeranstalterAuthorizationServiceTest {

	private VeranstalterRepository veranstalterRepository;

	private VeranstalterAuthorizationService service;

	@BeforeEach
	void setUp() {

		veranstalterRepository = Mockito.mock(VeranstalterRepository.class);
		service = VeranstalterAuthorizationService.createForTest(veranstalterRepository);

	}

	@Test
	void should_checkPermissionForTeilnahmenummerThrowException_when_VeranstalterNotFound() {

		// Arrrange
		Identifier veranstalterId = new Identifier("agdqiqq");
		Identifier teilnahmeId = new Identifier("vhyxaksgk");

		Mockito.when(veranstalterRepository.ofId(veranstalterId)).thenReturn(Optional.empty());

		// Act + Assert
		try {

			service.checkPermissionForTeilnahmenummer(veranstalterId, teilnahmeId);
			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			// nüscht
		}

	}

	@Test
	void should_checkPermissionForTeilnahmenummerThrowException_when_TeilnahmeIdNotFound() {

		// Arrrange
		Identifier veranstalterId = new Identifier("agdqiqq");
		Identifier teilnahmeId = new Identifier("vhyxaksgk");

		Privatperson veranstalter = new Privatperson(new Person("azdqi", "Karl"),
			Arrays.asList(new Identifier[] { new Identifier("gagdgq") }));

		Mockito.when(veranstalterRepository.ofId(veranstalterId)).thenReturn(Optional.of(veranstalter));

		// Act + Assert
		try {

			service.checkPermissionForTeilnahmenummer(veranstalterId, teilnahmeId);
			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			// nüscht
		}

	}

	@Test
	void should_checkPermissionForTeilnahmenummerNotThrowException_when_berechtigt() {

		// Arrrange
		Identifier veranstalterId = new Identifier("agdqiqq");
		Identifier teilnahmeId = new Identifier("vhyxaksgk");

		Privatperson veranstalter = new Privatperson(new Person("azdqi", "Karl"),
			Arrays.asList(new Identifier[] { teilnahmeId }));

		Mockito.when(veranstalterRepository.ofId(veranstalterId)).thenReturn(Optional.of(veranstalter));

		// Act
		assertTrue(service.checkPermissionForTeilnahmenummer(veranstalterId, teilnahmeId));

	}

}
