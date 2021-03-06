// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.signup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.auth.events.LehrerCreated;
import de.egladil.web.mk_gateway.domain.auth.events.PrivatveranstalterCreated;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.user.UserRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.User;

/**
 * SignUpServiceTest
 */
public class SignUpServiceTest {

	@Test
	void should_CreateUserThrowException_when_ParameterNull() {

		try {

			SignUpService.createForTest(Mockito.mock(UserRepository.class)).createUser(null, false);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("signUpResourceOwner darf nicht null sein", e.getMessage());
		}
	}

	@Test
	void should_CreateUserNotCreateEvent_when_UserKnown() {

		// Arrange
		final String uuid = "hldshach";
		SignUpResourceOwner resourceOwner = new SignUpResourceOwner(uuid, "Kalle", "kalle@malle.es", "PRIVAT");

		User user = new User();
		user.setUuid(uuid);

		UserRepository userRepository = Mockito.mock(UserRepository.class);
		Mockito.when(userRepository.ofId(uuid)).thenReturn(Optional.of(user));

		SignUpService signupService = SignUpService.createForTest(userRepository);

		// Act
		User resultingUser = signupService.createUser(resourceOwner, false);

		// Assert
		assertNull(signupService.event());
		assertEquals(uuid, resultingUser.getUuid());
	}

	@Test
	void should_CreateUserCreatePrivatEventAndUser_when_UserUnknown() {

		// Arrange
		final String uuid = "hldshach";
		String fullName = "Kalle";
		SignUpResourceOwner resourceOwner = new SignUpResourceOwner(uuid, fullName, "kalle@malle.es", "PRIVAT");

		User user = new User();
		user.setUuid(uuid);

		UserRepository userRepository = Mockito.mock(UserRepository.class);
		SignUpService signupService = SignUpService.createForTest(userRepository);

		// Act
		User resultingUser = signupService.createUser(resourceOwner, false);

		// Assert
		assertEquals(uuid, resultingUser.getImportierteUuid());

		PrivatveranstalterCreated event = (PrivatveranstalterCreated) signupService.event();
		assertEquals(fullName, event.fullName());
		assertEquals("kalle@malle.es", event.email());
		assertEquals("PrivatveranstalterCreated", event.typeName());
		assertEquals(uuid, event.uuid());
		assertNotNull(event.occuredOn());
		assertEquals(Rolle.PRIVAT, event.rolle());
	}

	@Test
	void should_CreateUserCreateLehrerEventAndUser_when_UserUnknown() {

		// Arrange
		final String uuid = "hldshach";
		String fullName = "Kalle";
		SignUpResourceOwner resourceOwner = new SignUpResourceOwner(uuid, fullName, "kalle@malle.es", "LEHRER-NUGT6Z90");

		User user = new User();
		user.setUuid(uuid);

		UserRepository userRepository = Mockito.mock(UserRepository.class);
		SignUpService signupService = SignUpService.createForTest(userRepository);

		// Act
		User resultingUser = signupService.createUser(resourceOwner, false);

		// Assert
		assertEquals(uuid, resultingUser.getImportierteUuid());

		LehrerCreated event = (LehrerCreated) signupService.event();
		assertEquals(fullName, event.fullName());
		assertEquals("kalle@malle.es", event.email());
		assertEquals("LehrerCreated", event.typeName());
		assertEquals(uuid, event.uuid());
		assertNotNull(event.occuredOn());
		assertEquals("NUGT6Z90", event.schulkuerzel());
		assertEquals(Rolle.LEHRER, event.rolle());
	}

}
