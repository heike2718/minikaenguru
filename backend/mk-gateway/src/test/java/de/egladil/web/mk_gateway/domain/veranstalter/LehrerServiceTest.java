// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Optional;

import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.api.LehrerAPIModel;

/**
 * LehrerServiceTest
 */
public class LehrerServiceTest extends AbstractDomainServiceTest {

	private LehrerService service;

	@BeforeEach
	@Override
	protected void setUp() {

		super.setUp();
		SchulkollegienService schulkollegienService = SchulkollegienService.createForTest(getSchulkollegienRepository());
		ZugangUnterlagenService zugangUnterlagenService = ZugangUnterlagenService.createForTest(getTeilnahmenRepository(),
			getVeranstalterRepository(), getWettbewerbService());
		service = LehrerService.createServiceForTest(getVeranstalterRepository(), schulkollegienService, zugangUnterlagenService,
			getWettbewerbService());

	}

	@Test
	void should_doNothing_when_LehrerExists() throws Exception {

		// Arrange
		CreateOrUpdateLehrerCommand command = CreateOrUpdateLehrerCommand.createForTest(UUID_LEHRER_1, "Irgendein Name",
			SCHULKUERZEL_1);

		// Act
		service.addLehrer(command);

		// Assert
		int anzahlNachher = getVeranstalterRepository().getCountLehrerAdded();
		assertEquals(0, anzahlNachher);

		LehrerChanged event = service.lehrerChangedEventPayload();
		assertNull(event);
	}

	@Test
	void should_AddLehrerAndCreateEvent_when_NeuerLehrer() throws Exception {

		// Arrange
		final String uuid = "hklhasshdiha";
		final String fullName = "Professor Proton";
		final String schulkuerzel = "GHKFFDFF";

		final Identifier identifier = new Identifier(uuid);

		Optional<Veranstalter> optVeranstalter = getVeranstalterRepository().ofId(identifier);
		assertFalse(optVeranstalter.isPresent());

		CreateOrUpdateLehrerCommand command = CreateOrUpdateLehrerCommand.createForTest(uuid, fullName,
			schulkuerzel);

		// Act
		service.addLehrer(command);

		// Assert
		int anzahlNachher = getVeranstalterRepository().getCountLehrerAdded();
		assertEquals(1, anzahlNachher);

		optVeranstalter = getVeranstalterRepository().ofId(identifier);
		assertTrue(optVeranstalter.isPresent());

		Veranstalter veranstalter = optVeranstalter.get();

		assertEquals(Rolle.LEHRER, veranstalter.rolle());
		assertEquals(uuid, veranstalter.uuid());
		assertEquals(fullName, veranstalter.fullName());

		Lehrer lehrer = (Lehrer) veranstalter;
		assertEquals(schulkuerzel, lehrer.persistierbareTeilnahmenummern());

		LehrerChanged event = service.lehrerChangedEventPayload();
		assertNotNull(event);

		assertEquals(lehrer.person(), event.person());
		assertEquals("", event.alteSchulkuerzel());
		assertEquals(schulkuerzel, event.neueSchulkuerzel());

		assertEquals("LehrerChanged", event.typeName());
		System.out.println("command: " + new ObjectMapper().writeValueAsString(command));
	}

