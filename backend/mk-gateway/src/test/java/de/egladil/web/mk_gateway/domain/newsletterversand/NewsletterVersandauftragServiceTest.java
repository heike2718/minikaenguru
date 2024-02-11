// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.ErrorResponseDto;
import de.egladil.web.mk_gateway.domain.error.Schweregrad;
import de.egladil.web.mk_gateway.domain.mail.Empfaengertyp;
import de.egladil.web.mk_gateway.domain.newsletters.Newsletter;
import de.egladil.web.mk_gateway.domain.newsletters.NewsletterService;
import de.egladil.web.mk_gateway.domain.newsletterversand.api.NewsletterVersandauftrag;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterMailinfoService;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.NewsletterauslieferungenRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * NewsletterVersandauftragServiceTest
 */
@QuarkusTest
public class NewsletterVersandauftragServiceTest {

	@Inject
	NewsletterVersandauftragService service;

	@InjectMock
	VersandauftraegeRepository versandauftraegeRepo;

	@InjectMock
	NewsletterauslieferungenRepository auslieferungenRepository;

	@InjectMock
	NewsletterService newsletterService;

	@InjectMock
	VeranstalterMailinfoService veranstalterMailinfoService;

	@Nested
	class MappingTests {

		@Test
		void should_initVersandauftragMappTheAttributes() {

			// Arrange
			Identifier newsletterIdentifier = new Identifier("newsletterID");
			Empfaengertyp empfaengertyp = Empfaengertyp.PRIVATVERANSTALTER;
			int anzahlEmpfaenger = 2718;

			// Act
			Versandauftrag result = service.initVersandauftrag(newsletterIdentifier, empfaengertyp, anzahlEmpfaenger);

			// Assert
			assertEquals(0, result.anzahlAktuellVersendet());
			assertEquals(2718, result.anzahlEmpaenger());
			assertEquals(empfaengertyp, result.empfaengertyp());
			assertNull(result.fehlermeldung());
			assertNull(result.getErfasstAm());
			assertEquals(StatusAuslieferung.NEU, result.getStatus());
			assertNull(result.identifier());
			assertFalse(result.mitFehler());
			assertEquals(newsletterIdentifier, result.newsletterID());
			assertNull(result.versandBeendetAm());
		}

	}

	@Nested
	class CreateVersandauftragTests {

		@Test
		void should_createVersandauftragThrowWebApplicationException_when_keinNewsletter() {

			// Arrange
			String newsletterId = "hklashl";
			Empfaengertyp empfaengertyp = Empfaengertyp.LEHRER;

			NewsletterVersandauftrag newsletterVersandauftrag = NewsletterVersandauftrag.create(newsletterId, empfaengertyp);

			// Act
			try {

				service.createVersandauftrag(newsletterVersandauftrag);
				fail("keine WebapplicationException");
			} catch (WebApplicationException e) {

				Response response = e.getResponse();
				assertEquals(404, response.getStatus());
				ErrorResponseDto errorPayload = (ErrorResponseDto) response.getEntity();
				assertEquals(Schweregrad.ERROR.toString(), errorPayload.getLevel());
				assertEquals("kein Newsletter mit der ID vorhanden", errorPayload.getMessage());

				verify(newsletterService).findNewsletterWithID(any(Identifier.class));
				verify(veranstalterMailinfoService, never()).getMailempfaengerGroups(empfaengertyp);
				verify(versandauftraegeRepo, never()).findForNewsletter(any(Identifier.class));
				verify(versandauftraegeRepo, never()).saveVersandauftrag(any(Versandauftrag.class));
				verify(versandauftraegeRepo, never()).delete(any(Versandauftrag.class));
				verify(auslieferungenRepository, never()).addAuslieferung(any(NewsletterAuslieferung.class));
			}
		}

