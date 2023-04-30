// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.TestConstants;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.PrivatteilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.SchulanmeldungRequestPayload;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.SchulteilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.events.PrivatteilnahmeCreated;
import de.egladil.web.mk_gateway.domain.teilnahmen.events.SchulteilnahmeCreated;
import de.egladil.web.mk_gateway.domain.veranstalter.Lehrer;
import de.egladil.web.mk_gateway.domain.veranstalter.Person;
import de.egladil.web.mk_gateway.domain.veranstalter.Privatveranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagen;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

/**
 * AktuelleTeilnahmeServiceTest
 */
@QuarkusTest
public class AktuelleTeilnahmeServiceTest {

	@InjectMock
	DomainEventHandler domainEventHandler;

	@InjectMock
	LoggableEventDelegate eventDelegate;

	@InjectMock
	VeranstalterRepository veranstalterRepository;

	@InjectMock
	WettbewerbService wettbewerbService;

	@InjectMock
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	AktuelleTeilnahmeService service;

	@Nested
	@DisplayName("Aktuelle Teilnahme Feature")
	class AktuelleTeilnahmeFeature {

		@Test
		void should_GetAktuelleTeilnahmeThrowException_when_TeilnahmenummerNull() {

			// Arrange
			String teilnahmenummer = null;

			try {

				service.aktuelleTeilnahme(teilnahmenummer);
				fail("keine BadRequestException");
			} catch (BadRequestException e) {

				assertEquals("teilnahmenummer darf nicht blank sein.", e.getMessage());
			}

		}

		@Test
		void should_GetAktuelleTeilnahmeThrowException_when_TeilnahmenNull() {

			// Arrange
			List<Teilnahme> teilnahmen = null;

			try {

				service.aktuelleTeilnahme(teilnahmen);
				fail("keine BadRequestException");
			} catch (BadRequestException e) {

				assertEquals("teilnahmen darf nicht null sein.", e.getMessage());
			}

		}

		@Test
		void should_GetAktuelleTeilnahmeThrowException_when_TeilnahmenummerBlank() {

			try {

				service.aktuelleTeilnahme("  ");
				fail("keine BadRequestException");
			} catch (BadRequestException e) {

				assertEquals("teilnahmenummer darf nicht blank sein.", e.getMessage());
			}

		}

		@Test
		void should_GetAktuelleTeilnahmeReturnEmpty_when_KeinLaufenderWettbewerb() {

			// Arrange
			String teilnahmenummer = "HJKGDTDTU";
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.empty());

			// Act
			Optional<Teilnahme> opt = service.aktuelleTeilnahme(teilnahmenummer);

			// Assert
			assertTrue(opt.isEmpty());

		}

		@Test
		void should_GetAktuelleTeilnahmeReturnEmpty_when_NichtAngemeldet() {

			// Arrange
			String teilnahmenummer = "HJKGDTDTU";
			when(wettbewerbService.aktuellerWettbewerb())
				.thenReturn(Optional.of(getAktuellerWettbewerb(TestConstants.WETTBEWERBSJAHR_AKTUELL, WettbewerbStatus.ANMELDUNG)));

			// Act
			Optional<Teilnahme> opt = service.aktuelleTeilnahme(teilnahmenummer);

			// Assert
			assertTrue(opt.isEmpty());

		}

