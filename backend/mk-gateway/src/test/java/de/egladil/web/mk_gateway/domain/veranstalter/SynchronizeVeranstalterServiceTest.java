// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MessagingAuthException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.user.UserRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.api.ChangeUserCommand;
import de.egladil.web.mk_gateway.infrastructure.messaging.HandshakeAck;
import de.egladil.web.mk_gateway.infrastructure.messaging.LoescheVeranstalterCommand;
import de.egladil.web.mk_gateway.infrastructure.messaging.SyncHandshake;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

/**
 * SynchronizeVeranstalterServiceTest
 */
@QuarkusTest
public class SynchronizeVeranstalterServiceTest {

	/**
	 *
	 */
	private static final String CLIENT_ID = "GUIguiggudwgugwo";

	private static final String NONCE = "guagfuowge";

	private static final String UUID_LEHRER_1 = "UUID_LEHRER_1";

	@InjectMock
	DomainEventHandler domainEventHandler;

	@InjectMock
	LoggableEventDelegate eventDelegate;

	@InjectMock
	VeranstalterRepository veranstalterRepository;

	@InjectMock
	UserRepository userRepository;

	@InjectMock
	SchulkollegienService schulkollegienService;

	@Inject
	private SynchronizeVeranstalterService service;

	private SyncHandshake handshake = SyncHandshake.create(CLIENT_ID, NONCE);

	@Test
	void should_createHandshakeAck_return_validSyncToken() {

		// Act
		HandshakeAck ack = service.createHandshakeAck(handshake);

		// Assert
		assertEquals(NONCE, ack.nonce());
		assertNotNull(ack.syncToken());

		service.verifySession(ack.syncToken());

	}

	@Test
	void should_changeVeranstalterDaten_invalidateTheSyncToken() {

		// Arrange
		HandshakeAck ack = service.createHandshakeAck(handshake);

		String expectedMessage = "Aufruf sync mit falscher sessionId '" + ack.syncToken() + "'";

		ChangeUserCommand cmd = new ChangeUserCommand().withEmail("email@web.de").withNachname("Bert").withUuid("ABSDE")
			.withVorname("Hans Herbert").withSyncToken(ack.syncToken());

		service.changeVeranstalterDaten(cmd);

		try {

			// Act
			service.verifySession(ack.syncToken());
			fail("keine MessagingAuthException");
		} catch (MessagingAuthException e) {

			// Assert
			assertEquals(expectedMessage, e.getMessage());

			verify(eventDelegate).fireSecurityEvent(any(), any());
		}
	}

	@Test
	void should_changeVeranstalterDaten_change_LehrerWhenExists() {

		// Arrange
		HandshakeAck ack = service.createHandshakeAck(handshake);

		Veranstalter veranstalter = new Lehrer(new Person(UUID_LEHRER_1, "Hans Weißwurst"), false,
			Collections.singletonList(new Identifier("A12345678")));

		when(veranstalterRepository.ofId(new Identifier(UUID_LEHRER_1))).thenReturn(Optional.of(veranstalter));

		ChangeUserCommand cmd = new ChangeUserCommand().withEmail("hans-weisswurst@web.de").withNachname("Weißwurst")
			.withUuid(UUID_LEHRER_1)
			.withVorname("Hans").withSyncToken(ack.syncToken());

		// Act
		service.changeVeranstalterDaten(cmd);

		// Assert
		verify(veranstalterRepository).changeVeranstalter(veranstalter);
		verify(schulkollegienService, times(1)).handleLehrerChanged(any());
	}

	@Test
	void should_loescheVeranstalter_invalidateTheSyncToken() {

		// Arrange
		HandshakeAck ack = service.createHandshakeAck(handshake);

		String expectedMessage = "Aufruf sync mit falscher sessionId '" + ack.syncToken() + "'";

		LoescheVeranstalterCommand cmd = new LoescheVeranstalterCommand().withUuid("ABSDE")
			.withSyncToken(ack.syncToken());

		service.loescheVeranstalter(cmd);

		try {

			// Act
			service.verifySession(ack.syncToken());
			fail("keine MessagingAuthException");
		} catch (MessagingAuthException e) {

			// Assert
			assertEquals(expectedMessage, e.getMessage());

			verify(eventDelegate).fireSecurityEvent(any(), any());
		}
	}

	@Test
	void should_loescheVeranstalterDaten_change_LehrerWhenExists() {

		// Arrange
		HandshakeAck ack = service.createHandshakeAck(handshake);

		Veranstalter veranstalter = new Lehrer(new Person(UUID_LEHRER_1, "Hans Weißwurst"), false,
			Collections.singletonList(new Identifier("A12345678")));

		when(veranstalterRepository.ofId(new Identifier(UUID_LEHRER_1))).thenReturn(Optional.of(veranstalter));

		LoescheVeranstalterCommand cmd = new LoescheVeranstalterCommand().withUuid(UUID_LEHRER_1)
			.withSyncToken(ack.syncToken());

		// Act
		service.loescheVeranstalter(cmd);

		// Assert
		verify(schulkollegienService).entferneSpurenDesLehrers((Lehrer) veranstalter);
	}

}