		@Test
		void should_createVersandauftragThrowWebApplicationException_when_keineEmpfaenger() {

			// Arrange
			String newsletterId = "hklashl";
			Empfaengertyp empfaengertyp = Empfaengertyp.LEHRER;

			NewsletterVersandauftrag newsletterVersandauftrag = NewsletterVersandauftrag.create(newsletterId, empfaengertyp);

			Newsletter newsletter = new Newsletter();
			when(newsletterService.findNewsletterWithID(any(Identifier.class))).thenReturn(Optional.of(newsletter));
			when(veranstalterMailinfoService.getMailempfaengerGroups(empfaengertyp)).thenReturn(new ArrayList<>());

			// Act
			try {

				service.createVersandauftrag(newsletterVersandauftrag);
				fail("keine WebapplicationException");
			} catch (WebApplicationException e) {

				Response response = e.getResponse();
				assertEquals(412, response.getStatus());
				ErrorResponseDto errorPayload = (ErrorResponseDto) response.getEntity();
				assertEquals(Schweregrad.WARNING.toString(), errorPayload.getLevel());
				assertEquals("keine Empfänger => kein Versand", errorPayload.getMessage());

				verify(newsletterService).findNewsletterWithID(any(Identifier.class));
				verify(veranstalterMailinfoService).getMailempfaengerGroups(empfaengertyp);
				verify(versandauftraegeRepo, never()).findForNewsletter(any(Identifier.class));
				verify(versandauftraegeRepo, never()).saveVersandauftrag(any(Versandauftrag.class));
				verify(versandauftraegeRepo, never()).delete(any(Versandauftrag.class));
				verify(auslieferungenRepository, never()).addAuslieferung(any(NewsletterAuslieferung.class));
			}
		}

		@Test
		void should_createVersandauftragThrowWebApplicationException_when_versandBereitsBeendet() {

			// Arrange
			String expectedMessage = "Newsletter wurde bereits am 14.01.2024 an 12 LEHRER versendet";

			String newsletterId = "hklashl";
			Empfaengertyp empfaengertyp = Empfaengertyp.LEHRER;

			NewsletterVersandauftrag newsletterVersandauftrag = NewsletterVersandauftrag.create(newsletterId, empfaengertyp);

			List<List<String>> empfaengergruppen = getEmpfaengergruppen();

			Versandauftrag auftrag = new Versandauftrag()
				.withAnzahlAktuellVersendet(12)
				.withAnzahlEmpaenger(12)
				.withVersandBeendetAm("14.01.2024")
				.withIdentifier(new Identifier(newsletterId))
				.withStatus(StatusAuslieferung.COMPLETED)
				.withEmpfaengertyp(empfaengertyp);

			List<Versandauftrag> vorhandene = Arrays.asList(new Versandauftrag[] { auftrag });

			Newsletter newsletter = new Newsletter();
			newsletter.withIdentifier(new Identifier(newsletterId));

			when(newsletterService.findNewsletterWithID(any(Identifier.class))).thenReturn(Optional.of(newsletter));
			when(veranstalterMailinfoService.getMailempfaengerGroups(empfaengertyp)).thenReturn(empfaengergruppen);
			when(versandauftraegeRepo.findForNewsletter(any(Identifier.class))).thenReturn(vorhandene);
			verify(versandauftraegeRepo, never()).saveVersandauftrag(any(Versandauftrag.class));
			verify(versandauftraegeRepo, never()).delete(any(Versandauftrag.class));
			verify(auslieferungenRepository, never()).addAuslieferung(any(NewsletterAuslieferung.class));

			// Act
			try {

				service.createVersandauftrag(newsletterVersandauftrag);
				fail("keine WebapplicationException");
			} catch (WebApplicationException e) {

				Response response = e.getResponse();
				assertEquals(409, response.getStatus());
				ErrorResponseDto errorPayload = (ErrorResponseDto) response.getEntity();
				assertEquals(Schweregrad.WARNING.toString(), errorPayload.getLevel());
				assertEquals(expectedMessage, errorPayload.getMessage());

				verify(newsletterService).findNewsletterWithID(any(Identifier.class));
				verify(veranstalterMailinfoService).getMailempfaengerGroups(empfaengertyp);
				verify(versandauftraegeRepo).findForNewsletter(any(Identifier.class));
			}
		}