		@Test
		void should_GetAktuelleTeilnahmeReturnNonEmpty_when_Angemeldet() {

			// Arrange
			Teilnahme vorhandene = new Privatteilnahme(
				getAktuellerWettbewerb(TestConstants.WETTBEWERBSJAHR_AKTUELL, WettbewerbStatus.ANMELDUNG).id(),
				new Identifier(TestConstants.TEILNAHMENUMMER_PRIVAT));

			when(teilnahmenRepository.addTeilnahme(vorhandene)).thenReturn(Boolean.FALSE);

			// Act
			service.aktuelleTeilnahme(TestConstants.TEILNAHMENUMMER_PRIVAT);

			// Assert
			verify(teilnahmenRepository, never()).addTeilnahme(vorhandene);

		}

	}

	@Nested
	@DisplayName("Schule anmelden")
	class SchuleAnmeldenFeature {

		@Test
		void should_SchuleAnmeldenThrowException_when_PayloadNull() {

			// Arrange
			String uuid = "HZUUFUF";

			try {

				service.schuleAnmelden(null, uuid);
				fail("keine BadRequestException");
			} catch (BadRequestException e) {

				assertEquals("payload darf nicht null sein.", e.getMessage());
				verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
				verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
				verify(eventDelegate, never()).fireSecurityEvent(any(), any());
			}
		}

		@Test
		void should_SchuleAnmeldenThrowException_when_UuidNull() {

			// Arrange
			String uuid = null;
			String schulkuerzel = "TZUTUFFZUF";

			SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(schulkuerzel, "Antonschule");

			try {

				service.schuleAnmelden(payload, uuid);
				fail("keine BadRequestException");
			} catch (BadRequestException e) {

				assertEquals("uuid darf nicht blank sein.", e.getMessage());
				verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
				verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
				verify(eventDelegate, never()).fireSecurityEvent(any(), any());
			}
		}

		@Test
		void should_SchuleAnmeldenThrowException_when_UuidBlank() {

			// Arrange
			String uuid = "  ";
			String schulkuerzel = "TZUTUFFZUF";
			SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(schulkuerzel, "Antonschule");

			try {

				service.schuleAnmelden(payload, uuid);
				fail("keine BadRequestException");
			} catch (BadRequestException e) {

				assertEquals("uuid darf nicht blank sein.", e.getMessage());
				verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
				verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
				verify(eventDelegate, never()).fireSecurityEvent(any(), any());
			}
		}

		@Test
		void should_SchuleAnmeldenThrowException_when_SchulkuerzelNull() {

			// Arrange
			String uuid = "TZUTUFFZUF";
			String schulkuerzel = null;
			SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(schulkuerzel, "Antonschule");

			try {

				service.schuleAnmelden(payload, uuid);
				fail("keine BadRequestException");
			} catch (BadRequestException e) {

				assertEquals("schulkuerzel darf nicht blank sein.", e.getMessage());
				verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
				verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
				verify(eventDelegate, never()).fireSecurityEvent(any(), any());
			}
		}

		@Test
		void should_SchuleAnmeldenThrowException_when_SchulkuerzelBlank() {

			// Arrange
			String uuid = "TZUTUFFZUF";
			String schulkuerzel = " ";
			SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(schulkuerzel, "Antonschule");

			try {

				service.schuleAnmelden(payload, uuid);
				fail("keine BadRequestException");
			} catch (BadRequestException e) {

				assertEquals("schulkuerzel darf nicht blank sein.", e.getMessage());
				verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
				verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
				verify(eventDelegate, never()).fireSecurityEvent(any(), any());
			}
		}

		@Test
		void should_SchuleAnmeldenThrowException_when_SchulnameNull() {

			// Arrange
			String uuid = "TZUTUFFZUF";
			String schulkuerzel = "OJZTFGRD";
			SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(schulkuerzel, null);

			try {

				service.schuleAnmelden(payload, uuid);
				fail("keine BadRequestException");
			} catch (BadRequestException e) {

				assertEquals("schulname darf nicht blank sein.", e.getMessage());
				verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
				verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
				verify(eventDelegate, never()).fireSecurityEvent(any(), any());
			}
		}

		@Test
		void should_SchuleAnmeldenThrowException_when_SchulnameBlank() {

			// Arrange
			String uuid = "TZUTUFFZUF";
			String schulkuerzel = "OJZTFGRD";
			SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(schulkuerzel, " ");

			try {

				service.schuleAnmelden(payload, uuid);
				fail("keine BadRequestException");
			} catch (BadRequestException e) {

				assertEquals("schulname darf nicht blank sein.", e.getMessage());
				verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
				verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
				verify(eventDelegate, never()).fireSecurityEvent(any(), any());
			}
		}

		@Test
		void should_SchuleAnmelden_call_WettbewerbImAnmeldemodus() {

			// Arrange
			when(wettbewerbService.aktuellerWettbewerbImAnmeldemodus()).thenThrow(new IllegalStateException());

			String uuid = "TZUTUFFZUF";
			String schulkuerzel = "UTGFR56FR";
			SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(schulkuerzel, "Antonschule");

			// Act
			ResponsePayload responsePayload = service.schuleAnmelden(payload, uuid);

			verify(veranstalterRepository, never()).ofId(any());
			verify(teilnahmenRepository, never()).ofTeilnahmeIdentifier(any());
			verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
			verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
			verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
			verify(eventDelegate, never()).fireSecurityEvent(any(), any());

			assertEquals("WARN", responsePayload.getMessage().getLevel());

		}

		@Test
		void should_SchuleAnmeldenThrowException_when_NoLehrerFound() {

			// Arrange
			String uuid = "basghoqh";
			String schulkuerzel = "UTGFR56FR";
			SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(schulkuerzel, "Antonschule");

			// Act
			try {

				service.schuleAnmelden(payload, uuid);
				fail("keine AccessDeniedException");

			} catch (AccessDeniedException e) {

				assertEquals("keinen Veranstalter mit UUID=basghoqh gefunden", e.getMessage());
				verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
				verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
				verify(eventDelegate).fireSecurityEvent(any(), any());

			}

		}

		@Test
		void should_SchuleAnmeldenThrowException_when_ZugangUnterlagenEntzogen() {

			// Arrange
			SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(TestConstants.SCHULKUERZEL_1, "Antonschule");

			Veranstalter veranstalter = new Lehrer(new Person(TestConstants.UUID_LEHRER_GESPERRT, "bösewicht"), false,
				Collections.singletonList(new Identifier(TestConstants.SCHULKUERZEL_1)))
					.withZugangUnterlagen(ZugangUnterlagen.ENTZOGEN);

			when(veranstalterRepository.ofId(new Identifier(TestConstants.UUID_LEHRER_GESPERRT)))
				.thenReturn(Optional.of(veranstalter));

			// Act
			try {

				service.schuleAnmelden(payload, TestConstants.UUID_LEHRER_GESPERRT);
				fail("keine AccessDeniedException");

			} catch (AccessDeniedException e) {

				assertEquals("Dem Veranstalter wurde der Zugang zu den Unterlagen entzogen.", e.getMessage());
				verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
				verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
				verify(eventDelegate).fireSecurityEvent(any(), any());
			}
		}

		@Test
		void should_SchuleAnmeldenThrowException_when_VeranstalterKeinLehrer() {

			// Arrange
			String uuid = TestConstants.UUID_PRIVAT;
			String schulkuerzel = TestConstants.SCHULKUERZEL_1;
			SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(schulkuerzel, "Antonschule");

			Veranstalter veranstalter = new Privatveranstalter(new Person(TestConstants.UUID_PRIVAT, "bösewicht"), false,
				Collections.singletonList(new Identifier(TestConstants.SCHULKUERZEL_1)));

			Wettbewerb wettbewerb = getAktuellerWettbewerb(2020, WettbewerbStatus.DOWNLOAD_LEHRER);
			when(wettbewerbService.aktuellerWettbewerbImAnmeldemodus()).thenReturn(wettbewerb);

			when(veranstalterRepository.ofId(new Identifier(TestConstants.UUID_PRIVAT)))
				.thenReturn(Optional.of(veranstalter));

			// Act
			try {

				service.schuleAnmelden(payload, uuid);
				fail("keine AccessDeniedException");

			} catch (AccessDeniedException e) {

				assertEquals("Dies ist ein Privatveranstalter. Nur Lehrer dürfen diese Funktion aufrufen.", e.getMessage());
				verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
				verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
				verify(eventDelegate).fireSecurityEvent(any(), any());
			}
		}

		@Test
		void should_SchuleAnmeldenThrowException_when_LehrerAndererSchule() {

			// Arrange
			String uuid = TestConstants.UUID_LEHRER_ANDERE_SCHULE;
			String schulkuerzel = TestConstants.SCHULKUERZEL_1;
			SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(schulkuerzel, "Antonschule");

			Wettbewerb wettbewerb = getAktuellerWettbewerb(2020, WettbewerbStatus.DOWNLOAD_LEHRER);
			when(wettbewerbService.aktuellerWettbewerbImAnmeldemodus()).thenReturn(wettbewerb);

			Veranstalter veranstalter = new Lehrer(new Person(TestConstants.UUID_LEHRER_ANDERE_SCHULE, "bösewicht"), false,
				Collections.singletonList(new Identifier(TestConstants.SCHULKUERZEL_2)));

			when(veranstalterRepository.ofId(new Identifier(TestConstants.UUID_LEHRER_ANDERE_SCHULE)))
				.thenReturn(Optional.of(veranstalter));

			// Act
			try {

				service.schuleAnmelden(payload, uuid);
				fail("keine AccessDeniedException");

			} catch (AccessDeniedException e) {

				assertEquals("Der Lehrer gehört nicht zur anzumeldenden Schule.", e.getMessage());
				verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
				verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
				verify(eventDelegate).fireSecurityEvent(any(), any());
			}
		}

		@Test
		void should_SchuleAnmeldenDoNothing_when_TeilnahmeVorhanden() {

			// Arrange
			String uuid = TestConstants.UUID_LEHRER_1;
			String schulkuerzel = TestConstants.SCHULKUERZEL_1;
			SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(schulkuerzel, "Antonschule");

			Veranstalter veranstalter = new Lehrer(new Person(TestConstants.UUID_LEHRER_1, "Herr Müller"), false,
				Collections.singletonList(new Identifier(TestConstants.SCHULKUERZEL_1)));

			Wettbewerb wettbewerb = getAktuellerWettbewerb(2020, WettbewerbStatus.DOWNLOAD_LEHRER);
			when(wettbewerbService.aktuellerWettbewerbImAnmeldemodus()).thenReturn(wettbewerb);

			when(veranstalterRepository.ofId(new Identifier(TestConstants.UUID_LEHRER_1)))
				.thenReturn(Optional.of(veranstalter));

			Teilnahme teilnahme = new Schulteilnahme(wettbewerb.id(), new Identifier(TestConstants.SCHULKUERZEL_1),
				payload.schulname(), new Identifier(TestConstants.UUID_LEHRER_1));
			when(teilnahmenRepository.ofTeilnahmeIdentifier(any())).thenReturn(Optional.of(teilnahme));

			// Act
			SchulteilnahmeAPIModel actual = (SchulteilnahmeAPIModel) service.schuleAnmelden(payload, uuid).getData();

			// Assert
			assertEquals("Antonschule", actual.nameUrkunde());
			assertEquals("Herr Müller", actual.angemeldetDurch());
			verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
			verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
			verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
			verify(eventDelegate, never()).fireSecurityEvent(any(), any());
		}

		@Test
		void should_SchuleAnmeldenCreateNew_when_TeilnahmeNichtVorhanden() {

			// Arrange
			String uuid = TestConstants.UUID_LEHRER_1;
			String schulkuerzel = TestConstants.SCHULKUERZEL_1;
			SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(schulkuerzel, "Antonschule");

			Veranstalter veranstalter = new Lehrer(new Person(TestConstants.UUID_LEHRER_1, "Herr Müller"), false,
				Collections.singletonList(new Identifier(TestConstants.SCHULKUERZEL_1)));

			Wettbewerb wettbewerb = getAktuellerWettbewerb(2020, WettbewerbStatus.DOWNLOAD_LEHRER);
			when(wettbewerbService.aktuellerWettbewerbImAnmeldemodus()).thenReturn(wettbewerb);

			when(veranstalterRepository.ofId(new Identifier(TestConstants.UUID_LEHRER_1)))
				.thenReturn(Optional.of(veranstalter));

			// Act
			service.schuleAnmelden(payload, uuid).getData();

			// Assert
			verify(domainEventHandler).handleEvent(any(SchulteilnahmeCreated.class));
			verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
			verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
			verify(eventDelegate, never()).fireSecurityEvent(any(), any());
		}

	}

	@Nested
	@DisplayName("Privatveranstalter anmelden")
	class PrivatpersonAnmeldenFeature {

		@Test
		void should_PrivatpersonAnmeldenThrowException_when_ParameterNull() {

			// Arrange
			String uuid = null;

			try {

				service.privatpersonAnmelden(uuid);
				fail("keine BadRequestException");
			} catch (BadRequestException e) {

				assertEquals("uuid darf nicht blank sein.", e.getMessage());
				verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
				verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
				verify(eventDelegate, never()).fireSecurityEvent(any(), any());
			}

		}

		@Test
		void should_PrivatpersonAnmeldenThrowException_when_ParameterBlank() {

			// Arrange
			String uuid = "      ";

			try {

				service.privatpersonAnmelden(uuid);
				fail("keine BadRequestException");
			} catch (BadRequestException e) {

				assertEquals("uuid darf nicht blank sein.", e.getMessage());
				verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
				verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
				verify(eventDelegate, never()).fireSecurityEvent(any(), any());
			}

		}

		@Test
		void should_PrivatpersonAnmeldenThrowException_when_NoPrivatpersonFound() {

			// Arrange
			String uuid = "basghoqh";

			// Act
			try {

				service.privatpersonAnmelden(uuid);
				fail("keine AccessDeniedException");

			} catch (AccessDeniedException e) {

				assertEquals("keinen Veranstalter mit UUID=basghoqh gefunden", e.getMessage());
				verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
				verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
				verify(eventDelegate).fireSecurityEvent(any(), any());
			}

		}

		@Test
		void should_PrivatpersonAnmeldenThrowException_when_ZugangUnterlagenEntzogen() {

			// Arrange
			Veranstalter veranstalter = new Privatveranstalter(new Person(TestConstants.UUID_PRIVAT_GESPERRT, "bösewicht"), false,
				Collections.singletonList(new Identifier(TestConstants.SCHULKUERZEL_1)))
					.withZugangUnterlagen(ZugangUnterlagen.ENTZOGEN);

			Wettbewerb wettbewerb = getAktuellerWettbewerb(2020, WettbewerbStatus.DOWNLOAD_LEHRER);
			when(wettbewerbService.aktuellerWettbewerbImAnmeldemodus()).thenReturn(wettbewerb);

			when(veranstalterRepository.ofId(new Identifier(TestConstants.UUID_PRIVAT_GESPERRT)))
				.thenReturn(Optional.of(veranstalter));

			// Act
			try {

				service.privatpersonAnmelden(TestConstants.UUID_PRIVAT_GESPERRT);
				fail("keine AccessDeniedException");

			} catch (AccessDeniedException e) {

				assertEquals("Dem Veranstalter wurde der Zugang zu den Unterlagen entzogen.", e.getMessage());
				verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
				verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
				verify(eventDelegate).fireSecurityEvent(any(), any());
			}
		}

		@Test
		void should_PrivatpersonAnmeldenThrowException_when_VeranstalterLehrer() {

			// Arrange
			Veranstalter veranstalter = new Lehrer(new Person(TestConstants.UUID_LEHRER_1, "Herr Müller"), false,
				Collections.singletonList(new Identifier(TestConstants.SCHULKUERZEL_1)));

			when(veranstalterRepository.ofId(new Identifier(TestConstants.UUID_LEHRER_1)))
				.thenReturn(Optional.of(veranstalter));

			// Act
			try {

				service.privatpersonAnmelden(TestConstants.UUID_LEHRER_1);
				fail("keine AccessDeniedException");

			} catch (AccessDeniedException e) {

				assertEquals("Der Veranstalter ist ein Lehrer. Nur Privatprsonen dürfen diese Funktion aufrufen.", e.getMessage());
				verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
				verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
				verify(eventDelegate).fireSecurityEvent(any(), any());
			}
		}

		@Test
		void should_PrivatpersonAnmelden_call_WettbewerbErfasst() {

			// Arrange
			when(wettbewerbService.aktuellerWettbewerbImAnmeldemodus()).thenThrow(new IllegalStateException());

			// Act
			ResponsePayload responsePayload = service.privatpersonAnmelden(TestConstants.UUID_PRIVAT);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			verify(veranstalterRepository, never()).ofId(any());
			verify(teilnahmenRepository, never()).ofTeilnahmeIdentifier(any());
			verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
			verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
			verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
			verify(eventDelegate, never()).fireSecurityEvent(any(), any());

		}

		@Test
		void should_PrivatpersonAnmeldenCreateNew_when_NichtVorhanden() {

			Veranstalter veranstalter = new Privatveranstalter(new Person(TestConstants.UUID_PRIVAT_NICHT_ANGEMELDET, "bösewicht"),
				false,
				Collections.singletonList(new Identifier(TestConstants.TEILNAHMENUMMER_PRIVAT_NICHT_ANGEMELDET)));

			when(veranstalterRepository.ofId(new Identifier(TestConstants.UUID_PRIVAT_NICHT_ANGEMELDET)))
				.thenReturn(Optional.of(veranstalter));

			Wettbewerb wettbewerb = getAktuellerWettbewerb(2020, WettbewerbStatus.DOWNLOAD_LEHRER);
			when(wettbewerbService.aktuellerWettbewerbImAnmeldemodus()).thenReturn(wettbewerb);

			when(teilnahmenRepository.ofTeilnahmeIdentifier(any())).thenReturn(Optional.empty());

			// Act
			PrivatteilnahmeAPIModel teilnahme = (PrivatteilnahmeAPIModel) service
				.privatpersonAnmelden(TestConstants.UUID_PRIVAT_NICHT_ANGEMELDET)
				.getData();

			// Assert
			assertEquals(Teilnahmeart.PRIVAT, teilnahme.identifier().teilnahmeart());
			assertEquals(TestConstants.TEILNAHMENUMMER_PRIVAT_NICHT_ANGEMELDET, teilnahme.identifier().teilnahmenummer());
			assertEquals(TestConstants.WETTBEWERBSJAHR_AKTUELL.intValue(), teilnahme.identifier().jahr());

			verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
			verify(domainEventHandler).handleEvent(any(PrivatteilnahmeCreated.class));
			verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
			verify(eventDelegate, never()).fireSecurityEvent(any(), any());

		}

		@Test
		void should_PrivatpersonAnmeldenDoNothing_when_vorhanden() {

			// Arrange
			Veranstalter veranstalter = new Privatveranstalter(new Person(TestConstants.UUID_PRIVAT, "bösewicht"),
				false,
				Collections.singletonList(new Identifier(TestConstants.TEILNAHMENUMMER_PRIVAT)));

			when(veranstalterRepository.ofId(new Identifier(TestConstants.UUID_PRIVAT)))
				.thenReturn(Optional.of(veranstalter));

			Wettbewerb wettbewerb = getAktuellerWettbewerb(2020, WettbewerbStatus.DOWNLOAD_LEHRER);
			when(wettbewerbService.aktuellerWettbewerbImAnmeldemodus()).thenReturn(wettbewerb);

			Teilnahme teilnahme = new Privatteilnahme(new WettbewerbID(TestConstants.WETTBEWERBSJAHR_AKTUELL),
				new Identifier(TestConstants.TEILNAHMENUMMER_PRIVAT));
			when(teilnahmenRepository.ofTeilnahmeIdentifier(any())).thenReturn(Optional.of(teilnahme));

			// Act
			PrivatteilnahmeAPIModel result = (PrivatteilnahmeAPIModel) service.privatpersonAnmelden(TestConstants.UUID_PRIVAT)
				.getData();

			// Assert
			assertEquals(teilnahme.teilnahmeIdentifier(), result.identifier());

			verify(domainEventHandler, never()).handleEvent(any(SchulteilnahmeCreated.class));
			verify(domainEventHandler, never()).handleEvent(any(PrivatteilnahmeCreated.class));
			verify(eventDelegate, never()).fireDataInconsistencyEvent(any(), any());
			verify(eventDelegate, never()).fireSecurityEvent(any(), any());
		}
	}

	/**
	 * @param  jahr
	 * @return
	 */
	public Wettbewerb getAktuellerWettbewerb(final Integer jahr, final WettbewerbStatus status) {

		return new Wettbewerb(new WettbewerbID(jahr)).withStatus(status);
	}
}
