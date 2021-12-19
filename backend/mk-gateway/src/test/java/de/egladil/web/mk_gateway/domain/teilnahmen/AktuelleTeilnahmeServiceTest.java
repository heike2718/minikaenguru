// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.BadRequestException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.PrivatteilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.SchulanmeldungRequestPayload;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.SchulteilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.events.PrivatteilnahmeCreated;
import de.egladil.web.mk_gateway.domain.teilnahmen.events.SchulteilnahmeCreated;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * AktuelleTeilnahmeServiceTest
 */
public class AktuelleTeilnahmeServiceTest extends AbstractDomainServiceTest {

	private AktuelleTeilnahmeService service;

	private WettbewerbService wettbewerbService;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();

		wettbewerbService = WettbewerbService.createForTest(getMockitoBasedWettbewerbRepository());

		service = AktuelleTeilnahmeService.createForTest(getTeilnahmenRepository(), wettbewerbService, getVeranstalterRepository());
	}

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

			// Act
			Optional<Teilnahme> opt = service.aktuelleTeilnahme(teilnahmenummer);

			// Assert
			assertTrue(opt.isEmpty());

		}

		@Test
		void should_GetAktuelleTeilnahmeReturnEmpty_when_NichtAngemeldet() {

			// Arrange
			String teilnahmenummer = "HJKGDTDTU";

			// Act
			Optional<Teilnahme> opt = service.aktuelleTeilnahme(teilnahmenummer);

			// Assert
			assertTrue(opt.isEmpty());

		}

		@Test
		void should_GetAktuelleTeilnahmeReturnNonEmpty_when_Angemeldet() {

			// Arrange
			Teilnahme vorhandene = new Privatteilnahme(getAktuellerWettbewerb(WETTBEWERBSJAHR_AKTUELL).id(),
				new Identifier(TEILNAHMENUMMER_PRIVAT));

			// Act
			Optional<Teilnahme> opt = service.aktuelleTeilnahme(TEILNAHMENUMMER_PRIVAT);

			// Assert
			assertEquals(vorhandene, opt.get());
			assertEquals(0, getTeilnahmenRepository().getTeilnahmeAdded());

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
				assertNull(service.schulteilnahmeCreated());
				assertNull(service.privatteilnahmeCreatedEvent());
				assertNull(service.getSecurityIncidentRegistered());
				assertNull(service.getDataInconsistencyRegistered());
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
				assertNull(service.schulteilnahmeCreated());
				assertNull(service.privatteilnahmeCreatedEvent());
				assertNull(service.getSecurityIncidentRegistered());
				assertNull(service.getDataInconsistencyRegistered());
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
				assertNull(service.schulteilnahmeCreated());
				assertNull(service.privatteilnahmeCreatedEvent());
				assertNull(service.getSecurityIncidentRegistered());
				assertNull(service.getDataInconsistencyRegistered());
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
				assertNull(service.schulteilnahmeCreated());
				assertNull(service.privatteilnahmeCreatedEvent());
				assertNull(service.getSecurityIncidentRegistered());
				assertNull(service.getDataInconsistencyRegistered());
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
				assertNull(service.schulteilnahmeCreated());
				assertNull(service.privatteilnahmeCreatedEvent());
				assertNull(service.getSecurityIncidentRegistered());
				assertNull(service.getDataInconsistencyRegistered());
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
				assertNull(service.schulteilnahmeCreated());
				assertNull(service.privatteilnahmeCreatedEvent());
				assertNull(service.getSecurityIncidentRegistered());
				assertNull(service.getDataInconsistencyRegistered());
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
				assertNull(service.schulteilnahmeCreated());
				assertNull(service.privatteilnahmeCreatedEvent());
				assertNull(service.getSecurityIncidentRegistered());
				assertNull(service.getDataInconsistencyRegistered());
			}
		}

		@Test
		void should_SchuleAnmelden_call_WettbewerbImAnmeldemodus() {

			// Arrange
			String expectedMessage = "keine Anmeldung möglich hähähä";

			WettbewerbService mockService = Mockito.mock(WettbewerbService.class);
			Mockito.when(mockService.aktuellerWettbewerbImAnmeldemodus())
				.thenThrow(new IllegalStateException(expectedMessage));

			String uuid = "TZUTUFFZUF";
			String schulkuerzel = "UTGFR56FR";
			SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(schulkuerzel, "Antonschule");

			service = AktuelleTeilnahmeService.createForTest(getTeilnahmenRepository(), mockService, getVeranstalterRepository());

			// Act
			ResponsePayload responsePayload = service.schuleAnmelden(payload, uuid);

			// Assert

			MessagePayload messagePayload = responsePayload.getMessage();

			assertEquals(expectedMessage, messagePayload.getMessage());
			assertEquals("WARN", messagePayload.getLevel());
			assertNull(service.schulteilnahmeCreated());
			assertNull(service.privatteilnahmeCreatedEvent());
			assertNull(service.getSecurityIncidentRegistered());
			assertNull(service.getDataInconsistencyRegistered());

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
				assertNull(service.schulteilnahmeCreated());
				assertNotNull(service.getSecurityIncidentRegistered());
				assertNull(service.privatteilnahmeCreatedEvent());
				assertNull(service.getDataInconsistencyRegistered());

			}

		}

		@Test
		void should_SchuleAnmeldenThrowException_when_ZugangUnterlagenEntzogen() {

			// Arrange
			String uuid = UUID_LEHRER_GESPERRT;
			String schulkuerzel = SCHULKUERZEL_1;
			SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(schulkuerzel, "Antonschule");

			// Act
			try {

				service.schuleAnmelden(payload, uuid);
				fail("keine AccessDeniedException");

			} catch (AccessDeniedException e) {

				assertEquals("Dem Veranstalter wurde der Zugang zu den Unterlagen entzogen.", e.getMessage());
				assertNull(service.schulteilnahmeCreated());
				assertNotNull(service.getSecurityIncidentRegistered());
				assertNull(service.privatteilnahmeCreatedEvent());
				assertNull(service.getDataInconsistencyRegistered());
			}
		}

		@Test
		void should_SchuleAnmeldenThrowException_when_VeranstalterKeinLehrer() {

			// Arrange
			String uuid = UUID_PRIVAT;
			String schulkuerzel = SCHULKUERZEL_1;
			SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(schulkuerzel, "Antonschule");

			// Act
			try {

				service.schuleAnmelden(payload, uuid);
				fail("keine AccessDeniedException");

			} catch (AccessDeniedException e) {

				assertEquals("Dies ist ein Privatveranstalter. Nur Lehrer dürfen diese Funktion aufrufen.", e.getMessage());
				assertNull(service.schulteilnahmeCreated());
				assertNotNull(service.getSecurityIncidentRegistered());
				assertNull(service.privatteilnahmeCreatedEvent());
				assertNull(service.getDataInconsistencyRegistered());
			}
		}

		@Test
		void should_SchuleAnmeldenThrowException_when_LehrerAndererSchule() {

			// Arrange
			String uuid = UUID_LEHRER_ANDERE_SCHULE;
			String schulkuerzel = SCHULKUERZEL_1;
			SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(schulkuerzel, "Antonschule");

			// Act
			try {

				service.schuleAnmelden(payload, uuid);
				fail("keine AccessDeniedException");

			} catch (AccessDeniedException e) {

				assertEquals("Der Lehrer gehört nicht zur anzumeldenden Schule.", e.getMessage());
				assertNull(service.schulteilnahmeCreated());
				assertNotNull(service.getSecurityIncidentRegistered());
				assertNull(service.privatteilnahmeCreatedEvent());
				assertNull(service.getDataInconsistencyRegistered());
			}
		}

		@Test
		void should_SchuleAnmeldenDoNothing_when_TeilnahmeVorhanden() {

			// Arrange
			String uuid = UUID_LEHRER_1;
			String schulkuerzel = SCHULKUERZEL_1;
			SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(schulkuerzel, "Antonschule");

			// Act
			SchulteilnahmeAPIModel actual = (SchulteilnahmeAPIModel) service.schuleAnmelden(payload, uuid).getData();

			// Assert
			assertEquals("Christaschule", actual.nameUrkunde());
			assertEquals("Hans Wurst", actual.angemeldetDurch());
			assertNull(service.schulteilnahmeCreated());
			assertNull(service.getSecurityIncidentRegistered());
			assertNull(service.privatteilnahmeCreatedEvent());
			assertNull(service.getDataInconsistencyRegistered());
		}

		@Test
		void should_SchuleAnmeldenCreateNew_when_TeilnahmeNichtVorhanden() {

			// Arrange
			String uuid = UUID_LEHRER_1;
			String schulkuerzel = SCHULKUERZEL_2;
			SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(schulkuerzel, "Antonschule");

			// Act
			SchulteilnahmeAPIModel actual = (SchulteilnahmeAPIModel) service.schuleAnmelden(payload, uuid).getData();

			// Assert
			assertEquals("Antonschule", actual.nameUrkunde());
			assertEquals("Hans Wurst", actual.angemeldetDurch());

			SchulteilnahmeCreated event = service.schulteilnahmeCreated();

			assertNotNull(event);
			assertEquals("Antonschule", event.schulname());
			assertEquals(SCHULKUERZEL_2, event.teilnahmenummer());
			assertEquals(UUID_LEHRER_1, event.triggeringUser());
			assertEquals(Integer.valueOf(2020), event.wettbewerbsjahr());

			assertNull(service.getSecurityIncidentRegistered());
			assertNull(service.privatteilnahmeCreatedEvent());
			assertNull(service.getDataInconsistencyRegistered());
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
				assertNull(service.privatteilnahmeCreatedEvent());
				assertNull(service.getSecurityIncidentRegistered());
				assertNull(service.getDataInconsistencyRegistered());
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
				assertNull(service.privatteilnahmeCreatedEvent());
				assertNull(service.getSecurityIncidentRegistered());
				assertNull(service.getDataInconsistencyRegistered());
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
				assertNull(service.privatteilnahmeCreatedEvent());
				assertNotNull(service.getSecurityIncidentRegistered());
				assertNull(service.getDataInconsistencyRegistered());
			}

		}

		@Test
		void should_PrivatpersonAnmeldenThrowException_when_ZugangUnterlagenEntzogen() {

			// Arrange
			// Act
			try {

				service.privatpersonAnmelden(UUID_PRIVAT_GESPERRT);
				fail("keine AccessDeniedException");

			} catch (AccessDeniedException e) {

				assertEquals("Dem Veranstalter wurde der Zugang zu den Unterlagen entzogen.", e.getMessage());
				assertNull(service.privatteilnahmeCreatedEvent());
				assertNotNull(service.getSecurityIncidentRegistered());
				assertNull(service.getDataInconsistencyRegistered());
			}
		}

		@Test
		void should_PrivatpersonAnmeldenThrowException_when_VeranstalterLehrer() {

			// Arrange
			// Act
			try {

				service.privatpersonAnmelden(UUID_LEHRER_3);
				fail("keine AccessDeniedException");

			} catch (AccessDeniedException e) {

				assertEquals("Der Veranstalter ist ein Lehrer. Nur Privatprsonen dürfen diese Funktion aufrufen.", e.getMessage());
				assertNull(service.privatteilnahmeCreatedEvent());
				assertNotNull(service.getSecurityIncidentRegistered());
				assertNull(service.getDataInconsistencyRegistered());
			}
		}

		@Test
		void should_PrivatpersonAnmelden_call_WettbewerbImAnmeldemodus() {

			// Arrange
			String expectedMessage = "keine Anmeldung möglich hähähä";

			WettbewerbService mockService = Mockito.mock(WettbewerbService.class);
			Mockito.when(mockService.aktuellerWettbewerbImAnmeldemodus())
				.thenThrow(new IllegalStateException(expectedMessage));

			service = AktuelleTeilnahmeService.createForTest(getTeilnahmenRepository(), mockService, getVeranstalterRepository());

			// Act
			ResponsePayload responsePayload = service.privatpersonAnmelden(UUID_PRIVAT_NICHT_ANGEMELDET);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals(expectedMessage, messagePayload.getMessage());
			assertEquals("WARN", messagePayload.getLevel());
			assertNull(service.privatteilnahmeCreatedEvent());
			assertNull(service.getSecurityIncidentRegistered());
			assertNull(service.getDataInconsistencyRegistered());

		}

		@Test
		void should_PrivatpersonAnmeldenCreateNew_when_NichtVorhanden() {

			// Act
			PrivatteilnahmeAPIModel teilnahme = (PrivatteilnahmeAPIModel) service.privatpersonAnmelden(UUID_PRIVAT_NICHT_ANGEMELDET)
				.getData();

			// Assert
			assertEquals(1, getTeilnahmenRepository().getTeilnahmeAdded());
			assertEquals(Teilnahmeart.PRIVAT, teilnahme.identifier().teilnahmeart());
			assertEquals(TEILNAHMENUMMER_PRIVAT_NICHT_ANGEMELDET, teilnahme.identifier().teilnahmenummer());
			assertEquals(WETTBEWERBSJAHR_AKTUELL.intValue(), teilnahme.identifier().jahr());

			PrivatteilnahmeCreated event = service.privatteilnahmeCreatedEvent();

			assertNotNull(event);
			assertEquals(TEILNAHMENUMMER_PRIVAT_NICHT_ANGEMELDET, event.teilnahmenummer());
			assertEquals(UUID_PRIVAT_NICHT_ANGEMELDET, event.triggeringUser());
			assertEquals(WETTBEWERBSJAHR_AKTUELL, event.wettbewerbsjahr());

			assertNull(service.getSecurityIncidentRegistered());
			assertNull(service.getDataInconsistencyRegistered());

		}

		@Test
		void should_PrivatpersonAnmeldenDoNothing_when_vorhanden() {

			// Act
			PrivatteilnahmeAPIModel teilnahme = (PrivatteilnahmeAPIModel) service.privatpersonAnmelden(UUID_PRIVAT).getData();

			// Assert
			assertEquals(0, getTeilnahmenRepository().getTeilnahmeAdded());
			assertEquals(Teilnahmeart.PRIVAT, teilnahme.identifier().teilnahmeart());
			assertEquals(TEILNAHMENUMMER_PRIVAT, teilnahme.identifier().teilnahmenummer());
			assertEquals(WETTBEWERBSJAHR_AKTUELL.toString(), teilnahme.identifier().wettbewerbID());

			assertNull(service.privatteilnahmeCreatedEvent());
			assertNull(service.getSecurityIncidentRegistered());
			assertNull(service.getDataInconsistencyRegistered());
		}
	}
}