		@Test
		void should_createVersandauftragThrowWebApplicationException_when_versandNEU() {

			// Arrange
			String expectedMessage = "Newsletter wurde bereits am 13.01.2024 gespeichert. Empfaengertyp=LEHRER, Status=NEU";

			String newsletterId = "hklashl";
			Empfaengertyp empfaengertyp = Empfaengertyp.LEHRER;

			NewsletterVersandauftrag newsletterVersandauftrag = NewsletterVersandauftrag.create(newsletterId, empfaengertyp);

			List<List<String>> empfaengergruppen = getEmpfaengergruppen();

			Versandauftrag auftrag = new Versandauftrag()
				.withAnzahlAktuellVersendet(12)
				.withAnzahlEmpaenger(12)
				.withErfasstAm("13.01.2024")
				.withIdentifier(new Identifier(newsletterId))
				.withStatus(StatusAuslieferung.NEU)
				.withEmpfaengertyp(empfaengertyp);

			List<Versandauftrag> vorhandene = Arrays.asList(new Versandauftrag[] { auftrag });

			Newsletter newsletter = new Newsletter();
			newsletter.withIdentifier(new Identifier(newsletterId));

			when(newsletterService.findNewsletterWithID(any(Identifier.class))).thenReturn(Optional.of(newsletter));
			when(veranstalterMailinfoService.getMailempfaengerGroups(empfaengertyp)).thenReturn(empfaengergruppen);
			when(versandauftraegeRepo.findForNewsletter(any(Identifier.class))).thenReturn(vorhandene);
			verify(versandauftraegeRepo, never()).saveVersandauftrag(any(Versandauftrag.class));
			verify(versandauftraegeRepo, never()).delete(any(Versandauftrag.class));
			verify(auslieferungenRepository, never()).addAuslieferung(any(NewsletterAuslieferung.class));

			// Act
			try {

				service.createVersandauftrag(newsletterVersandauftrag);
				fail("keine WebapplicationException");
			} catch (WebApplicationException e) {

				Response response = e.getResponse();
				assertEquals(409, response.getStatus());
				ErrorResponseDto errorPayload = (ErrorResponseDto) response.getEntity();
				assertEquals(Schweregrad.WARNING.toString(), errorPayload.getLevel());
				assertEquals(
					"Newsletter wurde bereits am 13.01.2024 gespeichert. Empfaengertyp=LEHRER, Status=NEU, Anzahl Empfänger=12",
					errorPayload.getMessage());

				verify(newsletterService).findNewsletterWithID(any(Identifier.class));
				verify(veranstalterMailinfoService).getMailempfaengerGroups(empfaengertyp);
				verify(versandauftraegeRepo).findForNewsletter(any(Identifier.class));
			}
		}

		@Test
		void should_createVersandauftragThrowWebApplicationException_when_versandWAITING() {

			// Arrange
			String newsletterId = "hklashl";
			Empfaengertyp empfaengertyp = Empfaengertyp.LEHRER;

			NewsletterVersandauftrag newsletterVersandauftrag = NewsletterVersandauftrag.create(newsletterId, empfaengertyp);

			List<List<String>> empfaengergruppen = getEmpfaengergruppen();

			Versandauftrag auftrag = new Versandauftrag()
				.withAnzahlAktuellVersendet(12)
				.withAnzahlEmpaenger(12)
				.withErfasstAm("13.01.2024")
				.withIdentifier(new Identifier(newsletterId))
				.withStatus(StatusAuslieferung.WAITING)
				.withEmpfaengertyp(empfaengertyp);

			List<Versandauftrag> vorhandene = Arrays.asList(new Versandauftrag[] { auftrag });

			Newsletter newsletter = new Newsletter();
			newsletter.withIdentifier(new Identifier(newsletterId));

			when(newsletterService.findNewsletterWithID(any(Identifier.class))).thenReturn(Optional.of(newsletter));
			when(veranstalterMailinfoService.getMailempfaengerGroups(empfaengertyp)).thenReturn(empfaengergruppen);
			when(versandauftraegeRepo.findForNewsletter(any(Identifier.class))).thenReturn(vorhandene);
			verify(versandauftraegeRepo, never()).saveVersandauftrag(any(Versandauftrag.class));
			verify(versandauftraegeRepo, never()).delete(any(Versandauftrag.class));
			verify(auslieferungenRepository, never()).addAuslieferung(any(NewsletterAuslieferung.class));

			// Act
			try {

				service.createVersandauftrag(newsletterVersandauftrag);
				fail("keine WebapplicationException");
			} catch (WebApplicationException e) {

				Response response = e.getResponse();
				assertEquals(409, response.getStatus());
				ErrorResponseDto errorPayload = (ErrorResponseDto) response.getEntity();
				assertEquals(Schweregrad.WARNING.toString(), errorPayload.getLevel());
				assertEquals(
					"Newsletter wurde bereits am 13.01.2024 gespeichert. Empfaengertyp=LEHRER, Status=WAITING, Anzahl Empfänger=12",
					errorPayload.getMessage());

				verify(newsletterService).findNewsletterWithID(any(Identifier.class));
				verify(veranstalterMailinfoService).getMailempfaengerGroups(empfaengertyp);
				verify(versandauftraegeRepo).findForNewsletter(any(Identifier.class));
			}
		}

