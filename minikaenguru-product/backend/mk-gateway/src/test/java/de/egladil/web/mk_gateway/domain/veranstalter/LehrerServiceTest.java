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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.auth.events.LehrerCreated;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.veranstalter.api.LehrerAPIModel;
import de.egladil.web.mk_gateway.domain.veranstalter.events.LehrerChanged;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;

/**
 * LehrerServiceTest
 */
@QuarkusTest
public class LehrerServiceTest {

	private static final String UUID_LEHRER_1 = "UUID_LEHRER_1";

	private static final String UUID_LEHRER_3 = "UUID_LEHRER_3";

	private static final String SCHULKUERZEL_1 = "SCHULKUERZEL_1";

	private static final String SCHULKUERZEL_2 = "SCHULKUERZEL_2";

	private static final String SCHULKUERZEL_3 = "SCHULKUERZEL_3";

	private static final String UUID_PRIVAT = "UUID_PRIVAT";

	@InjectMock
	WettbewerbService wettbewerbService;

	@InjectMock
	VeranstalterRepository veranstalterRepository;

	@InjectMock
	SchulkollegienService schulkollegienService;

	@InjectMock
	ZugangUnterlagenService zugangUnterlagenService;

	@InjectMock
	DomainEventHandler domainEventHandler;

	@InjectMock
	LoggableEventDelegate eventDelegate;

	@Inject
	LehrerService service;

	@Test
	void should_doNothing_when_LehrerExists() throws Exception {

		// Arrange
		CreateOrUpdateLehrerCommand command = CreateOrUpdateLehrerCommand.createForTest(UUID_LEHRER_1, "Irgendein Name",
			SCHULKUERZEL_1);

		Veranstalter veranstalter = new Lehrer(new Person(UUID_LEHRER_1, "Irgendein Name"), false,
			Collections.singletonList(new Identifier(SCHULKUERZEL_1)));

		when(veranstalterRepository.ofId(new Identifier(UUID_LEHRER_1))).thenReturn(Optional.of(veranstalter));

		// Act
		service.addLehrer(command);

		// Assert
		verify(domainEventHandler, never()).handleEvent(any());
	}

	@Test
	void should_AddLehrerAndCreateEvent_when_NeuerLehrer() throws Exception {

		// Arrange
		final String uuid = "hklhasshdiha";
		final String fullName = "Professor Proton";
		final String schulkuerzel = "GHKFFDFF";

		final Identifier identifier = new Identifier(uuid);

		Veranstalter veranstalter = new Lehrer(new Person(uuid, "Irgendein Name"), false,
			Collections.singletonList(new Identifier(schulkuerzel)));

		when(veranstalterRepository.ofId(identifier)).thenReturn(Optional.empty());

		CreateOrUpdateLehrerCommand command = CreateOrUpdateLehrerCommand.createForTest(uuid, fullName,
			schulkuerzel);

		// Act
		service.addLehrer(command);

		// Assert
		verify(veranstalterRepository).addVeranstalter(veranstalter);
		verify(domainEventHandler).handleEvent(any(LehrerChanged.class));
	}

	@Test
	void should_addSchuleAddTheSchuleAndCreateEvent_when_SchuleNeuFuerLehrer() {

		// Arrange
		Identifier schuleID = new Identifier(SCHULKUERZEL_3);
		Identifier lehrerID = new Identifier(UUID_LEHRER_1);

		List<Identifier> schulen = new ArrayList<>();
		schulen.add(new Identifier(SCHULKUERZEL_1));

		Veranstalter veranstalter = new Lehrer(new Person(lehrerID.identifier(), "Irgendein Name"), false,
			schulen);

		when(veranstalterRepository.ofId(lehrerID)).thenReturn(Optional.of(veranstalter));

		// Act
		ResponsePayload responsePayload = service.addSchule(lehrerID, schuleID);

		// Assert
		assertTrue(responsePayload.isOk());
		assertEquals("Sie haben sich erfolgreich an der neuen Schule als Lehrerin/Lehrer registriert.",
			responsePayload.getMessage().getMessage());

		verify(domainEventHandler).handleEvent(any(LehrerChanged.class));
		verify(schulkollegienService).handleLehrerChanged(any(LehrerChanged.class));

	}

	@Test
	void should_addSchuleThrowNotFoundExceptionAndAddSecurityEvent_when_LehrerNichtVorhanden() {

		// Arrange
		String uuid = "UUID_LEHRER_4";
		Identifier schuleID = new Identifier(SCHULKUERZEL_3);
		Identifier lehrerID = new Identifier(uuid);

		when(veranstalterRepository.ofId(lehrerID)).thenReturn(Optional.empty());

		// Act
		try {

			service.addSchule(lehrerID, schuleID);
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			verify(eventDelegate).fireSecurityEvent(any(), any());

		}

	}

