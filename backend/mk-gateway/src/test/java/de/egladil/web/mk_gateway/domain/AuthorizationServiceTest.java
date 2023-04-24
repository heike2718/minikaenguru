// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.user.UserRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.Person;
import de.egladil.web.mk_gateway.domain.veranstalter.Privatveranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.User;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

/**
 * AuthorizationServiceTest
 */
@QuarkusTest
public class AuthorizationServiceTest {

	/**
	 *
	 */
	private static final String USER_UUID = "agdqiqq";

	@InjectMock
	LoggableEventDelegate eventDelegate;

	@InjectMock
	VeranstalterRepository veranstalterRepository;

	@InjectMock
	UserRepository userRepository;

	@Inject
	AuthorizationService service;

	@Test
	void should_checkPermissionForTeilnahmenummerThrowException_when_UserNotFound() {

		// Arrange

		Identifier veranstalterId = new Identifier(USER_UUID);
		Identifier teilnahmeId = new Identifier("vhyxaksgk");

		when(userRepository.ofId(USER_UUID)).thenReturn(Optional.empty());
		when(veranstalterRepository.ofId(veranstalterId)).thenReturn(Optional.empty());

		// Act + Assert
		try {

			service.checkPermissionForTeilnahmenummerAndReturnRolle(veranstalterId, teilnahmeId, "kontext");
			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			verify(eventDelegate).fireSecurityEvent(any(), any());
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

		when(userRepository.ofId(USER_UUID)).thenReturn(Optional.of(user));
		when(veranstalterRepository.ofId(veranstalterId)).thenReturn(Optional.empty());

		// Act + Assert
		try {

			service.checkPermissionForTeilnahmenummerAndReturnRolle(veranstalterId, teilnahmeId, "kontext");
			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			verify(eventDelegate).fireSecurityEvent(any(), any());
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

		when(userRepository.ofId(USER_UUID)).thenReturn(Optional.of(user));

		Privatveranstalter veranstalter = new Privatveranstalter(new Person("azdqi", "Karl"), true,
			Arrays.asList(new Identifier[] { new Identifier("gagdgq") }));

		when(veranstalterRepository.ofId(veranstalterId)).thenReturn(Optional.of(veranstalter));

		// Act + Assert
		try {

			service.checkPermissionForTeilnahmenummerAndReturnRolle(veranstalterId, teilnahmeId, "kontext");
			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			verify(eventDelegate).fireSecurityEvent(any(), any());
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

		when(userRepository.ofId(USER_UUID)).thenReturn(Optional.of(user));

		Privatveranstalter veranstalter = new Privatveranstalter(new Person(USER_UUID, "Karl"), false,
			Arrays.asList(new Identifier[] { teilnahmeId }));

		when(veranstalterRepository.ofId(veranstalterId)).thenReturn(Optional.of(veranstalter));

		// Act
		assertEquals(Rolle.PRIVAT, service.checkPermissionForTeilnahmenummerAndReturnRolle(veranstalterId, teilnahmeId, "kontext"));
		verify(eventDelegate, never()).fireSecurityEvent(any(), any());
	}

	@Test
	void should_checkPermissionForTeilnahmenummerNotThrowException_when_admin() {

		Identifier veranstalterId = new Identifier(USER_UUID);
		Identifier teilnahmeId = new Identifier("vhyxaksgk");

		User user = new User();
		user.setUuid(USER_UUID);
		user.setRolle(Rolle.ADMIN);

		when(userRepository.ofId(USER_UUID)).thenReturn(Optional.of(user));
		when(veranstalterRepository.ofId(veranstalterId)).thenReturn(Optional.empty());

		assertEquals(Rolle.ADMIN, service.checkPermissionForTeilnahmenummerAndReturnRolle(veranstalterId, teilnahmeId, "kontext"));
		verify(eventDelegate, never()).fireSecurityEvent(any(), any());
	}

}