	@Test
	void should_addSchuleAddTheSchuleAndCreateEvent_when_SchuleNeuFuerLehrer() {

		// Arrange
		Identifier schuleID = new Identifier(SCHULKUERZEL_3);
		Identifier lehrerID = new Identifier(UUID_LEHRER_1);

		String expectedAlteSchulkuerzel = SCHULKUERZEL_1 + "," + SCHULKUERZEL_2;
		String expectedNeueSchulkuertel = expectedAlteSchulkuerzel + "," + SCHULKUERZEL_3;

		Optional<Schulkollegium> optKoll = getSchulkollegienRepository().ofSchulkuerzel(schuleID);
		Optional<Veranstalter> optLehrer = getVeranstalterRepository().ofId(lehrerID);

		assertTrue(optKoll.isPresent());
		assertTrue(optLehrer.isPresent());

		Lehrer lehrer = (Lehrer) optLehrer.get();
		Schulkollegium schulkollegium = optKoll.get();
		assertFalse(schulkollegium.alleLehrerUnmodifiable().contains(lehrer.person()));

		// Act
		ResponsePayload responsePayload = service.addSchule(lehrerID, schuleID);

		// Assert
		assertTrue(responsePayload.isOk());
		assertEquals("Sie haben sich erfolgreich an der neuen Schule als Lehrerin/Lehrer registriert.",
			responsePayload.getMessage().getMessage());

		LehrerChanged eventPayload = service.lehrerChangedEventPayload();
		assertNotNull(eventPayload);

		assertEquals(expectedAlteSchulkuerzel, eventPayload.alteSchulkuerzel());
		assertEquals(expectedNeueSchulkuertel, eventPayload.neueSchulkuerzel());
		assertEquals(lehrer.person(), eventPayload.person());

	}

	@Test
	void should_addSchuleThrowNotFoundExceptionAndAddSecurityEvent_when_LehrerNichtVorhanden() {

		// Arrange
		String uuid = "UUID_LEHRER_4";
		Identifier schuleID = new Identifier(SCHULKUERZEL_3);
		Identifier lehrerID = new Identifier(uuid);

		// Act
		try {

			service.addSchule(lehrerID, schuleID);
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			SecurityIncidentRegistered secEventPayload = service.securityIncidentEventPayload();
			assertNotNull(secEventPayload);

			assertEquals("SecurityIncidentRegistered", secEventPayload.typeName());
			assertEquals("Unbekannter Lehrer mit UUID=UUID_LEHRER_4 versucht, Schule mit KUERZEL=SCHULKUERZEL_3 hinzuzufügen.",
				secEventPayload.message());
		}

	}

	@Test
	void should_addSchuleThrowNotFoundExceptionAndAddSecurityEvent_when_veranstalterKeinLehrer() {

		// Arrange
		Identifier schuleID = new Identifier(SCHULKUERZEL_3);
		Identifier lehrerID = new Identifier(UUID_PRIVAT);

		// Act
		try {

			service.addSchule(lehrerID, schuleID);
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			SecurityIncidentRegistered secEventPayload = service.securityIncidentEventPayload();
			assertNotNull(secEventPayload);

			assertEquals("SecurityIncidentRegistered", secEventPayload.typeName());
			assertEquals("Privatveranstalter mit UUID=UUID_PRIVAT versucht, Schule mit KUERZEL=SCHULKUERZEL_3 hinzuzufügen.",
				secEventPayload.message());
		}

	}

	@Test
	void should_addSchuleNotAddTheSchuleAndCreateEvent_when_SchuleNichtNeuFuerLehrer() {

		// Arrange
		Identifier schuleID = new Identifier(SCHULKUERZEL_1);
		Identifier lehrerID = new Identifier(UUID_LEHRER_1);

		Optional<Schulkollegium> optKoll = getSchulkollegienRepository().ofSchulkuerzel(schuleID);
		Optional<Veranstalter> optLehrer = getVeranstalterRepository().ofId(lehrerID);

		assertTrue(optKoll.isPresent());
		assertTrue(optLehrer.isPresent());

		Lehrer lehrer = (Lehrer) optLehrer.get();
		Schulkollegium schulkollegium = optKoll.get();
		assertTrue(schulkollegium.alleLehrerUnmodifiable().contains(lehrer.person()));

		// Act
		ResponsePayload responsePayload = service.addSchule(lehrerID, schuleID);

		// Assert
		assertFalse(responsePayload.isOk());
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("WARN", messagePayload.getLevel());
		assertEquals("An dieser Schule sind Sie bereits als Lehrerin/Lehrer registriert.",
			messagePayload.getMessage());
	}

