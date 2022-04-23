// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.unterlagen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.UnterlagenNichtVerfuegbarException;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagenService;

/**
 * UnterlagenServiceTest
 */
public class UnterlagenServiceTest extends AbstractDomainServiceTest {

	private UnterlagenService service;

	@BeforeEach
	@Override
	protected void setUp() {

		super.setUp();
		service = UnterlagenService.createForTest(getWettbewerbService(), getVeranstalterRepository(),
			getZugangUnterlagenService());
	}

	@Nested
	class LehrerTests {

		@Test
		void should_getUnterlagenFuerSchuleThrowNotFoundExceptionAndAddSecurityEvent_when_LehrerNichtVorhanden() {

			// Arrange
			String uuid = "UUID_LEHRER_4";
			Identifier lehrerID = new Identifier(uuid);

			// Act
			try {

				service.getUnterlagenFuerSchule(lehrerID, Sprache.de);
				fail("keine NotFoundException");
			} catch (NotFoundException e) {

				SecurityIncidentRegistered secEventPayload = service.securityIncidentEventPayload();
				assertNotNull(secEventPayload);

				assertEquals("SecurityIncidentRegistered", secEventPayload.typeName());
				assertEquals("Unbekannter Veranstalter mit UUID=UUID_LEHRER_4 versucht, Wettbewerbsunterlagen herunterzuladen.",
					secEventPayload.message());
			}
		}

		@Test
		void should_getUnterlagenFuerSchuleThrowNotFoundExceptionAndAddSecurityEvent_when_uuidPrivat() {

			// Arrange
			Identifier lehrerID = new Identifier(UUID_PRIVAT);

			// Act
			try {

				service.getUnterlagenFuerSchule(lehrerID, Sprache.de);
				fail("keine NotFoundException");
			} catch (NotFoundException e) {

				SecurityIncidentRegistered secEventPayload = service.securityIncidentEventPayload();
				assertNotNull(secEventPayload);

				assertEquals("SecurityIncidentRegistered", secEventPayload.typeName());
				assertEquals(
					"Veranstalter mit UUID=UUID_PRIVAT und Rolle PRIVAT versucht, Wettbewerbsunterlagen über die falsche URL herunterzuladen.",
					secEventPayload.message());
			}
		}

		@Test
		void should_getUnterlagenFuerSchuleThrowUnterlagenNichtVerfuegbarException_when_zugangUnterlagenEntzogen() {

			// Arrange
			Identifier lehrerID = new Identifier(UUID_LEHRER_GESPERRT);

			// Act
			try {

				service.getUnterlagenFuerSchule(lehrerID, Sprache.de);
				fail("keine UnterlagenNichtVerfuegbarException");
			} catch (UnterlagenNichtVerfuegbarException e) {

				SecurityIncidentRegistered secEventPayload = service.securityIncidentEventPayload();
				assertNotNull(secEventPayload);

				assertEquals("SecurityIncidentRegistered", secEventPayload.typeName());
				assertEquals(
					"Veranstalter UUID=UUID_LEHRER_GESPERRT, Zugang Unterlagen=ENTZOGEN, Wettbewerbsstatus=ANMELDUNG versucht, Wettbewerbsunterlagen herunterzuladen.",
					secEventPayload.message());
			}
		}

		@Test
		void should_getUnterlagenFuerSchule_returnDeutschZip_when_spracheDeutsch() {

			// Arrange
			Identifier lehrerID = new Identifier(UUID_LEHRER_1);

			ZugangUnterlagenService zugangsservice = Mockito.mock(ZugangUnterlagenService.class);
			Mockito.when(zugangsservice.hatZugang(UUID_LEHRER_1)).thenReturn(Boolean.TRUE);

			UnterlagenService theService = UnterlagenService.createForTest(getWettbewerbService(), getVeranstalterRepository(),
				zugangsservice);
			theService.setPathExternalFiles("/home/heike/git/testdaten/minikaenguru");

			// Act
			DownloadData result = theService.getUnterlagenFuerSchule(lehrerID, Sprache.de);

			// Assert
			assertNotNull(result);
			assertEquals("2020-minikaenguru-deutsch-schulen.zip", result.filename());
			assertTrue(result.data().length > 10);
		}

		@Test
		void should_getUnterlagenFuerSchule_returnDeutschZip_when_spracheEnglisch() {

			// Arrange
			Identifier lehrerID = new Identifier(UUID_LEHRER_1);

			ZugangUnterlagenService zugangsservice = Mockito.mock(ZugangUnterlagenService.class);
			Mockito.when(zugangsservice.hatZugang(UUID_LEHRER_1)).thenReturn(Boolean.TRUE);

			UnterlagenService theService = UnterlagenService.createForTest(getWettbewerbService(), getVeranstalterRepository(),
				zugangsservice);
			theService.setPathExternalFiles("/home/heike/git/testdaten/minikaenguru");

			// Act
			DownloadData result = theService.getUnterlagenFuerSchule(lehrerID, Sprache.en);

			// Assert
			assertNotNull(result);
			assertEquals("2020-minikangaroo-english-schools.zip", result.filename());
			assertTrue(result.data().length > 10);
		}
	}

