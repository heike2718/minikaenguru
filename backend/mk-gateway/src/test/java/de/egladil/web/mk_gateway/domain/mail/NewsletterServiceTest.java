// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.api.NewsletterAPIModel;
import de.egladil.web.mk_gateway.domain.mail.api.NewsletterVersandauftrag;
import de.egladil.web.mk_gateway.domain.mail.api.VersandinfoAPIModel;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterMailinfoService;

/**
 * NewsletterServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class NewsletterServiceTest {

	private List<List<String>> empfaengergruppen;

	@Mock
	NewsletterRepository newsletterRepositiory;

	@Mock
	VersandinfoService versandinfoService;

	@Mock
	ScheduleNewsletterDelegate scheduleNewsletterDelegate;

	@Mock
	VeranstalterMailinfoService veranstalterMailinfoService;

	@Mock
	AdminMailService mailService;

	@Mock
	ConcurrentSendMailDelegate sendMailDelegate;

	@InjectMocks
	NewsletterService newsletterService;

	@BeforeEach
	void setUp() {

		empfaengergruppen = new ArrayList<>();
		empfaengergruppen.add(Arrays.asList(new String[] { "1@web.de", "2@web.de" }));
	}

	@Test
	void should_getAllNewsletters_work() {

		// Arrange
		Identifier newsletterID1 = new Identifier("IDENT-1");
		Identifier newsletterID2 = new Identifier("IDENT-2");

		List<Newsletter> newsletters = new ArrayList<>();
		newsletters.add(new Newsletter().withBetreff("Betreff 1").withIdentifier(newsletterID1)
			.withText("Text 1"));

		newsletters.add(new Newsletter().withBetreff("Betreff 2").withIdentifier(newsletterID2)
			.withText("Text 2"));

		List<Versandinformation> versandinfosNL1 = new ArrayList<>();

		versandinfosNL1.add(
			new Versandinformation().withAnzahlAktuellVersendet(35).withAnzahlEmpaenger(35).withEmpfaengertyp(Empfaengertyp.LEHRER)
				.withIdentifier(new Identifier("VERSANDINFO-NL1")).withNewsletterID(newsletterID1)
				.withVersandBeendetAm("30.07.2021 10:45:20"));

		when(newsletterRepositiory.loadAll()).thenReturn(newsletters);

		when(versandinfoService.getVersandinformationenZuNewsletter(newsletterID1)).thenReturn(versandinfosNL1);
		when(versandinfoService.getVersandinformationenZuNewsletter(newsletterID2)).thenReturn(new ArrayList<>());

		// Act
		List<NewsletterAPIModel> alleNewsletters = newsletterService.getAllNewsletters();

		// Assert
		assertEquals(2, alleNewsletters.size());

		verify(newsletterRepositiory).loadAll();
		verify(versandinfoService).getVersandinformationenZuNewsletter(newsletterID1);
		verify(versandinfoService).getVersandinformationenZuNewsletter(newsletterID2);

	}

	@Nested
	class AblaufTests {

		@Test
		void should_scheduleAndStartMailversandWork_when_happyHour() {

			// Arrange
			Empfaengertyp empfaengertyp = Empfaengertyp.LEHRER;
			Identifier newsletterID = new Identifier("NEWSLETTER_LEHRER_UUID");

			Newsletter newsletter = new Newsletter().withBetreff("Uiuiui").withIdentifier(newsletterID)
				.withText("Ghu eguwe giqigi, Gtzw kqwh.");
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(newsletterID.identifier(), empfaengertyp);

			final Identifier versandinfoID = new Identifier("VERSANDINFO_LEHRER_UUID");

			Versandinformation neueVersandinformation = new Versandinformation().withEmpfaengertyp(empfaengertyp)
				.withNewsletterID(newsletterID).withIdentifier(versandinfoID);

			when(veranstalterMailinfoService.getMailempfaengerGroups(empfaengertyp)).thenReturn(empfaengergruppen);
			when(scheduleNewsletterDelegate.scheduleMailversand(auftrag)).thenReturn(neueVersandinformation);
			when(newsletterRepositiory.ofId(newsletterID)).thenReturn(Optional.of(newsletter));

			NewsletterTask newsletterTask = new NewsletterTask(newsletterService, newsletter, neueVersandinformation,
				Collections.emptyList());

			doAnswer(invocation -> {

				Versandinformation arg1 = invocation.getArgument(1);

				assertEquals(versandinfoID, arg1.identifier());
				return null;

			}).when(sendMailDelegate).mailsVersenden(newsletterTask, neueVersandinformation);

			// Act
			ResponsePayload responsePayload = newsletterService.scheduleAndStartMailversand(auftrag);

			// Assert
			VersandinfoAPIModel model = (VersandinfoAPIModel) responsePayload.getData();
			assertEquals(versandinfoID.toString(), model.uuid());
			assertEquals(newsletterID.toString(), model.newsletterID());
			assertNull(model.versandBeendetAm());
			assertNull(model.versandBegonnenAm());

			assertEquals(0, model.anzahlAktuellVersendet());
			assertEquals(0, model.anzahlEmpaenger());
			assertFalse(model.versandMitFehler());

			verify(scheduleNewsletterDelegate).scheduleMailversand(auftrag);
			verify(veranstalterMailinfoService).getMailempfaengerGroups(empfaengertyp);
			verify(newsletterRepositiory).ofId(newsletterID);
			verify(sendMailDelegate).mailsVersenden(newsletterTask, neueVersandinformation);
		}

		@Test
		void should_scheduleAndStartMailversandSendetNurEinmal_when_bereitsVersendet() {

			// Arrange
			Empfaengertyp empfaengertyp = Empfaengertyp.LEHRER;
			Identifier newsletterID = new Identifier("NEWSLETTER_LEHRER_UUID");

			Newsletter newsletter = new Newsletter().withBetreff("Uiuiui").withIdentifier(newsletterID)
				.withText("Ghu eguwe giqigi, Gtzw kqwh.");
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(newsletterID.identifier(), empfaengertyp);

			final Identifier versandinfoID = new Identifier("VERSANDINFO_LEHRER_UUID");

			Versandinformation neueVersandinformation = new Versandinformation().withEmpfaengertyp(empfaengertyp)
				.withNewsletterID(newsletterID).withIdentifier(versandinfoID).withVersandBegonnenAm("12.08.2021 00:00:00")
				.withVersandBeendetAm("12.08.2021 00:03:10");

			when(veranstalterMailinfoService.getMailempfaengerGroups(empfaengertyp)).thenReturn(empfaengergruppen);
			when(newsletterRepositiory.ofId(newsletterID)).thenReturn(Optional.of(newsletter));
			when(scheduleNewsletterDelegate.scheduleMailversand(auftrag)).thenReturn(neueVersandinformation);

			NewsletterTask newsletterTask = new NewsletterTask(newsletterService, newsletter, neueVersandinformation,
				Collections.emptyList());

			// Act
			ResponsePayload responsePayload = newsletterService.scheduleAndStartMailversand(auftrag);

			// Assert
			VersandinfoAPIModel model = (VersandinfoAPIModel) responsePayload.getData();
			assertEquals(versandinfoID.toString(), model.uuid());
			assertEquals(newsletterID.toString(), model.newsletterID());
			assertEquals("12.08.2021 00:03:10", model.versandBeendetAm());
			assertEquals("12.08.2021 00:00:00", model.versandBegonnenAm());

			assertEquals(0, model.anzahlAktuellVersendet());
			assertEquals(0, model.anzahlEmpaenger());
			assertFalse(model.versandMitFehler());

			verify(scheduleNewsletterDelegate).scheduleMailversand(auftrag);
			verify(veranstalterMailinfoService).getMailempfaengerGroups(empfaengertyp);
			verify(newsletterRepositiory).ofId(newsletterID);
			verify(sendMailDelegate, never()).mailsVersenden(newsletterTask, neueVersandinformation);
		}

		@Test
		void should_scheduleAndStartMailReturnErrorStatusWithEmptyVersandinfo_when_keinNewsletterMitId() {

			// Arrange
			Empfaengertyp empfaengertyp = Empfaengertyp.LEHRER;

			Identifier newsletterID = new Identifier("NEWSLETTER_LEHRER_UUID");

			Newsletter newsletter = new Newsletter().withBetreff("Uiuiui").withIdentifier(newsletterID)
				.withText("Ghu eguwe giqigi, Gtzw kqwh.");
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(newsletterID.identifier(), empfaengertyp);

			final Identifier versandinfoID = new Identifier("VERSANDINFO_LEHRER_UUID");

			Versandinformation neueVersandinformation = new Versandinformation().withEmpfaengertyp(empfaengertyp)
				.withNewsletterID(newsletterID).withIdentifier(versandinfoID);

			NewsletterTask newsletterTask = new NewsletterTask(newsletterService, newsletter, neueVersandinformation,
				Collections.emptyList());

			when(newsletterRepositiory.ofId(newsletterID)).thenReturn(Optional.empty());

			// Act
			ResponsePayload responsePayload = newsletterService.scheduleAndStartMailversand(auftrag);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();

			verify(scheduleNewsletterDelegate, never()).scheduleMailversand(auftrag);
			verify(newsletterRepositiory).ofId(newsletterID);
			verify(sendMailDelegate, never()).mailsVersenden(newsletterTask, neueVersandinformation);
			verify(veranstalterMailinfoService, never()).getMailempfaengerGroups(empfaengertyp);

			assertEquals("kein Newsletter mit UUID=NEWSLETTER_LEHRER_UUID vorhanden", messagePayload.getMessage());
			assertEquals("ERROR", messagePayload.getLevel());

			VersandinfoAPIModel model = (VersandinfoAPIModel) responsePayload.getData();
			assertEquals("neu", model.uuid());
			assertEquals(newsletterID.toString(), model.newsletterID());
			assertNotNull(model.versandBegonnenAm());
			assertNotNull(model.versandBeendetAm());
			assertEquals(0, model.anzahlAktuellVersendet());
			assertEquals(0, model.anzahlEmpaenger());
			assertEquals(true, model.versandMitFehler());
		}

		@Test
		void should_scheduleAndStartMailversandUpdateVersandinfo_when_EmpfaengerTESTAndAlreadyVersendet() {

			// Arrange
			Empfaengertyp empfaengertyp = Empfaengertyp.TEST;

			Identifier newsletterID = new Identifier("NEWSLETTER_UUID");

			Newsletter newsletter = new Newsletter().withBetreff("Uiuiui").withIdentifier(newsletterID)
				.withText("Ghu eguwe giqigi, Gtzw kqwh.");
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(newsletterID.identifier(), empfaengertyp);

			final Identifier versandinfoID = new Identifier("VERSANDINFO_LEHRER_UUID");

			Versandinformation neueVersandinformation = new Versandinformation().withEmpfaengertyp(empfaengertyp)
				.withNewsletterID(newsletterID).withIdentifier(versandinfoID).withVersandBegonnenAm("12.08.2021 00:00:00")
				.withVersandBeendetAm("12.08.2021 00:03:10");

			when(scheduleNewsletterDelegate.scheduleMailversand(auftrag)).thenReturn(neueVersandinformation);
			when(newsletterRepositiory.ofId(newsletterID)).thenReturn(Optional.of(newsletter));
			when(veranstalterMailinfoService.getMailempfaengerGroups(empfaengertyp)).thenReturn(empfaengergruppen);

			NewsletterTask newsletterTask = new NewsletterTask(newsletterService, newsletter, neueVersandinformation,
				empfaengergruppen);

			doAnswer(invocation -> {

				Versandinformation arg1 = invocation.getArgument(1);

				assertEquals(versandinfoID, arg1.identifier());
				return null;

			}).when(sendMailDelegate).mailsVersenden(newsletterTask, neueVersandinformation);

			// Act
			ResponsePayload responsePayload = newsletterService.scheduleAndStartMailversand(auftrag);

			// Assert
			VersandinfoAPIModel model = (VersandinfoAPIModel) responsePayload.getData();
			assertEquals(versandinfoID.toString(), model.uuid());
			assertEquals(newsletterID.toString(), model.newsletterID());
			assertEquals("12.08.2021 00:03:10", model.versandBeendetAm());
			assertEquals("12.08.2021 00:00:00", model.versandBegonnenAm());

			// assertEquals(0, model.anzahlAktuellVersendet());
			// assertEquals(1, model.anzahlEmpaenger());
			assertFalse(model.versandMitFehler());

			verify(scheduleNewsletterDelegate).scheduleMailversand(auftrag);
			verify(newsletterRepositiory).ofId(newsletterID);
			verify(veranstalterMailinfoService).getMailempfaengerGroups(empfaengertyp);
			verify(sendMailDelegate).mailsVersenden(newsletterTask, neueVersandinformation);
		}

		@Test
		void should_scheduleAndStartMailversandNotStart_when_EmpfaengerlisteLeer() {

			// Arrange
			Empfaengertyp empfaengertyp = Empfaengertyp.LEHRER;
			Identifier newsletterID = new Identifier("NEWSLETTER_UUID");

			Newsletter newsletter = new Newsletter().withBetreff("Uiuiui").withIdentifier(newsletterID)
				.withText("Ghu eguwe giqigi, Gtzw kqwh.");
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(newsletterID.identifier(), empfaengertyp);

			final Identifier versandinfoID = new Identifier("VERSANDINFO_UUID");

			Versandinformation neueVersandinformation = new Versandinformation().withEmpfaengertyp(empfaengertyp)
				.withIdentifier(versandinfoID).withNewsletterID(newsletterID);

			NewsletterTask newsletterTask = new NewsletterTask(newsletterService, newsletter, neueVersandinformation,
				Collections.emptyList());

			when(newsletterRepositiory.ofId(newsletterID)).thenReturn(Optional.of(newsletter));
			when(veranstalterMailinfoService.getMailempfaengerGroups(empfaengertyp)).thenReturn(empfaengergruppen);

			when(scheduleNewsletterDelegate.scheduleMailversand(auftrag))
				.thenThrow(new RuntimeException("Exception beim Erzeugen einer Versantinformation"));

			// Act
			ResponsePayload responsePayload = newsletterService.scheduleAndStartMailversand(auftrag);

			// Assert
			VersandinfoAPIModel model = (VersandinfoAPIModel) responsePayload.getData();
			assertEquals("neu", model.uuid());
			assertEquals(newsletterID.toString(), model.newsletterID());
			assertNotNull(model.versandBegonnenAm());
			assertNotNull(model.versandBeendetAm());
			assertEquals(0, model.anzahlAktuellVersendet());
			assertEquals(2, model.anzahlEmpaenger());

			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("ERROR", messagePayload.getLevel());
			assertEquals(
				"Beim Anlegen des Versandauftrags ist ein Fehler aufgetreten: Exception beim Erzeugen einer Versantinformation",
				messagePayload.getMessage());

			verify(veranstalterMailinfoService).getMailempfaengerGroups(empfaengertyp);
			verify(scheduleNewsletterDelegate).scheduleMailversand(auftrag);
			verify(newsletterRepositiory).ofId(newsletterID);
			verify(sendMailDelegate, never()).mailsVersenden(newsletterTask, neueVersandinformation);
		}

		@Test
		void should_scheduleAndStartMailversandNotStart_when_scheduleDelegateThrowsExcepion() {

			// Arrange
			Empfaengertyp empfaengertyp = Empfaengertyp.LEHRER;
			Identifier newsletterID = new Identifier("NEWSLETTER_UUID");

			Newsletter newsletter = new Newsletter().withBetreff("Uiuiui").withIdentifier(newsletterID)
				.withText("Ghu eguwe giqigi, Gtzw kqwh.");
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(newsletterID.identifier(), empfaengertyp);

			final Identifier versandinfoID = new Identifier("VERSANDINFO_UUID");

			Versandinformation neueVersandinformation = new Versandinformation().withEmpfaengertyp(empfaengertyp)
				.withIdentifier(versandinfoID).withNewsletterID(newsletterID);

			NewsletterTask newsletterTask = new NewsletterTask(newsletterService, newsletter, neueVersandinformation,
				Collections.emptyList());

			when(newsletterRepositiory.ofId(newsletterID)).thenReturn(Optional.of(newsletter));
			when(veranstalterMailinfoService.getMailempfaengerGroups(empfaengertyp)).thenReturn(Collections.emptyList());

			// Act
			ResponsePayload responsePayload = newsletterService.scheduleAndStartMailversand(auftrag);

			// Assert
			VersandinfoAPIModel model = (VersandinfoAPIModel) responsePayload.getData();
			assertEquals("neu", model.uuid());
			assertEquals(newsletterID.toString(), model.newsletterID());
			assertNotNull(model.versandBegonnenAm());
			assertNotNull(model.versandBeendetAm());
			assertEquals(0, model.anzahlAktuellVersendet());
			assertEquals(0, model.anzahlEmpaenger());

			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals("Keine Empfängeradressen für LEHRER gefunden.", messagePayload.getMessage());

			verify(veranstalterMailinfoService).getMailempfaengerGroups(empfaengertyp);
			verify(scheduleNewsletterDelegate, never()).scheduleMailversand(auftrag);
			verify(newsletterRepositiory).ofId(newsletterID);
			verify(sendMailDelegate, never()).mailsVersenden(newsletterTask, neueVersandinformation);

		}
	}

	@Nested
	class FinishTests {

		@Test
		void should_createFinishedVersandinformationSetVersandintervall() {

			// Arrange
			Empfaengertyp empfaengertyp = Empfaengertyp.LEHRER;
			String newsletterID = "NEWSLETTER-ID";

			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(newsletterID, empfaengertyp);

			// Act
			Versandinformation result = newsletterService.createFinishedVersandinfo(auftrag);

			// Assert
			assertNotNull(result.versandBegonnenAm());
			assertNotNull(result.versandBeendetAm());
			assertEquals("neu", result.identifier().toString());
			assertEquals(0, result.anzahlAktuellVersendet());
			assertEquals(0, result.anzahlEmpaenger());
			assertEquals(newsletterID, result.newsletterID().toString());
		}
	}
}