	@Test
	void should_addSchuleThrowNotFoundExceptionAndAddSecurityEvent_when_veranstalterKeinLehrer() {

		// Arrange
		Identifier schuleID = new Identifier(SCHULKUERZEL_3);
		Identifier lehrerID = new Identifier(UUID_PRIVAT);

		Veranstalter veranstalter = new Privatveranstalter(new Person(lehrerID.identifier(), "Irgendein Name"), false,
			Collections.singletonList(new Identifier(SCHULKUERZEL_1)));

		when(veranstalterRepository.ofId(lehrerID)).thenReturn(Optional.of(veranstalter));

		// Act
		try {

			service.addSchule(lehrerID, schuleID);
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			verify(eventDelegate).fireSecurityEvent(any(), any());
		}

	}

	@Test
	void should_addSchuleNotAddTheSchuleAndCreateEvent_when_SchuleNichtNeuFuerLehrer() {

		// Arrange
		Identifier schuleID = new Identifier(SCHULKUERZEL_1);
		Identifier lehrerID = new Identifier(UUID_LEHRER_1);

		Veranstalter veranstalter = new Lehrer(new Person(UUID_LEHRER_1, "Irgendein Name"), false,
			Collections.singletonList(new Identifier(SCHULKUERZEL_1)));

		when(veranstalterRepository.ofId(new Identifier(UUID_LEHRER_1))).thenReturn(Optional.of(veranstalter));

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
		when(veranstalterRepository.ofId(new Identifier(uuid))).thenReturn(Optional.empty());

		// Act
		try {

			service.findLehrer(uuid);
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			verify(eventDelegate).fireSecurityEvent(any(), any());
		}

	}

	@Test
	void should_findLehrerThrowNotFoundExceptionAndAddSecurityEvent_when_uuidEinerPrivatperson() {

		Veranstalter veranstalter = new Privatveranstalter(new Person(UUID_PRIVAT, "Irgendein Name"), false,
			Collections.singletonList(new Identifier(SCHULKUERZEL_1)));

		when(veranstalterRepository.ofId(new Identifier(UUID_PRIVAT))).thenReturn(Optional.of(veranstalter));

		// Act
		try {

			service.findLehrer(UUID_PRIVAT);
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			verify(eventDelegate).fireSecurityEvent(any(), any());
		}

	}

	@Test
	void should_findLehrerReturnTheData_when_lehrerVorhanden() {

		// Arrange
		Veranstalter veranstalter = new Lehrer(new Person(UUID_LEHRER_1, "Irgendein Name"), true,
			Collections.singletonList(new Identifier(SCHULKUERZEL_1)));

		when(veranstalterRepository.ofId(new Identifier(UUID_LEHRER_1))).thenReturn(Optional.of(veranstalter));

		// Act
		LehrerAPIModel lehrerAPIModel = service.findLehrer(UUID_LEHRER_1);

		// Assert
		assertTrue(lehrerAPIModel.newsletterAbonniert());
		assertEquals(1, lehrerAPIModel.getTeilnahmenummern().size());
	}

	@Test
	void should_changeLehrerAndCreateEvent_when_LehrerGeaendert() throws Exception {

		// Arrange
		final String uuid = UUID_LEHRER_3;
		final String fullName = "Mimi Mimimimimi";
		final String schulkuerzel = SCHULKUERZEL_1;

		final Identifier identifier = new Identifier(uuid);

		Veranstalter veranstalter = new Lehrer(new Person(UUID_LEHRER_3, "Irgendein Name"), false,
			Collections.singletonList(new Identifier(SCHULKUERZEL_1)));

		when(veranstalterRepository.ofId(identifier)).thenReturn(Optional.of(veranstalter));

		CreateOrUpdateLehrerCommand command = CreateOrUpdateLehrerCommand.createForTest(uuid, fullName,
			schulkuerzel);

		// Act
		boolean changed = service.changeLehrer(command);

		// Assert
		assertTrue(changed);

		verify(domainEventHandler).handleEvent(any(LehrerChanged.class));
		verify(domainEventHandler, never()).handleEvent(any(LehrerCreated.class));
	}

	@Test
	void should_changeLehrerReturnFalse_when_UuidUnbekannt() {

		// Arrange
		final String uuid = "UIUIUIUIUI";
		final String fullName = "Kannichnich";
		final String schulkuerzel = SCHULKUERZEL_1;

		final Identifier identifier = new Identifier(uuid);

		when(veranstalterRepository.ofId(identifier)).thenReturn(Optional.empty());

		CreateOrUpdateLehrerCommand command = CreateOrUpdateLehrerCommand.createForTest(uuid, fullName,
			schulkuerzel);

		// Act
		boolean changed = service.changeLehrer(command);

		// Assert
		verify(eventDelegate).fireSecurityEvent(any(), any());
		assertFalse(changed);

		verify(domainEventHandler, never()).handleEvent(any(LehrerChanged.class));
		verify(domainEventHandler, never()).handleEvent(any(LehrerCreated.class));
	}