	@Nested
	class PrivatveranstalterTests {

		@Test
		void should_getUnterlagenFuerPrivatanmeldungThrowNotFoundExceptionAndAddSecurityEvent_when_VeranstalterNichtVorhanden() {

			// Arrange
			String uuid = "HEINZ";
			Identifier veranstalterID = new Identifier(uuid);

			// Act
			try {

				service.getUnterlagenFuerPrivatanmeldung(veranstalterID, Sprache.de);
				fail("keine NotFoundException");
			} catch (NotFoundException e) {

				SecurityIncidentRegistered secEventPayload = service.securityIncidentEventPayload();
				assertNotNull(secEventPayload);

				assertEquals("SecurityIncidentRegistered", secEventPayload.typeName());
				assertEquals("Unbekannter Veranstalter mit UUID=HEINZ versucht, Wettbewerbsunterlagen herunterzuladen.",
					secEventPayload.message());
			}
		}

		@Test
		void should_getUnterlagenFuerPrivatanmeldungThrowNotFoundExceptionAndAddSecurityEvent_when_uuidLehrer() {

			// Arrange
			Identifier veranstalterID = new Identifier(UUID_LEHRER_1);

			// Act
			try {

				service.getUnterlagenFuerPrivatanmeldung(veranstalterID, Sprache.de);
				fail("keine NotFoundException");
			} catch (NotFoundException e) {

				SecurityIncidentRegistered secEventPayload = service.securityIncidentEventPayload();
				assertNotNull(secEventPayload);

				assertEquals("SecurityIncidentRegistered", secEventPayload.typeName());
				assertEquals(
					"Veranstalter mit UUID=UUID_LEHRER_1 und Rolle LEHRER versucht, Wettbewerbsunterlagen über die falsche URL herunterzuladen.",
					secEventPayload.message());
			}
		}

		@Test
		void should_getUnterlagenFuerPrivatanmeldungThrowUnterlagenNichtVerfuegbarException_when_zugangUnterlagenEntzogen() {

			// Arrange
			Identifier veranstalterID = new Identifier(UUID_PRIVAT_GESPERRT);

			// Act
			try {

				service.getUnterlagenFuerPrivatanmeldung(veranstalterID, Sprache.de);
				fail("keine UnterlagenNichtVerfuegbarException");
			} catch (UnterlagenNichtVerfuegbarException e) {

				SecurityIncidentRegistered secEventPayload = service.securityIncidentEventPayload();
				assertNotNull(secEventPayload);

				assertEquals("SecurityIncidentRegistered", secEventPayload.typeName());
				assertEquals(
					"Veranstalter UUID=UUID_PRIVAT_GESPERRT, Zugang Unterlagen=ENTZOGEN, Wettbewerbsstatus=ANMELDUNG versucht, Wettbewerbsunterlagen herunterzuladen.",
					secEventPayload.message());
			}
		}

		@Test
		void should_getUnterlagenFuerPrivatanmeldung_returnDeutschZip_when_spracheDeutsch() {

			// Arrange
			Identifier lehrerID = new Identifier(UUID_PRIVAT);

			ZugangUnterlagenService zugangsservice = Mockito.mock(ZugangUnterlagenService.class);
			Mockito.when(zugangsservice.hatZugang(UUID_PRIVAT)).thenReturn(Boolean.TRUE);

			UnterlagenService theService = UnterlagenService.createForTest(getWettbewerbService(), getVeranstalterRepository(),
				zugangsservice);
			theService.setPathExternalFiles("/home/heike/git/testdaten/minikaenguru");

			// Act
			DownloadData result = theService.getUnterlagenFuerPrivatanmeldung(lehrerID, Sprache.de);

			// Assert
			assertNotNull(result);
			assertEquals("2020-minikaenguru-deutsch-privat.zip", result.filename());
			assertTrue(result.data().length > 10);
		}

		@Test
		void should_getUnterlagenFuerPrivatanmeldung_returnDeutschZip_when_spracheEnglisch() {

			// Arrange
			Identifier lehrerID = new Identifier(UUID_PRIVAT);

			ZugangUnterlagenService zugangsservice = Mockito.mock(ZugangUnterlagenService.class);
			Mockito.when(zugangsservice.hatZugang(UUID_PRIVAT)).thenReturn(Boolean.TRUE);

			UnterlagenService theService = UnterlagenService.createForTest(getWettbewerbService(), getVeranstalterRepository(),
				zugangsservice);
			theService.setPathExternalFiles("/home/heike/git/testdaten/minikaenguru");

			// Act
			DownloadData result = theService.getUnterlagenFuerPrivatanmeldung(lehrerID, Sprache.en);

			// Assert
			assertNotNull(result);
			assertEquals("2020-minikangaroo-english-private.zip", result.filename());
			assertTrue(result.data().length > 10);
		}
	}
}
