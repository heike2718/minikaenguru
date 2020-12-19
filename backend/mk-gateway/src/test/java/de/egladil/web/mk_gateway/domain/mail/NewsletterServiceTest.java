// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.api.NewsletterAPIModel;
import de.egladil.web.mk_gateway.domain.mail.api.NewsletterVersandauftrag;
import de.egladil.web.mk_gateway.domain.mail.api.VersandinfoAPIModel;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterMailinfoService;

/**
 * NewsletterServiceTest
 */
public class NewsletterServiceTest extends AbstractDomainServiceTest {

	private NewsletterService newsletterService;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();

		AdminEmailsConfiguration mailConfiguration = AdminEmailsConfiguration.createForTest("hdwinkel@egladil.de", 3);
		VersandinfoService versandinfoService = VersandinfoService.createForTest(getVersandinfosRepository());
		VeranstalterMailinfoService veranstalterMailinfoService = VeranstalterMailinfoService
			.createForTest(getVeranstalterRepository(), mailConfiguration);

		AdminMailService adminMailservice = AdminMailService.createForTest();

		newsletterService = NewsletterService.createForTest(getNewsletterRepository(), versandinfoService,
			veranstalterMailinfoService, adminMailservice);
	}

	@Test
	void should_getAllNewsletters_work() {

		// Act
		List<NewsletterAPIModel> alleNewsletters = newsletterService.getAllNewsletters();

		// Assert
		assertEquals(3, alleNewsletters.size());

		{

			NewsletterAPIModel apiModel = alleNewsletters.stream()
				.filter(m -> m.uuid().equals(AbstractDomainServiceTest.NEWSLETTER_TEST_UND_ALLE_UUID)).findFirst().get();

			assertEquals("Wichtige Information an alle", apiModel.betreff());
			assertEquals("Bla bla bla blablablabla bla bla", apiModel.text());

			List<String> versandinfoIDs = apiModel.versandinfoIDs();

			assertEquals(2, versandinfoIDs.size());
			assertTrue(versandinfoIDs.contains(AbstractDomainServiceTest.VERSANDINFO_ALLE_UUID));
			assertTrue(versandinfoIDs.contains(AbstractDomainServiceTest.VERSANDINFO_TEST_UUID));
		}

		{

			NewsletterAPIModel apiModel = alleNewsletters.stream()
				.filter(m -> m.uuid().equals(AbstractDomainServiceTest.NEWSLETTER_PRIVATVERANSTALTER_UUID)).findFirst().get();

			assertEquals("Nur für Privatveranstalter", apiModel.betreff());
			assertEquals("Würg würg würg würgwürgwürgwürg würg würg", apiModel.text());

			List<String> versandinfoIDs = apiModel.versandinfoIDs();

			assertEquals(1, versandinfoIDs.size());
			assertTrue(versandinfoIDs.contains(AbstractDomainServiceTest.VERSANDINFO_PRIVATVERANSTALTER_UUID));
		}

		{

			NewsletterAPIModel apiModel = alleNewsletters.stream()
				.filter(m -> m.uuid().equals(AbstractDomainServiceTest.NEWSLETTER_LEHRER_UUID)).findFirst().get();

			assertEquals("Nur für Lehrer", apiModel.betreff());
			assertEquals("Blubb blubb blubb blubbblubbblubbblubb blubb blubb", apiModel.text());

			List<String> versandinfoIDs = apiModel.versandinfoIDs();

			assertEquals(1, versandinfoIDs.size());
			assertTrue(versandinfoIDs.contains(AbstractDomainServiceTest.VERSANDINFO_LEHRER_UUID));
		}

	}

	@Test
	void should_newsletterSpeichernCreateNew_when_uuidNeu() {

		// Arrange
		NewsletterAPIModel model = new NewsletterAPIModel(NewsletterAPIModel.KEINE_UUID, "ein Betreff", "ein Text");

		// Act
		NewsletterAPIModel result = newsletterService.newsletterSpeichern(model);

		// Assert
		assertEquals("ein Betreff", result.betreff());
		assertEquals("ein Text", result.text());
		assertFalse(result.uuid().equals(model.uuid()));

		assertEquals(1, getNewsletterRepository().getNewsletterAdded());
		assertEquals(0, getNewsletterRepository().getNewsletterChanged());
		assertEquals(0, getNewsletterRepository().getNewsletterRemoved());
	}

	@Test
	void should_newsletterSpeichernUpdateExisting_when_uuidNeu() {

		// Arrange
		NewsletterAPIModel model = new NewsletterAPIModel(AbstractDomainServiceTest.NEWSLETTER_LEHRER_UUID, "ein Betreff",
			"ein Text");

		// Act
		NewsletterAPIModel result = newsletterService.newsletterSpeichern(model);

		// Assert
		assertEquals("ein Betreff", result.betreff());
		assertEquals("ein Text", result.text());
		assertEquals(AbstractDomainServiceTest.NEWSLETTER_LEHRER_UUID, result.uuid());

		List<String> versandinfoIDs = result.versandinfoIDs();

		assertEquals(1, versandinfoIDs.size());
		assertTrue(versandinfoIDs.contains(AbstractDomainServiceTest.VERSANDINFO_LEHRER_UUID));

		assertEquals(0, getNewsletterRepository().getNewsletterAdded());
		assertEquals(1, getNewsletterRepository().getNewsletterChanged());
		assertEquals(0, getNewsletterRepository().getNewsletterRemoved());
	}

	@Test
	void should_scheduleAndStartMailversandWork_when_happyHour() {

		// Arrange
		NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(NEWSLETTER_LEHRER_UUID, Empfaengertyp.LEHRER);

		ScheduleNewsletterDelegate scheduleDelegate = Mockito.mock(ScheduleNewsletterDelegate.class);
		Versandinformation neueVersandinformation = new Versandinformation().withEmpfaengertyp(Empfaengertyp.LEHRER)
			.withNewsletterID(new Identifier(NEWSLETTER_LEHRER_UUID)).withIdentifier(new Identifier(VERSANDINFO_LEHRER_UUID));

		Mockito.when(scheduleDelegate.scheduleMailversand(auftrag)).thenReturn(neueVersandinformation);

		newsletterService.withMockedScheduleNewsletterDelegate(scheduleDelegate);

		// Act
		VersandinfoAPIModel model = newsletterService.scheduleAndStartMailversand(auftrag);

		// waitQuietly(15);

		// Assert
		assertEquals(VERSANDINFO_LEHRER_UUID, model.uuid());
		assertEquals(NEWSLETTER_LEHRER_UUID, model.newsletterID());
		assertNotNull(model.versandBeendetAm());
		assertNotNull(model.versandBegonnenAm());

		assertEquals(6, model.anzahlAktuellVersendet());
		assertEquals(6, model.anzahlEmpaenger());
		assertFalse(model.versandMitFehler());
	}
}
