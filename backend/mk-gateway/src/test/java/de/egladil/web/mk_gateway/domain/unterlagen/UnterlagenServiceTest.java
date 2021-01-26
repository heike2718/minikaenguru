// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.unterlagen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
				assertEquals("Unbekannter Lehrer mit UUID=UUID_LEHRER_4 versucht, Wettbewerbsunterlagen herunterzuladen.",
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
					"Veranstalter mit UUID=UUID_PRIVAT und Rolle PRIVAT versucht, Wettbewerbsunterlagen für Schulen herunterzuladen.",
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
					"Lehrer UUID=UUID_LEHRER_GESPERRT, Zugang Unterlagen=ENTZOGEN, Wettbewerbsstatus=ANMELDUNG versucht, Wettbewerbsunterlagen für Schulen herunterzuladen.",
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
			theService.setPathExternalFiles("/home/heike/mkv");

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
			theService.setPathExternalFiles("/home/heike/mkv");

			// Act
			DownloadData result = theService.getUnterlagenFuerSchule(lehrerID, Sprache.en);

			// Assert
			assertNotNull(result);
			assertEquals("2020-minikangaroo-english-schools.zip", result.filename());
			assertTrue(result.data().length > 10);
		}
	}
}