	@Test
	void should_changeLehrerReturnFalse_when_UuidPrivatperson() {

		// Arrange
		final String uuid = UUID_PRIVAT;
		final String fullName = "Kannichnich";
		final String schulkuerzel = SCHULKUERZEL_1;

		final Identifier identifier = new Identifier(uuid);

		Veranstalter veranstalter = new Privatveranstalter(new Person(uuid, "Horst"), false,
			Collections.singletonList(new Identifier("654647")));

		when(veranstalterRepository.ofId(identifier)).thenReturn(Optional.of(veranstalter));

		CreateOrUpdateLehrerCommand command = CreateOrUpdateLehrerCommand.createForTest(uuid, fullName,
			schulkuerzel);

		// Act
		boolean changed = service.changeLehrer(command);

		// Assert
		verify(domainEventHandler, never()).handleEvent(any(LehrerChanged.class));
		verify(domainEventHandler, never()).handleEvent(any(LehrerCreated.class));
		assertFalse(changed);

	}

	@Test
	void should_removeSchule_work() throws Exception {

		// Arrange
		Identifier lehrerID = new Identifier(UUID_LEHRER_3);
		Identifier schuleID = new Identifier(SCHULKUERZEL_1);

		List<Identifier> schulen = new ArrayList<>();
		schulen.add(new Identifier(SCHULKUERZEL_1));
		schulen.add(new Identifier(SCHULKUERZEL_3));

		Veranstalter veranstalter = new Lehrer(new Person(UUID_LEHRER_3, "Irgendein Name"), false, schulen);

		when(veranstalterRepository.ofId(new Identifier(UUID_LEHRER_3))).thenReturn(Optional.of(veranstalter));

		// Act
		ResponsePayload responsePayload = service.removeSchule(lehrerID, schuleID);

		// Assert
		assertTrue(responsePayload.isOk());

		verify(domainEventHandler).handleEvent(any(LehrerChanged.class));
		verify(domainEventHandler, never()).handleEvent(any(LehrerCreated.class));
		verify(schulkollegienService).handleLehrerChanged(any(LehrerChanged.class));
	}

	@Test
	void should_removeSchuleThrowNotFoundException_when_UuidUnbekannt() {

		// Arrange
		final String uuid = "MIMIMI";
		final String schulkuerzel = SCHULKUERZEL_1;

		final Identifier identifier = new Identifier(uuid);

		when(veranstalterRepository.ofId(identifier)).thenReturn(Optional.empty());

		// Act
		try {

			service.removeSchule(new Identifier(uuid), new Identifier(schulkuerzel));
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

			// Assert
			verify(eventDelegate).fireSecurityEvent(any(), any());
			verify(domainEventHandler, never()).handleEvent(any(LehrerChanged.class));
			verify(domainEventHandler, never()).handleEvent(any(LehrerCreated.class));
		}

	}

	@Test
	void should_removeSchuleThrowNotFoundException_when_UuidPrivatperson() {

		// Arrange
		final String schulkuerzel = SCHULKUERZEL_1;

		Veranstalter veranstalter = new Privatveranstalter(new Person(UUID_PRIVAT, "Irgendein Name"), false,
			Collections.singletonList(new Identifier(SCHULKUERZEL_1)));

		when(veranstalterRepository.ofId(new Identifier(UUID_PRIVAT))).thenReturn(Optional.of(veranstalter));

		// Act
		try {

			service.removeSchule(new Identifier(UUID_PRIVAT), new Identifier(schulkuerzel));
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

			// Assert
			verify(eventDelegate).fireSecurityEvent(any(), any());
			verify(domainEventHandler, never()).handleEvent(any(LehrerChanged.class));
			verify(domainEventHandler, never()).handleEvent(any(LehrerCreated.class));
		}

	}

	@Test
	void should_removeSchuleChangeNothing_when_schuleNichtZugeordnet() throws Exception {

		// Arrange
		Identifier lehrerID = new Identifier(UUID_LEHRER_3);
		Identifier schuleID = new Identifier(SCHULKUERZEL_2);

		List<Identifier> schulen = new ArrayList<>();
		schulen.add(new Identifier(SCHULKUERZEL_1));
		schulen.add(new Identifier(SCHULKUERZEL_3));

		Veranstalter veranstalter = new Lehrer(new Person(UUID_LEHRER_3, "Irgendein Name"), false, schulen);

		when(veranstalterRepository.ofId(new Identifier(UUID_LEHRER_3))).thenReturn(Optional.of(veranstalter));

		// Act
		ResponsePayload responsePayload = service.removeSchule(lehrerID, schuleID);

		// Assert
		assertFalse(responsePayload.isOk());

		assertEquals("WARN", responsePayload.getMessage().getLevel());
		assertEquals("An dieser Schule waren Sie nicht als Lehrerin/Lehrer registriert.",
			responsePayload.getMessage().getMessage());

		verify(eventDelegate).fireSecurityEvent(any(), any());
		verify(domainEventHandler, never()).handleEvent(any(LehrerChanged.class));
		verify(domainEventHandler, never()).handleEvent(any(LehrerCreated.class));
	}

}