		@Test
		void should_createVersandauftragThrowWebApplicationException_when_versandIN_PROGESS() {

			// Arrange
			String newsletterId = "hklashl";
			Empfaengertyp empfaengertyp = Empfaengertyp.LEHRER;

			NewsletterVersandauftrag newsletterVersandauftrag = NewsletterVersandauftrag.create(newsletterId, empfaengertyp);

			List<List<String>> empfaengergruppen = getEmpfaengergruppen();

			Versandauftrag auftrag = new Versandauftrag()
				.withAnzahlAktuellVersendet(12)
				.withAnzahlEmpaenger(12)
				.withErfasstAm("13.01.2024")
				.withIdentifier(new Identifier(newsletterId))
				.withStatus(StatusAuslieferung.IN_PROGRESS)
				.withEmpfaengertyp(empfaengertyp);

			List<Versandauftrag> vorhandene = Arrays.asList(new Versandauftrag[] { auftrag });

			Newsletter newsletter = new Newsletter();
			newsletter.withIdentifier(new Identifier(newsletterId));

			when(newsletterService.findNewsletterWithID(any(Identifier.class))).thenReturn(Optional.of(newsletter));
			when(veranstalterMailinfoService.getMailempfaengerGroups(empfaengertyp)).thenReturn(empfaengergruppen);
			when(versandauftraegeRepo.findForNewsletter(any(Identifier.class))).thenReturn(vorhandene);
			verify(versandauftraegeRepo, never()).saveVersandauftrag(any(Versandauftrag.class));
			verify(versandauftraegeRepo, never()).delete(any(Versandauftrag.class));
			verify(auslieferungenRepository, never()).addAuslieferung(any(NewsletterAuslieferung.class));

			// Act
			try {

				service.createVersandauftrag(newsletterVersandauftrag);
				fail("keine WebapplicationException");
			} catch (WebApplicationException e) {

				Response response = e.getResponse();
				assertEquals(409, response.getStatus());
				ErrorResponseDto errorPayload = (ErrorResponseDto) response.getEntity();
				assertEquals(Schweregrad.WARNING.toString(), errorPayload.getLevel());
				assertEquals(
					"Newsletter wurde bereits am 13.01.2024 gespeichert. Empfaengertyp=LEHRER, Status=IN_PROGRESS, Anzahl Empfänger=12",
					errorPayload.getMessage());

				verify(newsletterService).findNewsletterWithID(any(Identifier.class));
				verify(veranstalterMailinfoService).getMailempfaengerGroups(empfaengertyp);
				verify(versandauftraegeRepo).findForNewsletter(any(Identifier.class));
			}
		}

		@Test
		void should_createVersandauftragReplaceExisting_when_versandBereitsBeendetAndEmpfaengerTest() {

			// Arrange
			String newsletterId = "hklashl";
			Empfaengertyp empfaengertyp = Empfaengertyp.TEST;

			NewsletterVersandauftrag newsletterVersandauftrag = NewsletterVersandauftrag.create(newsletterId, empfaengertyp);

			List<List<String>> empfaengergruppen = getEmpfaengergruppen();

			Versandauftrag auftrag = new Versandauftrag()
				.withAnzahlAktuellVersendet(12)
				.withAnzahlEmpaenger(12)
				.withVersandBegonnenAm("13.01.2024")
				.withVersandBeendetAm("14.01.2024")
				.withIdentifier(new Identifier("gsauoewo"))
				.withNewsletterID(new Identifier(newsletterId))
				.withStatus(StatusAuslieferung.COMPLETED)
				.withEmpfaengertyp(empfaengertyp);

			Versandauftrag expected = service.initVersandauftrag(new Identifier(newsletterId), empfaengertyp, 23)
				.withIdentifier(new Identifier("kdgwgo"));

			List<Versandauftrag> vorhandene = Arrays.asList(new Versandauftrag[] { auftrag });

			Newsletter newsletter = new Newsletter();
			newsletter.withIdentifier(new Identifier(newsletterId));

			when(newsletterService.findNewsletterWithID(any(Identifier.class))).thenReturn(Optional.of(newsletter));
			when(veranstalterMailinfoService.getMailempfaengerGroups(empfaengertyp)).thenReturn(empfaengergruppen);
			when(versandauftraegeRepo.findForNewsletter(any(Identifier.class))).thenReturn(vorhandene);

			when(versandauftraegeRepo.saveVersandauftrag(any(Versandauftrag.class))).thenReturn(expected);
			doNothing().when(versandauftraegeRepo).delete(any(Versandauftrag.class));

			when(auslieferungenRepository.addAuslieferung(any(NewsletterAuslieferung.class))).thenReturn("ID");

			// Act
			Versandauftrag result = service.createVersandauftrag(newsletterVersandauftrag);

			// Assert
			verify(newsletterService).findNewsletterWithID(any(Identifier.class));
			verify(veranstalterMailinfoService).getMailempfaengerGroups(empfaengertyp);
			verify(versandauftraegeRepo).findForNewsletter(any(Identifier.class));
			verify(versandauftraegeRepo, times(2)).saveVersandauftrag(any(Versandauftrag.class));
			verify(versandauftraegeRepo).delete(any(Versandauftrag.class));
			verify(auslieferungenRepository, times(4)).addAuslieferung(any(NewsletterAuslieferung.class));

			assertNotNull(result);

		}

