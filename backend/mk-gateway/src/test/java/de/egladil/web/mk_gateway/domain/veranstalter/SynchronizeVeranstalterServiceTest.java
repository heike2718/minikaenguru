// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MessagingAuthException;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.veranstalter.api.ChangeUserCommand;
import de.egladil.web.mk_gateway.infrastructure.messaging.HandshakeAck;
import de.egladil.web.mk_gateway.infrastructure.messaging.LoescheVeranstalterCommand;
import de.egladil.web.mk_gateway.infrastructure.messaging.SyncHandshake;

/**
 * SynchronizeVeranstalterServiceTest
 */
public class SynchronizeVeranstalterServiceTest extends AbstractDomainServiceTest {

	/**
	 *
	 */
	private static final String CLIENT_ID = "GUIguiggudwgugwo";

	private static final String NONCE = "guagfuowge";

	private SynchronizeVeranstalterService service;

	private SyncHandshake handshake = SyncHandshake.create(CLIENT_ID, NONCE);

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();

		service = SynchronizeVeranstalterService.createForTest(getVeranstalterRepository(), getUserRepository(),
			SchulkollegienService.createForTest(getSchulkollegienRepository()));
	}

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

			SecurityIncidentRegistered secIncedent = service.securityIncidentPayload();
			assertEquals(expectedMessage, secIncedent.message());
		}
	}

	@Test
	void should_changeVeranstalterDaten_change_LehrerWhenExists() {

		// Arrange
		HandshakeAck ack = service.createHandshakeAck(handshake);

		ChangeUserCommand cmd = new ChangeUserCommand().withEmail("hans-weisswurst@web.de").withNachname("Weißwurst")
			.withUuid(UUID_LEHRER_1)
			.withVorname("Hans").withSyncToken(ack.syncToken());

		// Act
		service.changeVeranstalterDaten(cmd);

		assertEquals(0, getVeranstalterRepository().getCountLehrerAdded());
		assertEquals(1, getVeranstalterRepository().getCountLehrerChanged());

		assertEquals(0, getVeranstalterRepository().getCountPrivatpersonAdded());
		assertEquals(0, getVeranstalterRepository().getCountPrivatpersonChanged());

		assertEquals(0, getSchulkollegienRepository().getCountKollegiumAdded());
		assertEquals(2, getSchulkollegienRepository().getCountKollegiumChanged()); // da der Lehrer 2 Schulen hat
		assertEquals(0, getSchulkollegienRepository().getCountKollegiumDeleted());

		Person person = getVeranstalterRepository().ofId(new Identifier(UUID_LEHRER_1)).get().person();
		assertEquals("hans-weisswurst@web.de", person.email());
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

			SecurityIncidentRegistered secIncedent = service.securityIncidentPayload();
			assertEquals(expectedMessage, secIncedent.message());
		}
	}

	@Test
	void should_loescheVeranstalterDaten_change_LehrerWhenExists() {

		// Arrange
		HandshakeAck ack = service.createHandshakeAck(handshake);

		LoescheVeranstalterCommand cmd = new LoescheVeranstalterCommand().withUuid(UUID_LEHRER_1)
			.withSyncToken(ack.syncToken());

		// Act
		service.loescheVeranstalter(cmd);

		assertEquals(0, getVeranstalterRepository().getCountLehrerAdded());
		assertEquals(0, getVeranstalterRepository().getCountLehrerChanged());
		assertEquals(1, getVeranstalterRepository().getCountVeranstalterRemoved());

		assertEquals(0, getVeranstalterRepository().getCountPrivatpersonAdded());
		assertEquals(0, getVeranstalterRepository().getCountPrivatpersonChanged());

		assertEquals(0, getSchulkollegienRepository().getCountKollegiumAdded());
		assertEquals(1, getSchulkollegienRepository().getCountKollegiumChanged());
		assertEquals(1, getSchulkollegienRepository().getCountKollegiumDeleted());

		assertTrue(getVeranstalterRepository().ofId(new Identifier(UUID_LEHRER_1)).isEmpty());
	}

}
