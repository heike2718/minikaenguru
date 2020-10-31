// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.user.UserRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.Person;
import de.egladil.web.mk_gateway.domain.veranstalter.Privatveranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.User;

/**
 * AuthorizationServiceTest
 */
public class AuthorizationServiceTest {

	/**
	 *
	 */
	private static final String USER_UUID = "agdqiqq";

	private VeranstalterRepository veranstalterRepository;

	private UserRepository userRepository;

	private AuthorizationService service;

	@BeforeEach
	void setUp() {

		userRepository = Mockito.mock(UserRepository.class);
		veranstalterRepository = Mockito.mock(VeranstalterRepository.class);
		service = AuthorizationService.createForTest(veranstalterRepository, userRepository);

	}

	@Test
	void should_checkPermissionForTeilnahmenummerThrowException_when_UserNotFound() {

		// Arrange

		Identifier veranstalterId = new Identifier(USER_UUID);
		Identifier teilnahmeId = new Identifier("vhyxaksgk");

		Mockito.when(userRepository.ofId(USER_UUID)).thenReturn(Optional.empty());
		Mockito.when(veranstalterRepository.ofId(veranstalterId)).thenReturn(Optional.empty());

		// Act + Assert
		try {

			service.checkPermissionForTeilnahmenummer(veranstalterId, teilnahmeId, "kontext");
			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			assertNotNull(service.getSecurityIncidentRegistered());
		}

	}

	@Test
	void should_checkPermissionForTeilnahmenummerThrowException_when_VeranstalterNotFound() {

		// Arrange

		Identifier veranstalterId = new Identifier(USER_UUID);
		Identifier teilnahmeId = new Identifier("vhyxaksgk");
		User user = new User();
		user.setUuid(USER_UUID);
		user.setRolle(Rolle.LEHRER);

		Mockito.when(userRepository.ofId(USER_UUID)).thenReturn(Optional.of(user));
		Mockito.when(veranstalterRepository.ofId(veranstalterId)).thenReturn(Optional.empty());

		// Act + Assert
		try {

			service.checkPermissionForTeilnahmenummer(veranstalterId, teilnahmeId, "kontext");
			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			assertNotNull(service.getSecurityIncidentRegistered());
		}

	}

	@Test
	void should_checkPermissionForTeilnahmenummerThrowException_when_TeilnahmeIdNotFound() {

		// Arrrange
		Identifier veranstalterId = new Identifier(USER_UUID);
		Identifier teilnahmeId = new Identifier("vhyxaksgk");

		User user = new User();
		user.setUuid(USER_UUID);
		user.setRolle(Rolle.LEHRER);

		Mockito.when(userRepository.ofId(USER_UUID)).thenReturn(Optional.of(user));

		Privatveranstalter veranstalter = new Privatveranstalter(new Person("azdqi", "Karl"), true,
			Arrays.asList(new Identifier[] { new Identifier("gagdgq") }));

		Mockito.when(veranstalterRepository.ofId(veranstalterId)).thenReturn(Optional.of(veranstalter));

		// Act + Assert
		try {

			service.checkPermissionForTeilnahmenummer(veranstalterId, teilnahmeId, "kontext");
			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			assertNotNull(service.getSecurityIncidentRegistered());
		}

	}

	@Test
	void should_checkPermissionForTeilnahmenummerNotThrowException_when_berechtigt() {

		// Arrrange
		Identifier veranstalterId = new Identifier(USER_UUID);
		Identifier teilnahmeId = new Identifier("vhyxaksgk");

		User user = new User();
		user.setUuid(USER_UUID);
		user.setRolle(Rolle.PRIVAT);

		Mockito.when(userRepository.ofId(USER_UUID)).thenReturn(Optional.of(user));

		Privatveranstalter veranstalter = new Privatveranstalter(new Person(USER_UUID, "Karl"), false,
			Arrays.asList(new Identifier[] { teilnahmeId }));

		Mockito.when(veranstalterRepository.ofId(veranstalterId)).thenReturn(Optional.of(veranstalter));

		// Act
		assertTrue(service.checkPermissionForTeilnahmenummer(veranstalterId, teilnahmeId, "kontext"));
		assertNull(service.getSecurityIncidentRegistered());
	}

	@Test
	void should_checkPermissionForTeilnahmenummerNotThrowException_when_admin() {

		Identifier veranstalterId = new Identifier(USER_UUID);
		Identifier teilnahmeId = new Identifier("vhyxaksgk");

		User user = new User();
		user.setUuid(USER_UUID);
		user.setRolle(Rolle.ADMIN);

		Mockito.when(userRepository.ofId(USER_UUID)).thenReturn(Optional.of(user));
		Mockito.when(veranstalterRepository.ofId(veranstalterId)).thenReturn(Optional.empty());

		assertTrue(service.checkPermissionForTeilnahmenummer(veranstalterId, teilnahmeId, "kontext"));
		assertNull(service.getSecurityIncidentRegistered());
	}

}