		@Test
		void should_createVersandauftragCreateNew_when_notExists() {

			// Arrange
			String newsletterId = "hklashl";
			Empfaengertyp empfaengertyp = Empfaengertyp.TEST;

			NewsletterVersandauftrag newsletterVersandauftrag = NewsletterVersandauftrag.create(newsletterId, empfaengertyp);

			List<List<String>> empfaengergruppen = getEmpfaengergruppen();

			Versandauftrag expected = service.initVersandauftrag(new Identifier(newsletterId), empfaengertyp, 23)
				.withIdentifier(new Identifier("kdgwgo"));

			List<Versandauftrag> vorhandene = new ArrayList<>();

			Newsletter newsletter = new Newsletter();
			newsletter.withIdentifier(new Identifier(newsletterId));

			when(newsletterService.findNewsletterWithID(any(Identifier.class))).thenReturn(Optional.of(newsletter));
			when(veranstalterMailinfoService.getMailempfaengerGroups(empfaengertyp)).thenReturn(empfaengergruppen);
			when(versandauftraegeRepo.findForNewsletter(any(Identifier.class))).thenReturn(vorhandene);

			when(versandauftraegeRepo.saveVersandauftrag(any(Versandauftrag.class))).thenReturn(expected);
			doNothing().when(versandauftraegeRepo).delete(any(Versandauftrag.class));

			when(auslieferungenRepository.addAuslieferung(any(NewsletterAuslieferung.class))).thenReturn("ID");

			// Act
			Versandauftrag result = service.createVersandauftrag(newsletterVersandauftrag);

			// Assert
			verify(newsletterService).findNewsletterWithID(any(Identifier.class));
			verify(veranstalterMailinfoService).getMailempfaengerGroups(empfaengertyp);
			verify(versandauftraegeRepo).findForNewsletter(any(Identifier.class));
			verify(versandauftraegeRepo, times(2)).saveVersandauftrag(any(Versandauftrag.class));
			verify(versandauftraegeRepo, never()).delete(any(Versandauftrag.class));
			verify(auslieferungenRepository, times(4)).addAuslieferung(any(NewsletterAuslieferung.class));

			assertNotNull(result);

		}

