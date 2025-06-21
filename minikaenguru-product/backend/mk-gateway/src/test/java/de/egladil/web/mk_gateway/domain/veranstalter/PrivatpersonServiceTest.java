// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.auth.events.PrivatveranstalterCreated;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.api.PrivatveranstalterAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;

/**
 * PrivatpersonServiceTest
 */
@QuarkusTest
public class PrivatpersonServiceTest {

	private static final String UUID_PRIVAT = "UUID_PRIVAT";

	@InjectMock
	DomainEventHandler domainEventHandler;

	@InjectMock
	LoggableEventDelegate eventDelegate;

	@InjectMock
	VeranstalterRepository veranstalterRepository;

	@InjectMock
	ZugangUnterlagenService zugangUnterlagenService;

	@InjectMock
	WettbewerbService wettbewerbService;

	@InjectMock
	TeilnahmenRepository teilnahmenRepository;

	@InjectMock
	PrivatteilnahmeKuerzelService teilnahmenKuerzelService;

	@Inject
	private PrivatveranstalterService service;

	@Test
	void should_NotAddPrivatperson_when_PrivatpersonExists() {

		// Arrange
		CreateOrUpdatePrivatveranstalterCommand command = CreateOrUpdatePrivatveranstalterCommand.create(UUID_PRIVAT,
			"Herta Grütze");

		Veranstalter veranstalter = new Privatveranstalter(new Person(UUID_PRIVAT, "Herta GRütze"), false,
			Collections.singletonList(new Identifier(UUID_PRIVAT)));

		when(veranstalterRepository.ofId(new Identifier(UUID_PRIVAT))).thenReturn(Optional.of(veranstalter));

		// Act
		service.addPrivatperson(command);

		// Assert
		verify(domainEventHandler, never()).handleEvent(any(PrivatveranstalterCreated.class));
		verify(veranstalterRepository, never()).addVeranstalter(any());

	}

	@Test
	void should_AddPrivatperson_when_PrivatpersonUnknown() {

		// Arrange
		final String uuid = "dd97e1bf-f52a-4429-9443-0de9d96dac37";
		final String fullName = "Herta Kirsch-Grüzte";

		CreateOrUpdatePrivatveranstalterCommand command = CreateOrUpdatePrivatveranstalterCommand.create(uuid,
			fullName);

		Identifier identifier = new Identifier(uuid);
		when(veranstalterRepository.ofId(identifier)).thenReturn(Optional.empty());
		when(teilnahmenKuerzelService.neuesKuerzel()).thenReturn("TZZFFFIF");

		// Act
		service.addPrivatperson(command);

		// Assert
		verify(veranstalterRepository).addVeranstalter(any());
		verify(domainEventHandler, never()).handleEvent(any(PrivatveranstalterCreated.class));
		verify(teilnahmenKuerzelService).neuesKuerzel();
	}

	@Test
	void shouldFindPrivatpersonThrowException_when_UuidNull() {

		try {

			service.findPrivatperson(null);
			fail("keine BadRequestException");
		} catch (BadRequestException e) {

			assertEquals("uuid darf nicht blank sein.", e.getMessage());
		}
	}

	@Test
	void shouldFindPrivatpersonThrowException_when_UuidBlank() {

		try {

			service.findPrivatperson(" ");
			fail("keine BadRequestException");
		} catch (BadRequestException e) {

			assertEquals("uuid darf nicht blank sein.", e.getMessage());
		}
	}

	@Test
	void shouldFindPrivatpersonThrowException_when_VeranstalterUnknown() {

		// Arrange
		String uuid = "3523528285";

		try {

			service.findPrivatperson(uuid);
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			assertEquals("Kennen keinen Veranstalter mit dieser ID", e.getMessage());
		}
	}

	@Test
	void shouldFindPrivatpersonThrowException_when_Lehrer() {

		// Arrange
		Veranstalter veranstalter = new Lehrer(new Person(UUID_PRIVAT, "Herta GRütze"), false,
			Collections.singletonList(new Identifier(UUID_PRIVAT)));

		when(veranstalterRepository.ofId(new Identifier(UUID_PRIVAT))).thenReturn(Optional.of(veranstalter));

		try {

			service.findPrivatperson(UUID_PRIVAT);

			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			assertEquals("Kennen keinen Privatveranstalter mit dieser ID", e.getMessage());
			verify(eventDelegate).fireSecurityEvent(any(), any());
		}
	}

	@Test
	void shouldFindPrivatperson_when_Present() {

		// Arrange
		List<Identifier> teilnahmenummern = new ArrayList<>();
		teilnahmenummern.add(new Identifier(UUID_PRIVAT));

		Veranstalter persistenterVeranstalter = new Privatveranstalter(new Person(UUID_PRIVAT, "Herta Grütze"), false,
			teilnahmenummern);

		when(veranstalterRepository.ofId(new Identifier(UUID_PRIVAT))).thenReturn(Optional.of(persistenterVeranstalter));

		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(new Wettbewerb(new WettbewerbID(2020))));

		// Act
		PrivatveranstalterAPIModel veranstalter = service.findPrivatperson(UUID_PRIVAT);

		// Assert
		assertNotNull(veranstalter);
	}

	@Test
	void shouldFindPrivatpersonThrowException_when_VeranstalterMitMehrerenTeilnahmekuerzeln() {

		List<Identifier> teilnahmekuerzel = new ArrayList<>();
		teilnahmekuerzel.add(new Identifier(UUID_PRIVAT));
		teilnahmekuerzel.add(new Identifier("KIGTRFGZT"));

		Veranstalter veranstalter = new Privatveranstalter(new Person(UUID_PRIVAT, "Herta GRütze"), false, teilnahmekuerzel);

		when(veranstalterRepository.ofId(new Identifier(UUID_PRIVAT))).thenReturn(Optional.of(veranstalter));

		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(new Wettbewerb(new WettbewerbID(2020))));

		try {

			service.findPrivatperson(UUID_PRIVAT);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals("Kann aktuelle Teilnahme nicht ermitteln", e.getMessage());
			verify(eventDelegate).fireDataInconsistencyEvent(any(), any());
		}
	}

	@Test
	void shouldFindPrivatpersonThrowException_when_VeranstalterOhneTeilnahmekuerzel() {

		Veranstalter veranstalter = new Privatveranstalter(new Person(UUID_PRIVAT, "Herta GRütze"), false, new ArrayList<>());

		when(veranstalterRepository.ofId(new Identifier(UUID_PRIVAT))).thenReturn(Optional.of(veranstalter));

		Wettbewerb aktueller = new Wettbewerb(new WettbewerbID(2020)).withStatus(WettbewerbStatus.ANMELDUNG);
		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktueller));

		try {

			service.findPrivatperson(UUID_PRIVAT);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals("Kann aktuelle Teilnahme nicht ermitteln", e.getMessage());
			verify(eventDelegate).fireDataInconsistencyEvent(any(), any());
		}
	}

}