	@Test
	void should_findLehrerThrowIllegalArgumentException_when_uuidNull() {

		try {

			service.findLehrer(null);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("uuid darf nicht blank sein", e.getMessage());
		}
	}

	@Test
	void should_findLehrerThrowIllegalArgumentException_when_uuidBlank() {

		try {

			service.findLehrer("  ");
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("uuid darf nicht blank sein", e.getMessage());
		}
	}

	@Test
	void should_findLehrerThrowNotFoundExceptionAndAddSecurityEvent_when_LehrerNichtVorhanden() {

		// Arrange
		String uuid = "UUID_LEHRER_4";

		// Act
		try {

			service.findLehrer(uuid);
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			SecurityIncidentRegistered secEventPayload = service.securityIncidentEventPayload();
			assertNotNull(secEventPayload);

			assertEquals("SecurityIncidentRegistered", secEventPayload.typeName());
			assertEquals("Versuch, nicht vorhandenen Veranstalter mit UUID=UUID_LEHRER_4 zu finden",
				secEventPayload.message());
		}

	}

	@Test
	void should_findLehrerThrowNotFoundExceptionAndAddSecurityEvent_when_uuidEinerPrivatperson() {

		// Act
		try {

			service.findLehrer(UUID_PRIVAT);
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			SecurityIncidentRegistered secEventPayload = service.securityIncidentEventPayload();
			assertNotNull(secEventPayload);

			assertEquals("SecurityIncidentRegistered", secEventPayload.typeName());
			assertEquals("Falsche Rolle: erwarten Lehrer, war aber UUID_PRIVAT - Herta Grütze (PRIVAT)",
				secEventPayload.message());
		}

	}

	@Test
	void should_findLehrerReturnTheData_when_lehrerVorhanden() {

		// Act
		LehrerAPIModel lehrerAPIModel = service.findLehrer(UUID_LEHRER_GESPERRT);

		// Assert
		assertFalse(lehrerAPIModel.hatZugangZuUnterlangen());
		assertTrue(lehrerAPIModel.newsletterAbonniert());
	}

	@Test
	void should_changeLehrerAndCreateEvent_when_LehrerGeaendert() throws Exception {

		// Arrange
		final String uuid = UUID_LEHRER_3;
		final String fullName = "Mimi Mimimimimi";
		final String schulkuerzel = SCHULKUERZEL_1;

		final Identifier identifier = new Identifier(uuid);

		Optional<Veranstalter> optVeranstalter = getVeranstalterRepository().ofId(identifier);
		assertTrue(optVeranstalter.isPresent());

		CreateOrUpdateLehrerCommand command = CreateOrUpdateLehrerCommand.createForTest(uuid, fullName,
			schulkuerzel);

		// Act
		boolean changed = service.changeLehrer(command);

		// Assert
		assertTrue(changed);

		int anzahlNachher = getVeranstalterRepository().getCountLehrerAdded();
		assertEquals(0, anzahlNachher);
		int anzahlChanged = getVeranstalterRepository().getCountLehrerChanged();
		assertEquals(1, anzahlChanged);

		optVeranstalter = getVeranstalterRepository().ofId(identifier);
		assertTrue(optVeranstalter.isPresent());

		Veranstalter veranstalter = optVeranstalter.get();

		assertEquals(Rolle.LEHRER, veranstalter.rolle());
		assertEquals(uuid, veranstalter.uuid());
		assertEquals(fullName, veranstalter.fullName());

		Lehrer lehrer = (Lehrer) veranstalter;
		assertEquals(schulkuerzel, lehrer.persistierbareTeilnahmenummern());

		LehrerChanged event = service.lehrerChangedEventPayload();
		assertNotNull(event);

		assertEquals(lehrer.person(), event.person());
		assertEquals(SCHULKUERZEL_1, event.alteSchulkuerzel());
		assertEquals(schulkuerzel, event.neueSchulkuerzel());

		assertEquals("LehrerChanged", event.typeName());
		System.out.println("command: " + new ObjectMapper().writeValueAsString(command));
	}

}
