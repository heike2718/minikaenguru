// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.api.NewsletterVersandauftrag;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;

/**
 * ScheduleNewsletterDelegateTest
 */
@QuarkusTest
public class ScheduleNewsletterDelegateTest {

	private static final String BEGONNEN_AM = "26.04.2021 10:10:10";

	private static final String BEENDET_AM = "26.04.2021 10:10:23";

	private static final int ANZAHL = 73;

	public static final Identifier NEWSLETTER_ID = new Identifier("gaudowo");

	public static final Identifier VERSANDINFO_ID = new Identifier("hhsfhowho");

	@InjectMock
	VersandinfoService versandinfoService;

	@Inject
	ScheduleNewsletterDelegate delegate;

	@Nested
	class VersandAnTest {

		@Test
		void should_scheduleMailversandSend_when_nochNichtVersendet() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(), Empfaengertyp.TEST);

			List<Versandinformation> vorhandene = new ArrayList<>();

			Versandinformation neue = new Versandinformation()
				.withEmpfaengertyp(Empfaengertyp.TEST)
				.withNewsletterID(NEWSLETTER_ID);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);
			Mockito.when(versandinfoService.versandinformationSpeichern(any())).thenReturn(neue);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(0, versandinformation.anzahlAktuellVersendet());
			assertEquals(0, versandinformation.anzahlEmpaenger());
			assertEquals(null, versandinformation.versandBegonnenAm());
			assertEquals(null, versandinformation.versandBeendetAm());
			assertFalse(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.TEST, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(1)).versandinformationSpeichern(any());
		}

		@Test
		void should_scheduleMailversandSend_when_bereitsVersendetAnAlle() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(), Empfaengertyp.TEST);

			List<Versandinformation> vorhandene = new ArrayList<>();
			Versandinformation info = new Versandinformation().withAnzahlAktuellVersendet(ANZAHL).withAnzahlEmpaenger(ANZAHL)
				.withEmpfaengertyp(Empfaengertyp.ALLE).withNewsletterID(NEWSLETTER_ID).withIdentifier(VERSANDINFO_ID)
				.withVersandBeendetAm(BEENDET_AM).withVersandBegonnenAm(BEGONNEN_AM);
			vorhandene.add(info);

			Versandinformation neue = new Versandinformation()
				.withEmpfaengertyp(Empfaengertyp.TEST)
				.withNewsletterID(NEWSLETTER_ID);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);
			Mockito.when(versandinfoService.versandinformationSpeichern(any())).thenReturn(neue);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(0, versandinformation.anzahlAktuellVersendet());
			assertEquals(0, versandinformation.anzahlEmpaenger());
			assertEquals(null, versandinformation.versandBegonnenAm());
			assertEquals(null, versandinformation.versandBeendetAm());
			assertFalse(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.TEST, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(1)).versandinformationSpeichern(any());
		}

		@Test
		void should_scheduleMailversandSend_when_bereitsVersendetAnLehrer() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(), Empfaengertyp.TEST);

			List<Versandinformation> vorhandene = new ArrayList<>();
			Versandinformation info = new Versandinformation().withAnzahlAktuellVersendet(ANZAHL).withAnzahlEmpaenger(ANZAHL)
				.withEmpfaengertyp(Empfaengertyp.LEHRER).withNewsletterID(NEWSLETTER_ID).withIdentifier(VERSANDINFO_ID)
				.withVersandBeendetAm(BEENDET_AM).withVersandBegonnenAm(BEGONNEN_AM);
			vorhandene.add(info);

			Versandinformation neue = new Versandinformation()
				.withEmpfaengertyp(Empfaengertyp.TEST)
				.withNewsletterID(NEWSLETTER_ID);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);
			Mockito.when(versandinfoService.versandinformationSpeichern(any())).thenReturn(neue);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(0, versandinformation.anzahlAktuellVersendet());
			assertEquals(0, versandinformation.anzahlEmpaenger());
			assertEquals(null, versandinformation.versandBegonnenAm());
			assertEquals(null, versandinformation.versandBeendetAm());
			assertFalse(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.TEST, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(1)).versandinformationSpeichern(any());
		}

		@Test
		void should_scheduleMailversandSend_when_bereitsVersendetAnPrivatveranstelter() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(), Empfaengertyp.TEST);

			List<Versandinformation> vorhandene = new ArrayList<>();
			Versandinformation info = new Versandinformation().withAnzahlAktuellVersendet(ANZAHL).withAnzahlEmpaenger(ANZAHL)
				.withEmpfaengertyp(Empfaengertyp.PRIVATVERANSTALTER).withNewsletterID(NEWSLETTER_ID).withIdentifier(VERSANDINFO_ID)
				.withVersandBeendetAm(BEENDET_AM).withVersandBegonnenAm(BEGONNEN_AM);
			vorhandene.add(info);

			Versandinformation neue = new Versandinformation()
				.withEmpfaengertyp(Empfaengertyp.TEST)
				.withNewsletterID(NEWSLETTER_ID);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);
			Mockito.when(versandinfoService.versandinformationSpeichern(any())).thenReturn(neue);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(0, versandinformation.anzahlAktuellVersendet());
			assertEquals(0, versandinformation.anzahlEmpaenger());
			assertEquals(null, versandinformation.versandBegonnenAm());
			assertEquals(null, versandinformation.versandBeendetAm());
			assertFalse(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.TEST, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(1)).versandinformationSpeichern(any());
		}

		@Test
		void should_scheduleMailversandSend_when_bereitsVersendetAnTest() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(), Empfaengertyp.TEST);

			List<Versandinformation> vorhandene = new ArrayList<>();
			Versandinformation info = new Versandinformation().withAnzahlAktuellVersendet(ANZAHL).withAnzahlEmpaenger(ANZAHL)
				.withEmpfaengertyp(Empfaengertyp.TEST).withNewsletterID(NEWSLETTER_ID).withIdentifier(VERSANDINFO_ID)
				.withVersandBeendetAm(BEENDET_AM).withVersandBegonnenAm(BEGONNEN_AM);
			vorhandene.add(info);

			Versandinformation neue = new Versandinformation()
				.withEmpfaengertyp(Empfaengertyp.TEST)
				.withNewsletterID(NEWSLETTER_ID).withNewsletterID(NEWSLETTER_ID).withIdentifier(VERSANDINFO_ID);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);
			Mockito.when(versandinfoService.versandinformationSpeichern(info)).thenReturn(neue);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(0, versandinformation.anzahlAktuellVersendet());
			assertEquals(0, versandinformation.anzahlEmpaenger());
			assertEquals(null, versandinformation.versandBegonnenAm());
			assertEquals(null, versandinformation.versandBeendetAm());
			assertFalse(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.TEST, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(1)).versandinformationSpeichern(any());
		}

	}

	@Nested
	class VersandAnAlle {

		@Test
		void should_scheduleMailversandSend_when_nochNichtVersendet() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(), Empfaengertyp.ALLE);

			List<Versandinformation> vorhandene = new ArrayList<>();

			Versandinformation neue = new Versandinformation()
				.withEmpfaengertyp(Empfaengertyp.ALLE)
				.withNewsletterID(NEWSLETTER_ID);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);
			Mockito.when(versandinfoService.versandinformationSpeichern(any())).thenReturn(neue);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(0, versandinformation.anzahlAktuellVersendet());
			assertEquals(0, versandinformation.anzahlEmpaenger());
			assertEquals(null, versandinformation.versandBegonnenAm());
			assertEquals(null, versandinformation.versandBeendetAm());
			assertFalse(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.ALLE, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(1)).versandinformationSpeichern(any());
		}

		@Test
		void should_scheduleMailversandSend_when_bereitsVersendetAnTest() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(), Empfaengertyp.ALLE);

			List<Versandinformation> vorhandene = new ArrayList<>();
			Versandinformation info = new Versandinformation().withAnzahlAktuellVersendet(ANZAHL).withAnzahlEmpaenger(ANZAHL)
				.withEmpfaengertyp(Empfaengertyp.TEST).withNewsletterID(NEWSLETTER_ID).withIdentifier(VERSANDINFO_ID)
				.withVersandBeendetAm(BEENDET_AM).withVersandBegonnenAm(BEGONNEN_AM);
			vorhandene.add(info);

			Versandinformation neue = new Versandinformation()
				.withEmpfaengertyp(Empfaengertyp.ALLE)
				.withNewsletterID(NEWSLETTER_ID);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);
			Mockito.when(versandinfoService.versandinformationSpeichern(any())).thenReturn(neue);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(0, versandinformation.anzahlAktuellVersendet());
			assertEquals(0, versandinformation.anzahlEmpaenger());
			assertEquals(null, versandinformation.versandBegonnenAm());
			assertEquals(null, versandinformation.versandBeendetAm());
			assertFalse(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.ALLE, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(1)).versandinformationSpeichern(any());
		}

		@Test
		void should_scheduleMailversandNotSend_when_bereitsVersendetAnLehrer() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(), Empfaengertyp.ALLE);

			List<Versandinformation> vorhandene = new ArrayList<>();
			Versandinformation info = new Versandinformation().withAnzahlAktuellVersendet(ANZAHL).withAnzahlEmpaenger(ANZAHL)
				.withEmpfaengertyp(Empfaengertyp.LEHRER).withNewsletterID(NEWSLETTER_ID).withIdentifier(VERSANDINFO_ID)
				.withVersandBeendetAm(BEENDET_AM).withVersandBegonnenAm(BEGONNEN_AM);
			vorhandene.add(info);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(ANZAHL, versandinformation.anzahlAktuellVersendet());
			assertEquals(ANZAHL, versandinformation.anzahlEmpaenger());
			assertEquals(BEGONNEN_AM, versandinformation.versandBegonnenAm());
			assertEquals(BEENDET_AM, versandinformation.versandBeendetAm());
			assertTrue(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.LEHRER, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(0)).versandinformationSpeichern(any());

		}

		@Test
		void should_scheduleMailversandNotSend_when_bereitsVersendetAnPrivatveranstalter() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(), Empfaengertyp.ALLE);

			List<Versandinformation> vorhandene = new ArrayList<>();
			Versandinformation info = new Versandinformation().withAnzahlAktuellVersendet(ANZAHL).withAnzahlEmpaenger(ANZAHL)
				.withEmpfaengertyp(Empfaengertyp.PRIVATVERANSTALTER).withNewsletterID(NEWSLETTER_ID).withIdentifier(VERSANDINFO_ID)
				.withVersandBeendetAm(BEENDET_AM).withVersandBegonnenAm(BEGONNEN_AM);
			vorhandene.add(info);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(ANZAHL, versandinformation.anzahlAktuellVersendet());
			assertEquals(ANZAHL, versandinformation.anzahlEmpaenger());
			assertEquals(BEGONNEN_AM, versandinformation.versandBegonnenAm());
			assertEquals(BEENDET_AM, versandinformation.versandBeendetAm());
			assertTrue(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.PRIVATVERANSTALTER, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(0)).versandinformationSpeichern(any());
		}

		@Test
		void should_scheduleMailversandNotSendAnAlle_when_bereitsVersendetAnAlle() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(), Empfaengertyp.ALLE);

			List<Versandinformation> vorhandene = new ArrayList<>();
			Versandinformation info = new Versandinformation().withAnzahlAktuellVersendet(ANZAHL).withAnzahlEmpaenger(ANZAHL)
				.withEmpfaengertyp(Empfaengertyp.ALLE).withNewsletterID(NEWSLETTER_ID).withIdentifier(VERSANDINFO_ID)
				.withVersandBeendetAm(BEENDET_AM).withVersandBegonnenAm(BEGONNEN_AM);
			vorhandene.add(info);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(ANZAHL, versandinformation.anzahlAktuellVersendet());
			assertEquals(ANZAHL, versandinformation.anzahlEmpaenger());
			assertEquals(BEGONNEN_AM, versandinformation.versandBegonnenAm());
			assertEquals(BEENDET_AM, versandinformation.versandBeendetAm());
			assertTrue(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.ALLE, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(0)).versandinformationSpeichern(any());
		}

	}

	@Nested
	class VersandAnLehrer {

		@Test
		void should_scheduleMailversandSend_when_nochNichtVersendet() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(), Empfaengertyp.LEHRER);

			List<Versandinformation> vorhandene = new ArrayList<>();

			Versandinformation neue = new Versandinformation()
				.withEmpfaengertyp(Empfaengertyp.LEHRER)
				.withNewsletterID(NEWSLETTER_ID);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);
			Mockito.when(versandinfoService.versandinformationSpeichern(any())).thenReturn(neue);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(0, versandinformation.anzahlAktuellVersendet());
			assertEquals(0, versandinformation.anzahlEmpaenger());
			assertEquals(null, versandinformation.versandBegonnenAm());
			assertEquals(null, versandinformation.versandBeendetAm());
			assertFalse(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.LEHRER, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(1)).versandinformationSpeichern(any());
		}

		@Test
		void should_scheduleMailversandSend_when_bereitsVersendetAnTest() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(), Empfaengertyp.LEHRER);

			List<Versandinformation> vorhandene = new ArrayList<>();
			Versandinformation info = new Versandinformation().withAnzahlAktuellVersendet(ANZAHL).withAnzahlEmpaenger(ANZAHL)
				.withEmpfaengertyp(Empfaengertyp.TEST).withNewsletterID(NEWSLETTER_ID).withIdentifier(VERSANDINFO_ID)
				.withVersandBeendetAm(BEENDET_AM).withVersandBegonnenAm(BEGONNEN_AM);
			vorhandene.add(info);

			Versandinformation neue = new Versandinformation()
				.withEmpfaengertyp(Empfaengertyp.LEHRER)
				.withNewsletterID(NEWSLETTER_ID);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);
			Mockito.when(versandinfoService.versandinformationSpeichern(any())).thenReturn(neue);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(0, versandinformation.anzahlAktuellVersendet());
			assertEquals(0, versandinformation.anzahlEmpaenger());
			assertEquals(null, versandinformation.versandBegonnenAm());
			assertEquals(null, versandinformation.versandBeendetAm());
			assertFalse(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.LEHRER, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(1)).versandinformationSpeichern(any());
		}

		@Test
		void should_scheduleMailversandNotSend_when_bereitsVersendetAnLehrer() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(), Empfaengertyp.LEHRER);

			List<Versandinformation> vorhandene = new ArrayList<>();
			Versandinformation info = new Versandinformation().withAnzahlAktuellVersendet(ANZAHL).withAnzahlEmpaenger(ANZAHL)
				.withEmpfaengertyp(Empfaengertyp.LEHRER).withNewsletterID(NEWSLETTER_ID).withIdentifier(VERSANDINFO_ID)
				.withVersandBeendetAm(BEENDET_AM).withVersandBegonnenAm(BEGONNEN_AM);
			vorhandene.add(info);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(ANZAHL, versandinformation.anzahlAktuellVersendet());
			assertEquals(ANZAHL, versandinformation.anzahlEmpaenger());
			assertEquals(BEGONNEN_AM, versandinformation.versandBegonnenAm());
			assertEquals(BEENDET_AM, versandinformation.versandBeendetAm());
			assertTrue(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.LEHRER, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(0)).versandinformationSpeichern(any());

		}

		@Test
		void should_scheduleMailversandSend_when_bereitsVersendetAnPrivatveranstalter() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(), Empfaengertyp.LEHRER);

			List<Versandinformation> vorhandene = new ArrayList<>();
			Versandinformation info = new Versandinformation().withAnzahlAktuellVersendet(ANZAHL).withAnzahlEmpaenger(ANZAHL)
				.withEmpfaengertyp(Empfaengertyp.PRIVATVERANSTALTER).withNewsletterID(NEWSLETTER_ID).withIdentifier(VERSANDINFO_ID)
				.withVersandBeendetAm(BEENDET_AM).withVersandBegonnenAm(BEGONNEN_AM);
			vorhandene.add(info);

			Versandinformation neue = new Versandinformation()
				.withEmpfaengertyp(Empfaengertyp.LEHRER)
				.withNewsletterID(NEWSLETTER_ID);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);
			Mockito.when(versandinfoService.versandinformationSpeichern(any())).thenReturn(neue);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(0, versandinformation.anzahlAktuellVersendet());
			assertEquals(0, versandinformation.anzahlEmpaenger());
			assertEquals(null, versandinformation.versandBegonnenAm());
			assertEquals(null, versandinformation.versandBeendetAm());
			assertFalse(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.LEHRER, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(1)).versandinformationSpeichern(any());
		}

		@Test
		void should_scheduleMailversandNotSendAnAlle_when_bereitsVersendetAnAlle() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(), Empfaengertyp.LEHRER);

			List<Versandinformation> vorhandene = new ArrayList<>();
			Versandinformation info = new Versandinformation().withAnzahlAktuellVersendet(ANZAHL).withAnzahlEmpaenger(ANZAHL)
				.withEmpfaengertyp(Empfaengertyp.ALLE).withNewsletterID(NEWSLETTER_ID).withIdentifier(VERSANDINFO_ID)
				.withVersandBeendetAm(BEENDET_AM).withVersandBegonnenAm(BEGONNEN_AM);
			vorhandene.add(info);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(ANZAHL, versandinformation.anzahlAktuellVersendet());
			assertEquals(ANZAHL, versandinformation.anzahlEmpaenger());
			assertEquals(BEGONNEN_AM, versandinformation.versandBegonnenAm());
			assertEquals(BEENDET_AM, versandinformation.versandBeendetAm());
			assertTrue(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.ALLE, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(0)).versandinformationSpeichern(any());
		}

	}

	@Nested
	class VersandAnPrivatveranstalter {

		@Test
		void should_scheduleMailversandSend_when_nochNichtVersendet() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(),
				Empfaengertyp.PRIVATVERANSTALTER);

			List<Versandinformation> vorhandene = new ArrayList<>();

			Versandinformation neue = new Versandinformation()
				.withEmpfaengertyp(Empfaengertyp.PRIVATVERANSTALTER)
				.withNewsletterID(NEWSLETTER_ID);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);
			Mockito.when(versandinfoService.versandinformationSpeichern(any())).thenReturn(neue);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(0, versandinformation.anzahlAktuellVersendet());
			assertEquals(0, versandinformation.anzahlEmpaenger());
			assertEquals(null, versandinformation.versandBegonnenAm());
			assertEquals(null, versandinformation.versandBeendetAm());
			assertFalse(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.PRIVATVERANSTALTER, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(1)).versandinformationSpeichern(any());
		}

		@Test
		void should_scheduleMailversandSend_when_bereitsVersendetAnTest() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(),
				Empfaengertyp.PRIVATVERANSTALTER);

			List<Versandinformation> vorhandene = new ArrayList<>();
			Versandinformation info = new Versandinformation().withAnzahlAktuellVersendet(ANZAHL).withAnzahlEmpaenger(ANZAHL)
				.withEmpfaengertyp(Empfaengertyp.TEST).withNewsletterID(NEWSLETTER_ID).withIdentifier(VERSANDINFO_ID)
				.withVersandBeendetAm(BEENDET_AM).withVersandBegonnenAm(BEGONNEN_AM);
			vorhandene.add(info);

			Versandinformation neue = new Versandinformation()
				.withEmpfaengertyp(Empfaengertyp.PRIVATVERANSTALTER)
				.withNewsletterID(NEWSLETTER_ID);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);
			Mockito.when(versandinfoService.versandinformationSpeichern(any())).thenReturn(neue);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(0, versandinformation.anzahlAktuellVersendet());
			assertEquals(0, versandinformation.anzahlEmpaenger());
			assertEquals(null, versandinformation.versandBegonnenAm());
			assertEquals(null, versandinformation.versandBeendetAm());
			assertFalse(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.PRIVATVERANSTALTER, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(1)).versandinformationSpeichern(any());
		}

		@Test
		void should_scheduleMailversandNotSend_when_bereitsVersendetAnPrivatveranstalter() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(),
				Empfaengertyp.PRIVATVERANSTALTER);

			List<Versandinformation> vorhandene = new ArrayList<>();
			Versandinformation info = new Versandinformation().withAnzahlAktuellVersendet(ANZAHL).withAnzahlEmpaenger(ANZAHL)
				.withEmpfaengertyp(Empfaengertyp.PRIVATVERANSTALTER).withNewsletterID(NEWSLETTER_ID).withIdentifier(VERSANDINFO_ID)
				.withVersandBeendetAm(BEENDET_AM).withVersandBegonnenAm(BEGONNEN_AM);
			vorhandene.add(info);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(ANZAHL, versandinformation.anzahlAktuellVersendet());
			assertEquals(ANZAHL, versandinformation.anzahlEmpaenger());
			assertEquals(BEGONNEN_AM, versandinformation.versandBegonnenAm());
			assertEquals(BEENDET_AM, versandinformation.versandBeendetAm());
			assertTrue(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.PRIVATVERANSTALTER, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(0)).versandinformationSpeichern(any());

		}

		@Test
		void should_scheduleMailversandSend_when_bereitsVersendetAnLehrer() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(),
				Empfaengertyp.PRIVATVERANSTALTER);

			List<Versandinformation> vorhandene = new ArrayList<>();
			Versandinformation info = new Versandinformation().withAnzahlAktuellVersendet(ANZAHL).withAnzahlEmpaenger(ANZAHL)
				.withEmpfaengertyp(Empfaengertyp.LEHRER).withNewsletterID(NEWSLETTER_ID).withIdentifier(VERSANDINFO_ID)
				.withVersandBeendetAm(BEENDET_AM).withVersandBegonnenAm(BEGONNEN_AM);
			vorhandene.add(info);

			Versandinformation neue = new Versandinformation()
				.withEmpfaengertyp(Empfaengertyp.PRIVATVERANSTALTER)
				.withNewsletterID(NEWSLETTER_ID);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);
			Mockito.when(versandinfoService.versandinformationSpeichern(any())).thenReturn(neue);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(0, versandinformation.anzahlAktuellVersendet());
			assertEquals(0, versandinformation.anzahlEmpaenger());
			assertEquals(null, versandinformation.versandBegonnenAm());
			assertEquals(null, versandinformation.versandBeendetAm());
			assertFalse(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.PRIVATVERANSTALTER, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(1)).versandinformationSpeichern(any());
		}

		@Test
		void should_scheduleMailversandNotSendAnAlle_when_bereitsVersendetAnAlle() {

			// Arrange
			NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_ID.identifier(),
				Empfaengertyp.PRIVATVERANSTALTER);

			List<Versandinformation> vorhandene = new ArrayList<>();
			Versandinformation info = new Versandinformation().withAnzahlAktuellVersendet(ANZAHL).withAnzahlEmpaenger(ANZAHL)
				.withEmpfaengertyp(Empfaengertyp.ALLE).withNewsletterID(NEWSLETTER_ID).withIdentifier(VERSANDINFO_ID)
				.withVersandBeendetAm(BEENDET_AM).withVersandBegonnenAm(BEGONNEN_AM);
			vorhandene.add(info);

			Mockito.when(versandinfoService.getVersandinformationenZuNewsletter(NEWSLETTER_ID)).thenReturn(vorhandene);

			// Act
			Versandinformation versandinformation = delegate.scheduleMailversand(auftrag);

			// Assert
			assertEquals(ANZAHL, versandinformation.anzahlAktuellVersendet());
			assertEquals(ANZAHL, versandinformation.anzahlEmpaenger());
			assertEquals(BEGONNEN_AM, versandinformation.versandBegonnenAm());
			assertEquals(BEENDET_AM, versandinformation.versandBeendetAm());
			assertTrue(versandinformation.bereitsVersendet());
			assertEquals(Empfaengertyp.ALLE, versandinformation.empfaengertyp());

			Mockito.verify(versandinfoService, times(0)).versandinformationSpeichern(any());
		}

	}

}
