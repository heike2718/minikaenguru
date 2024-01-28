// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_mailer.exception.EmailException;
import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterMailinfoService;

/**
 * NewsletterTaskTest
 */
public class NewsletterTaskTest extends AbstractDomainServiceTest {

	private NewsletterAuftraegeService versandinfoService;

	private VeranstalterMailinfoService veranstalterMailinfoService;

	private AdminEmailsConfiguration mailConfiguration;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();

		mailConfiguration = AdminEmailsConfiguration.createForTest("hdwinkel@egladil.de", 3);
		versandinfoService = NewsletterAuftraegeService.createForTest(getVersandinfosRepository());
		veranstalterMailinfoService = VeranstalterMailinfoService.createForTest(getVeranstalterRepository(), mailConfiguration);
	}

	@Test
	void should_call_work_when_noException() throws Exception {

		// Arrange
		AdminMailService adminMailservice = AdminMailService.createForTest();
		NewsletterService newsletterService = NewsletterService.createForTest(getNewsletterRepository(), versandinfoService,
			veranstalterMailinfoService, adminMailservice);

		Newsletter newsletter = getNewsletterRepository().ofId(new Identifier(NEWSLETTER_PRIVATVERANSTALTER_UUID)).get();

		List<List<String>> mailempfaengerGruppen = veranstalterMailinfoService
			.getMailempfaengerGroups(Empfaengertyp.PRIVATVERANSTALTER);

		Versandauftrag versandinfo = getVersandinfosRepository().ofId(new Identifier(VERSANDINFO_PRIVATVERANSTALTER_UUID))
			.get();

		NewsletterTask task = new NewsletterTask(newsletterService, newsletter, versandinfo, mailempfaengerGruppen);

		// Act
		Versandauftrag result = task.call();

		// Assert
		assertEquals(6, result.anzahlEmpaenger());
		assertEquals(6, result.anzahlAktuellVersendet());
		assertFalse(result.mitFehler());
		assertNotNull(result.versandBegonnenAm());
		assertNotNull(result.versandBeendetAm());
	}

	@Test
	void should_call_work_when_mailException() throws Exception {

		// Arrange
		AdminMailService adminMailservice = AdminMailService.createForTestWithMailException();
		NewsletterService newsletterService = NewsletterService.createForTest(getNewsletterRepository(), versandinfoService,
			veranstalterMailinfoService, adminMailservice);

		Versandauftrag versandinfo = getVersandinfosRepository().ofId(new Identifier(VERSANDINFO_PRIVATVERANSTALTER_UUID))
			.get();

		Newsletter newsletter = getNewsletterRepository().ofId(new Identifier(NEWSLETTER_PRIVATVERANSTALTER_UUID)).get();

		List<List<String>> mailempfaengerGruppen = veranstalterMailinfoService
			.getMailempfaengerGroups(Empfaengertyp.PRIVATVERANSTALTER);

		NewsletterTask task = new NewsletterTask(newsletterService, newsletter, versandinfo, mailempfaengerGruppen);

		// Act
		try {

			task.call();

		} catch (EmailException e) {

			assertEquals("Das ist eine gemockte Mailexception", e.getMessage());
		}

	}

	@Test
	void should_call_work_when_invalidMailAddressException() throws Exception {

		// Arrange
		AdminMailService adminMailservice = AdminMailService.createForTestWithInvalidMailaddressesException();
		NewsletterService newsletterService = NewsletterService.createForTest(getNewsletterRepository(), versandinfoService,
			veranstalterMailinfoService, adminMailservice);

		Versandauftrag versandinfo = getVersandinfosRepository().ofId(new Identifier(VERSANDINFO_PRIVATVERANSTALTER_UUID))
			.get();

		Newsletter newsletter = getNewsletterRepository().ofId(new Identifier(NEWSLETTER_PRIVATVERANSTALTER_UUID)).get();

		List<List<String>> mailempfaengerGruppen = veranstalterMailinfoService
			.getMailempfaengerGroups(Empfaengertyp.PRIVATVERANSTALTER);

		NewsletterTask task = new NewsletterTask(newsletterService, newsletter, versandinfo, mailempfaengerGruppen);

		// Act
		task.call();
	}

}
