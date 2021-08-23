// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.signup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.egladil.web.mk_gateway.domain.auth.events.LehrerCreated;
import de.egladil.web.mk_gateway.domain.auth.events.PrivatveranstalterCreated;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.user.UserRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.SynchronizeVeranstalterService;
import de.egladil.web.mk_gateway.infrastructure.messaging.PropagateUserService;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.User;

/**
 * SignUpServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class SignUpServiceTest {

	@Mock
	UserRepository userRepository;

	@Mock
	SynchronizeVeranstalterService syncVeranstalterService;

	@Mock
	DomainEventHandler domainEventHandler;

	@Mock
	PropagateUserService propagateUserService;

	@InjectMocks
	SignUpService service;

	@Test
	void should_CreateUserThrowException_when_ParameterNull() {

		try {

			service.createUserAndVeranstalter(null);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("signUpResourceOwner darf nicht null sein", e.getMessage());
			verify(userRepository, never()).ofId(any());
			verify(userRepository, never()).addUser(any());
			verify(domainEventHandler, never()).handleEvent(any());
			verify(propagateUserService, never()).handleDomainEvent(any());
		}
	}

	@Test
	void should_CreateUserNotCreateEvent_when_UserKnown() {

		// Arrange
		final String uuid = "hldshach";
		SignUpResourceOwner resourceOwner = new SignUpResourceOwner(uuid, "Kalle", "kalle@malle.es", "PRIVAT");

		User user = new User();
		user.setUuid(uuid);
		user.setRolle(Rolle.PRIVAT);

		when(userRepository.ofId(uuid)).thenReturn(Optional.of(user));

		// Act
		User resultingUser = service.createUserAndVeranstalter(resourceOwner);

		// Assert
		assertEquals(uuid, resultingUser.getUuid());

		verify(userRepository).ofId(uuid);
		verify(userRepository, never()).addUser(any());
		verify(domainEventHandler, never()).handleEvent(any());
		verify(propagateUserService, never()).handleDomainEvent(any());
	}

	@Test
	void should_CreateUserCreatePrivatEventAndUser_when_UserUnknown() {

		// Arrange
		final String uuid = "hldshach";
		String fullName = "Kalle";
		SignUpResourceOwner resourceOwner = new SignUpResourceOwner(uuid, fullName, "kalle@malle.es", "PRIVAT");

		User user = new User();
		user.setUuid(uuid);
		user.setRolle(Rolle.PRIVAT);

		when(userRepository.ofId(uuid)).thenReturn(Optional.empty());
		when(userRepository.addUser(any())).thenReturn(user);
		doNothing().when(domainEventHandler).handleEvent(any());
		doNothing().when(propagateUserService).handleDomainEvent(any());

		// Act
		User resultingUser = service.createUserAndVeranstalter(resourceOwner);

		// Assert
		assertEquals(uuid, resultingUser.getImportierteUuid());

		PrivatveranstalterCreated event = (PrivatveranstalterCreated) service.event();
		assertEquals(fullName, event.fullName());
		assertEquals("kalle@malle.es", event.email());
		assertEquals("PrivatveranstalterCreated", event.typeName());
		assertEquals(uuid, event.uuid());
		assertNotNull(event.occuredOn());
		assertEquals(Rolle.PRIVAT, event.rolle());

		verify(userRepository).addUser(any());
		verify(domainEventHandler).handleEvent(any());
		verify(propagateUserService).handleDomainEvent(any());
	}

	@Test
	void should_CreateUserCreateLehrerEventAndUser_when_UserUnknown() {

		// Arrange
		final String uuid = "hldshach";
		String fullName = "Kalle";
		SignUpResourceOwner resourceOwner = new SignUpResourceOwner(uuid, fullName, "kalle@malle.es", "LEHRER-NUGT6Z90");

		User user = new User();
		user.setUuid(uuid);
		user.setRolle(Rolle.LEHRER);

		when(userRepository.ofId(uuid)).thenReturn(Optional.empty());
		when(userRepository.addUser(any())).thenReturn(user);
		doNothing().when(domainEventHandler).handleEvent(any());
		doNothing().when(propagateUserService).handleDomainEvent(any());

		// Act
		User resultingUser = service.createUserAndVeranstalter(resourceOwner);

		// Assert
		assertEquals(uuid, resultingUser.getImportierteUuid());

		LehrerCreated event = (LehrerCreated) service.event();
		assertEquals(fullName, event.fullName());
		assertEquals("kalle@malle.es", event.email());
		assertEquals("LehrerCreated", event.typeName());
		assertEquals(uuid, event.uuid());
		assertNotNull(event.occuredOn());
		assertEquals("NUGT6Z90", event.schulkuerzel());
		assertEquals(Rolle.LEHRER, event.rolle());

		verify(userRepository).addUser(any());
		verify(domainEventHandler).handleEvent(any());
		verify(propagateUserService).handleDomainEvent(any());
	}

}
