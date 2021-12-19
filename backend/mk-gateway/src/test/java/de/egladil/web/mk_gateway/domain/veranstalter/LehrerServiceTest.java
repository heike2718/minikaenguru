// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
import de.egladil.web.mk_gateway.domain.veranstalter.events.LehrerChanged;

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
		assertEquals(null, event.alteSchulkuerzel());
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
		assertFalse(schulkollegium.alleLehrerUnmodifiable().contains(Kollege.fromPerson(lehrer.person())));

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
		Kollege kollege = Kollege.fromPerson(lehrer.person());
		Schulkollegium schulkollegium = optKoll.get();
		assertTrue(schulkollegium.alleLehrerUnmodifiable().contains(kollege));

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
		assertFalse(lehrerAPIModel.hatZugangZuUnterlagen());
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

	@Test
	void should_changeLehrerAndCreateEvent_when_LehrerHinterherOhneSchulen() throws Exception {

		// Arrange
		final String uuid = UUID_LEHRER_2;
		final String fullName = "Olle Keule";

		final Identifier identifier = new Identifier(uuid);

		Optional<Veranstalter> optVeranstalter = getVeranstalterRepository().ofId(identifier);
		assertTrue(optVeranstalter.isPresent());

		CreateOrUpdateLehrerCommand command = CreateOrUpdateLehrerCommand.createForTest(uuid, fullName,
			null);

		Schulkollegium aktuellesSchulkollegium = getSchulkollegienRepository()
			.ofSchulkuerzel(new Identifier(SCHULKUERZEL_1)).get();

		Optional<Kollege> optKeule = aktuellesSchulkollegium.alleLehrerUnmodifiable().stream()
			.filter(p -> fullName.equals(p.fullName())).findFirst();

		assertTrue(optKeule.isPresent());

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
		assertNull(lehrer.persistierbareTeilnahmenummern());

		LehrerChanged event = service.lehrerChangedEventPayload();
		assertNotNull(event);

		assertEquals(lehrer.person(), event.person());
		assertEquals(SCHULKUERZEL_1, event.alteSchulkuerzel());
		assertEquals(null, event.neueSchulkuerzel());

		assertEquals("LehrerChanged", event.typeName());
		System.out.println("command: " + new ObjectMapper().writeValueAsString(command));
	}

	@Test
	void should_changeLehrerReturnFalse_when_UuidUnbekannt() {

		// Arrange
		final String uuid = "UIUIUIUIUI";
		final String fullName = "Kannichnich";
		final String schulkuerzel = SCHULKUERZEL_1;

		final Identifier identifier = new Identifier(uuid);

		Optional<Veranstalter> optVeranstalter = getVeranstalterRepository().ofId(identifier);
		assertFalse(optVeranstalter.isPresent());

		CreateOrUpdateLehrerCommand command = CreateOrUpdateLehrerCommand.createForTest(uuid, fullName,
			schulkuerzel);

		// Act
		boolean changed = service.changeLehrer(command);

		// Assert

		SecurityIncidentRegistered secIncident = service.securityIncidentEventPayload();

		assertNotNull(secIncident);
		assertEquals(
			"Versuch, einen nicht existierenden Lehrer zu ändern: CreateOrUpdateLehrerCommand [uuid=UIUIUIUIUI, fullName=Kannichnich, schulkuerzel=SCHULKUERZEL_1, newsletterEmpfaenger=false]",
			secIncident.message());

		assertFalse(changed);

		int anzahlNachher = getVeranstalterRepository().getCountLehrerAdded();
		assertEquals(0, anzahlNachher);
		int anzahlChanged = getVeranstalterRepository().getCountLehrerChanged();
		assertEquals(0, anzahlChanged);
	}

	@Test
	void should_changeLehrerReturnFalse_when_UuidPrivatperson() {

		// Arrange
		final String uuid = UUID_PRIVAT;
		final String fullName = "Kannichnich";
		final String schulkuerzel = SCHULKUERZEL_1;

		final Identifier identifier = new Identifier(uuid);

		Optional<Veranstalter> optVeranstalter = getVeranstalterRepository().ofId(identifier);
		assertTrue(optVeranstalter.isPresent());

		CreateOrUpdateLehrerCommand command = CreateOrUpdateLehrerCommand.createForTest(uuid, fullName,
			schulkuerzel);

		// Act
		boolean changed = service.changeLehrer(command);

		// Assert

		SecurityIncidentRegistered secIncident = service.securityIncidentEventPayload();

		assertNotNull(secIncident);
		assertEquals(
			"Versuch, einen Veranstalter zu ändern, der kein Lehrer ist: CreateOrUpdateLehrerCommand [uuid=UUID_PRIVAT, fullName=Kannichnich, schulkuerzel=SCHULKUERZEL_1, newsletterEmpfaenger=false] - UUID_PRIVAT - Herta Grütze (PRIVAT)",
			secIncident.message());

		assertFalse(changed);

		int anzahlNachher = getVeranstalterRepository().getCountLehrerAdded();
		assertEquals(0, anzahlNachher);
		int anzahlChanged = getVeranstalterRepository().getCountLehrerChanged();
		assertEquals(0, anzahlChanged);

	}

	@Test
	void should_removeSchule_work() throws Exception {

		// Arrange
		Identifier lehrerID = new Identifier(UUID_LEHRER_3);
		Identifier schuleID = new Identifier(SCHULKUERZEL_1);

		// Act
		ResponsePayload responsePayload = service.removeSchule(lehrerID, schuleID);

		// Assert
		assertTrue(responsePayload.isOk());

		LehrerChanged lehrerChangedEventPayload = service.lehrerChangedEventPayload();
		assertNotNull(lehrerChangedEventPayload);

		String changedSerialized = new ObjectMapper().writeValueAsString(lehrerChangedEventPayload);

		assertEquals(
			"{\"person\":{\"uuid\":\"UUID_LEHRER_3\",\"fullName\":\"Mimi Mimimi\",\"email\":\"minimi@web.de\"},\"alteSchulkuerzel\":\"SCHULKUERZEL_1\",\"neueSchulkuerzel\":null,\"newsletterAbonnieren\":true}",
			changedSerialized);

		int anzahlNachher = getVeranstalterRepository().getCountLehrerAdded();
		assertEquals(0, anzahlNachher);
		int anzahlChanged = getVeranstalterRepository().getCountLehrerChanged();
		assertEquals(1, anzahlChanged);

		Optional<Veranstalter> optVer = getVeranstalterRepository().ofId(lehrerID);
		Lehrer lehrer = (Lehrer) optVer.get();

		assertNull(lehrer.joinedSchulen());
		assertNull(lehrer.persistierbareTeilnahmenummern());
		assertTrue(lehrer.teilnahmeIdentifier().isEmpty());
	}

	@Test
	void should_removeSchuleThrowNotFoundException_when_UuidUnbekannt() {

		// Arrange
		final String uuid = "MIMIMI";
		final String schulkuerzel = SCHULKUERZEL_1;

		final Identifier identifier = new Identifier(uuid);

		Optional<Veranstalter> optVeranstalter = getVeranstalterRepository().ofId(identifier);
		assertFalse(optVeranstalter.isPresent());

		// Act
		try {

			service.removeSchule(new Identifier(uuid), new Identifier(schulkuerzel));
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

			// Assert

			SecurityIncidentRegistered secIncident = service.securityIncidentEventPayload();

			assertNotNull(secIncident);
			assertEquals(
				"Unbekannter Lehrer mit UUID=MIMIMI versucht, sich von der Schule mit KUERZEL=SCHULKUERZEL_1 abzumelden.",
				secIncident.message());

			int anzahlNachher = getVeranstalterRepository().getCountLehrerAdded();
			assertEquals(0, anzahlNachher);
			int anzahlChanged = getVeranstalterRepository().getCountLehrerChanged();
			assertEquals(0, anzahlChanged);
		}

	}

	@Test
	void should_removeSchuleThrowNotFoundException_when_UuidPrivatperson() {

		// Arrange
		final String uuid = UUID_PRIVAT;
		final String schulkuerzel = SCHULKUERZEL_1;

		final Identifier identifier = new Identifier(uuid);

		Optional<Veranstalter> optVeranstalter = getVeranstalterRepository().ofId(identifier);
		assertTrue(optVeranstalter.isPresent());

		// Act
		try {

			service.removeSchule(new Identifier(uuid), new Identifier(schulkuerzel));
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

			// Assert

			SecurityIncidentRegistered secIncident = service.securityIncidentEventPayload();

			assertNotNull(secIncident);
			assertEquals(
				"Privatveranstalter mit UUID=UUID_PRIVAT versucht, sich von der Schule mit KUERZEL=SCHULKUERZEL_1 abzumelden.",
				secIncident.message());

			int anzahlNachher = getVeranstalterRepository().getCountLehrerAdded();
			assertEquals(0, anzahlNachher);
			int anzahlChanged = getVeranstalterRepository().getCountLehrerChanged();
			assertEquals(0, anzahlChanged);
		}

	}

	@Test
	void should_removeSchuleChangeNothing_when_schuleNichtZugeordnet() throws Exception {

		// Arrange
		Identifier lehrerID = new Identifier(UUID_LEHRER_3);
		Identifier schuleID = new Identifier(SCHULKUERZEL_2);

		// Act
		ResponsePayload responsePayload = service.removeSchule(lehrerID, schuleID);

		// Assert
		assertFalse(responsePayload.isOk());

		assertEquals("WARN", responsePayload.getMessage().getLevel());
		assertEquals("An dieser Schule waren Sie nicht als Lehrerin/Lehrer registriert.",
			responsePayload.getMessage().getMessage());

		LehrerChanged lehrerChangedEventPayload = service.lehrerChangedEventPayload();
		assertNull(lehrerChangedEventPayload);

		SecurityIncidentRegistered secIncident = service.securityIncidentEventPayload();
		assertNotNull(secIncident);

		assertEquals("{\"message\":\"removeSchule(): Schule SCHULKUERZEL_2 war dem Lehrer UUID_LEHRER_3 nicht zugeordnet.\"}",
			new ObjectMapper().writeValueAsString(secIncident));

		int anzahlNachher = getVeranstalterRepository().getCountLehrerAdded();
		assertEquals(0, anzahlNachher);
		int anzahlChanged = getVeranstalterRepository().getCountLehrerChanged();
		assertEquals(0, anzahlChanged);

		Optional<Veranstalter> optVer = getVeranstalterRepository().ofId(lehrerID);
		Lehrer lehrer = (Lehrer) optVer.get();

		assertEquals("SCHULKUERZEL_1", lehrer.joinedSchulen());
		assertEquals("SCHULKUERZEL_1", lehrer.persistierbareTeilnahmenummern());
		assertEquals(1, lehrer.teilnahmeIdentifier().size());
	}

}