		@Test
		void should_createVersandauftragThrowWebApplicationException_when_ExceptionOnSaveVersandauftrag() {

			// Arrange
			String expectedMessage = "Beim Anlegen des Versandauftrags ist ein Fehler aufgetreten: anzahl empfänger=12";
			String newsletterId = "hklashl";
			Empfaengertyp empfaengertyp = Empfaengertyp.TEST;

			NewsletterVersandauftrag newsletterVersandauftrag = NewsletterVersandauftrag.create(newsletterId, empfaengertyp);

			List<List<String>> empfaengergruppen = getEmpfaengergruppen();
			List<Versandauftrag> vorhandene = new ArrayList<>();

			Newsletter newsletter = new Newsletter();
			newsletter.withIdentifier(new Identifier(newsletterId));

			when(newsletterService.findNewsletterWithID(any(Identifier.class))).thenReturn(Optional.of(newsletter));
			when(veranstalterMailinfoService.getMailempfaengerGroups(empfaengertyp)).thenReturn(empfaengergruppen);
			when(versandauftraegeRepo.findForNewsletter(any(Identifier.class))).thenReturn(vorhandene);

			when(versandauftraegeRepo.saveVersandauftrag(any(Versandauftrag.class)))
				.thenThrow(new RuntimeException("Versandauftrag kann nicht gespeichert werden"));

			// Act
			try {

				service.createVersandauftrag(newsletterVersandauftrag);
				fail("keine WebapplicationException");
			} catch (WebApplicationException e) {

				Response response = e.getResponse();
				assertEquals(500, response.getStatus());
				ErrorResponseDto errorPayload = (ErrorResponseDto) response.getEntity();
				assertEquals(Schweregrad.ERROR.toString(), errorPayload.getLevel());
				assertEquals(expectedMessage, errorPayload.getMessage());

				verify(newsletterService).findNewsletterWithID(any(Identifier.class));
				verify(veranstalterMailinfoService).getMailempfaengerGroups(empfaengertyp);
				verify(versandauftraegeRepo).findForNewsletter(any(Identifier.class));
				verify(versandauftraegeRepo, times(1)).saveVersandauftrag(any(Versandauftrag.class));
				verify(versandauftraegeRepo, never()).delete(any(Versandauftrag.class));
				verify(auslieferungenRepository, never()).addAuslieferung(any(NewsletterAuslieferung.class));
			}
		}

		@Test
		void should_createVersandauftragThrowWebApplicationException_when_ExceptionOnAddAuslieferung() {

			// Arrange
			String expectedMessage = "Beim Anlegen des Versandauftrags ist ein Fehler aufgetreten: anzahl empfänger=12";
			String newsletterId = "hklashl";
			Empfaengertyp empfaengertyp = Empfaengertyp.TEST;

			NewsletterVersandauftrag newsletterVersandauftrag = NewsletterVersandauftrag.create(newsletterId, empfaengertyp);

			List<List<String>> empfaengergruppen = getEmpfaengergruppen();

			Versandauftrag expected = service.initVersandauftrag(new Identifier(newsletterId), empfaengertyp, 23)
				.withIdentifier(new Identifier("kdgwgo"));

			List<Versandauftrag> vorhandene = new ArrayList<>();

			Newsletter newsletter = new Newsletter();
			newsletter.withIdentifier(new Identifier(newsletterId));

			when(newsletterService.findNewsletterWithID(any(Identifier.class))).thenReturn(Optional.of(newsletter));
			when(veranstalterMailinfoService.getMailempfaengerGroups(empfaengertyp)).thenReturn(empfaengergruppen);
			when(versandauftraegeRepo.findForNewsletter(any(Identifier.class))).thenReturn(vorhandene);

			when(versandauftraegeRepo.saveVersandauftrag(any(Versandauftrag.class))).thenReturn(expected);
			doNothing().when(versandauftraegeRepo).delete(any(Versandauftrag.class));

			when(auslieferungenRepository.addAuslieferung(any(NewsletterAuslieferung.class)))
				.thenThrow(new RuntimeException("Auslieferung kann nicht gespeichert werden"));

			// Act
			try {

				service.createVersandauftrag(newsletterVersandauftrag);
				fail("keine WebapplicationException");
			} catch (WebApplicationException e) {

				Response response = e.getResponse();
				assertEquals(500, response.getStatus());
				ErrorResponseDto errorPayload = (ErrorResponseDto) response.getEntity();
				assertEquals(Schweregrad.ERROR.toString(), errorPayload.getLevel());
				assertEquals(expectedMessage, errorPayload.getMessage());

				verify(newsletterService).findNewsletterWithID(any(Identifier.class));
				verify(veranstalterMailinfoService).getMailempfaengerGroups(empfaengertyp);
				verify(versandauftraegeRepo).findForNewsletter(any(Identifier.class));
				verify(versandauftraegeRepo, times(1)).saveVersandauftrag(any(Versandauftrag.class));
				verify(versandauftraegeRepo, never()).delete(any(Versandauftrag.class));
				verify(auslieferungenRepository, times(1)).addAuslieferung(any(NewsletterAuslieferung.class));
			}
		}
	}

	private List<List<String>> getEmpfaengergruppen() {

		List<List<String>> result = new ArrayList<>();
		result.add(getGruppe());
		result.add(getGruppe());
		result.add(getGruppe());
		result.add(getGruppe());

		return result;

	}

	private List<String> getGruppe() {

		return Arrays.asList(new String[] { "eins@egladil.de", "zwei@egladil.de", "drei@egladil.de" });
	}
}
