// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.unterlagen;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.UnterlagenNichtVerfuegbarException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.veranstalter.Lehrer;
import de.egladil.web.mk_gateway.domain.veranstalter.Person;
import de.egladil.web.mk_gateway.domain.veranstalter.Privatveranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagenService;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

/**
 * UnterlagenServiceTest
 */
@QuarkusTest
public class UnterlagenServiceTest {

	private static final String UUID = "UUID";

	private static final Integer WETTBEWERBSJAHR_AKTUELL = 2020;

	@InjectMock
	DomainEventHandler domainEventHandler;

	@InjectMock
	WettbewerbService wettbewerbService;

	@InjectMock
	VeranstalterRepository veranstalterRepository;

	@InjectMock
	ZugangUnterlagenService zugangUnterlagenService;

	@InjectMock
	DownloadsRepository downloadsRepository;

	@InjectMock
	LoggableEventDelegate eventDelegate;

	@Inject
	UnterlagenService service;

	void setUp() {

	}

	@Nested
	class LehrerTests {

		@Test
		void should_getUnterlagenFuerSchuleThrowNotFoundExceptionAndAddSecurityEvent_when_LehrerNichtVorhanden() {

			// Arrange
			Identifier userId = new Identifier(UUID);

			when(veranstalterRepository.ofId(userId)).thenReturn(Optional.empty());

			// Act
			try {

				service.getUnterlagenFuerSchule(userId, Sprache.de);
				fail("keine NotFoundException");
			} catch (NotFoundException e) {

				verify(eventDelegate).fireSecurityEvent(any(), any());
			}
		}

		@Test
		void should_getUnterlagenFuerSchuleThrowNotFoundExceptionAndAddSecurityEvent_when_uuidPrivat() {

			// Arrange
			Identifier userId = new Identifier(UUID);

			Veranstalter veranstalter = new Privatveranstalter(new Person(UUID, "Privatmensch"), false,
				Collections.singletonList(new Identifier(UUID)));

			when(veranstalterRepository.ofId(userId)).thenReturn(Optional.of(veranstalter));

			// Act
			try {

				service.getUnterlagenFuerSchule(userId, Sprache.de);
				fail("keine NotFoundException");
			} catch (NotFoundException e) {

				verify(eventDelegate).fireSecurityEvent(any(), any());
			}
		}

		@Test
		void should_getUnterlagenFuerSchuleThrowUnterlagenNichtVerfuegbarException_when_zugangUnterlagenEntzogen() {

			// Arrange
			Identifier userId = new Identifier(UUID);
			Wettbewerb aktuellerWettbewerb = new Wettbewerb(new WettbewerbID(WETTBEWERBSJAHR_AKTUELL))
				.withStatus(WettbewerbStatus.ANMELDUNG)
				.withWettbewerbsbeginn(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.JANUARY, 1))
				.withDatumFreischaltungLehrer(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.MARCH, 1))
				.withDatumFreischaltungPrivat(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.JUNE, 1))
				.withWettbewerbsende(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.AUGUST, 1));

			Veranstalter veranstalter = new Lehrer(new Person(UUID, "Lehrer"), false,
				Collections.singletonList(new Identifier(UUID)));

			when(veranstalterRepository.ofId(userId)).thenReturn(Optional.of(veranstalter));
			when(zugangUnterlagenService.hatZugang(UUID)).thenReturn(Boolean.FALSE);
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));

			// Act
			try {

				service.getUnterlagenFuerSchule(userId, Sprache.de);
				fail("keine UnterlagenNichtVerfuegbarException");
			} catch (UnterlagenNichtVerfuegbarException e) {

				verify(eventDelegate).fireSecurityEvent(any(), any());
			}
		}

	}

	@Nested
	class PrivatveranstalterTests {

		@Test
		void should_getUnterlagenFuerPrivatanmeldungThrowNotFoundExceptionAndAddSecurityEvent_when_VeranstalterNichtVorhanden() {

			// Arrange
			Identifier userId = new Identifier(UUID);

			when(veranstalterRepository.ofId(userId)).thenReturn(Optional.empty());

			// Act
			try {

				service.getUnterlagenFuerPrivatanmeldung(userId, Sprache.de);
				fail("keine NotFoundException");
			} catch (NotFoundException e) {

				verify(eventDelegate).fireSecurityEvent(any(), any());
			}
		}

		@Test
		void should_getUnterlagenFuerPrivatanmeldungThrowNotFoundExceptionAndAddSecurityEvent_when_uuidLehrer() {

			// Arrange
			Identifier userId = new Identifier(UUID);
			Veranstalter veranstalter = new Lehrer(new Person(UUID, "Lehrer"), false,
				Collections.singletonList(new Identifier(UUID)));

			when(veranstalterRepository.ofId(userId)).thenReturn(Optional.of(veranstalter));

			// Act
			try {

				service.getUnterlagenFuerPrivatanmeldung(userId, Sprache.de);
				fail("keine NotFoundException");
			} catch (NotFoundException e) {

				verify(eventDelegate).fireSecurityEvent(any(), any());
			}
		}

		@Test
		void should_getUnterlagenFuerSchuleThrowUnterlagenNichtVerfuegbarException_when_zugangUnterlagenEntzogen() {

			// Arrange
			Identifier userId = new Identifier(UUID);
			Wettbewerb aktuellerWettbewerb = new Wettbewerb(new WettbewerbID(WETTBEWERBSJAHR_AKTUELL))
				.withStatus(WettbewerbStatus.ANMELDUNG)
				.withWettbewerbsbeginn(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.JANUARY, 1))
				.withDatumFreischaltungLehrer(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.MARCH, 1))
				.withDatumFreischaltungPrivat(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.JUNE, 1))
				.withWettbewerbsende(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.AUGUST, 1));

			Veranstalter veranstalter = new Privatveranstalter(new Person(UUID, "Privatmensch"), false,
				Collections.singletonList(new Identifier(UUID)));

			when(veranstalterRepository.ofId(userId)).thenReturn(Optional.of(veranstalter));
			when(zugangUnterlagenService.hatZugang(UUID)).thenReturn(Boolean.FALSE);
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));

			// Act
			try {

				service.getUnterlagenFuerPrivatanmeldung(userId, Sprache.de);
				fail("keine UnterlagenNichtVerfuegbarException");
			} catch (UnterlagenNichtVerfuegbarException e) {

				verify(eventDelegate).fireSecurityEvent(any(), any());
			}
		}

	}

}
