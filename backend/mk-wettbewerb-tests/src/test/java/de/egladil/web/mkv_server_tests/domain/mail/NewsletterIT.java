// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.domain.mail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.Empfaengertyp;
import de.egladil.web.mk_gateway.domain.mail.Newsletter;
import de.egladil.web.mk_gateway.domain.mail.NewsletterRepository;
import de.egladil.web.mk_gateway.domain.mail.NewsletterService;
import de.egladil.web.mk_gateway.domain.mail.VersandinfoService;
import de.egladil.web.mk_gateway.domain.mail.Versandinformation;
import de.egladil.web.mk_gateway.domain.mail.VersandinformationenRepository;
import de.egladil.web.mk_gateway.domain.mail.api.NewsletterAPIModel;
import de.egladil.web.mk_gateway.domain.mail.api.NewsletterVersandauftrag;
import de.egladil.web.mk_gateway.domain.mail.api.VersandinfoAPIModel;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandFinished;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandProgress;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.NewsletterHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.VersandinformationenHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;

/**
 * ScheduleNewsletterDelegateIT
 */
@TestMethodOrder(OrderAnnotation.class)
public class NewsletterIT extends AbstractIntegrationTest {

	private NewsletterService newsletterService;

	private NewsletterRepository newsletterRepository;

	private VersandinformationenRepository versandinfoRepo;

	private VersandinfoService versandinfoService;

	@BeforeEach
	@Override
	protected void setUp() {

		super.setUp();
		newsletterService = NewsletterService.createForIntegrationTest(entityManager);
		newsletterRepository = NewsletterHibernateRepository.createForIntegrationTest(entityManager);
		versandinfoRepo = VersandinformationenHibernateRepository.createForTest(entityManager);
		versandinfoService = VersandinfoService.createForIntegrationTest(entityManager);
	}

	@Test
	@Order(1)
	void should_createNewsletter() {

		// Arrange
		String betreff = "Integrationtest Entwurf";
		String text = "Das ist der erste Entwurf.";
		NewsletterAPIModel model = new NewsletterAPIModel(NewsletterAPIModel.KEINE_UUID, betreff,
			text);

		// Act
		NewsletterAPIModel result = this.speichernInTransaction(model);

		// Assert
		assertFalse(NewsletterAPIModel.KEINE_UUID.equals(result.uuid()));
		List<String> versandinfoIds = result.versandinfoIDs();
		assertTrue(versandinfoIds.isEmpty());

		Optional<Newsletter> optNewsletter = newsletterRepository.ofId(new Identifier(result.uuid()));
		assertTrue(optNewsletter.isPresent());

		Newsletter newsletter = optNewsletter.get();
		assertEquals(betreff, newsletter.betreff());
		assertEquals(text, newsletter.text());
		assertFalse(newsletter.isNeu());
		assertTrue(newsletter.idsVersandinformationen().isEmpty());

	}

	@Test
	@Order(2)
	void should_updateExistingNewsletter() {

		// Arrange
		String betreff = "Integrationtest";
		String text = "Das ist der erste Entwurf. Aber der Text wurde überarbeitet";

		List<Newsletter> allNewsletters = newsletterRepository.loadAll();
		assertEquals(1, allNewsletters.size());

		Newsletter theNewsletter = allNewsletters.get(0);

		NewsletterAPIModel model = NewsletterAPIModel.createFromNewsletter(theNewsletter).withBetreff(betreff).withText(text);

		// Act
		NewsletterAPIModel result = this.speichernInTransaction(model);

		// Assert
		assertEquals(betreff, result.betreff());
		assertEquals(text, result.text());
		assertEquals(theNewsletter.identifier().identifier(), result.uuid());
		List<String> versandinfoIds = result.versandinfoIDs();
		assertTrue(versandinfoIds.isEmpty());

		Optional<Newsletter> optNewsletter = newsletterRepository.ofId(new Identifier(result.uuid()));
		assertTrue(optNewsletter.isPresent());

		Newsletter newsletter = optNewsletter.get();
		assertEquals(betreff, newsletter.betreff());
		assertEquals(text, newsletter.text());
		assertFalse(newsletter.isNeu());
		assertTrue(newsletter.idsVersandinformationen().isEmpty());

	}

	@Test
	@Order(3)
	void should_scheduleToLehrer() {

		// Arrange
		String versandBegonnenAm = "26.04.2021 10:10:10";
		String versandBeendetAm = "26.04.2021 10:10:23";
		int anzahlEmpfaenger = 81;

		List<Newsletter> allNewsletters = newsletterRepository.loadAll();
		assertEquals(1, allNewsletters.size());

		Newsletter theNewsletter = allNewsletters.get(0);

		NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(theNewsletter.identifier().identifier(),
			Empfaengertyp.LEHRER);

		// Act
		ResponsePayload responsePayload = this.scheduleInTransaction(auftrag);

		// Assert
		assertTrue(responsePayload.isOk());
		VersandinfoAPIModel veransdinfoAPIModel = (VersandinfoAPIModel) responsePayload.getData();
		assertNull(veransdinfoAPIModel.versandBegonnenAm());
		assertNull(veransdinfoAPIModel.versandBeendetAm());
		assertEquals(0, veransdinfoAPIModel.anzahlEmpaenger());
		assertEquals(0, veransdinfoAPIModel.anzahlAktuellVersendet());

		Optional<Versandinformation> optVersandinfo = versandinfoRepo.ofId(new Identifier(veransdinfoAPIModel.uuid()));

		assertTrue(optVersandinfo.isPresent());

		waitQuietly(5);

		NewsletterversandProgress progressEventPayload = new NewsletterversandProgress().withAktuellVersendet(anzahlEmpfaenger)
			.withAnzahlEmpfaenger(anzahlEmpfaenger).withVersandBegonnenAm(versandBegonnenAm).withUuid(veransdinfoAPIModel.uuid());

		this.propagateProgressEventInTransaction(progressEventPayload);

		NewsletterversandFinished finishedEventPayload = new NewsletterversandFinished()
			.withMessage("Wird durch Test ausgeloest - Zahlen stimmen daher eher nicht")
			.withUuid(veransdinfoAPIModel.uuid())
			.withVersandBeendetAm(versandBeendetAm);

		this.propagateFinishedEventInTransaction(finishedEventPayload);

		optVersandinfo = versandinfoRepo.ofId(new Identifier(veransdinfoAPIModel.uuid()));

		assertTrue(optVersandinfo.isPresent());

		Versandinformation versandinfo = optVersandinfo.get();
		assertEquals(versandBegonnenAm, versandinfo.versandBegonnenAm());
		assertEquals(versandBeendetAm, versandinfo.versandBeendetAm());
		assertEquals(anzahlEmpfaenger, versandinfo.anzahlEmpaenger());
		assertEquals(anzahlEmpfaenger, versandinfo.anzahlAktuellVersendet());
	}

	@Test
	@Order(4)
	void should_scheduleToLehrerOnlyOnce() {

		// Arrange
		String versandBegonnenAm = "26.04.2021 10:10:10";
		String versandBeendetAm = "26.04.2021 10:10:23";
		int anzahlEmpfaenger = 81;

		List<Newsletter> allNewsletters = newsletterRepository.loadAll();
		assertEquals(1, allNewsletters.size());

		Newsletter theNewsletter = allNewsletters.get(0);

		NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(theNewsletter.identifier().identifier(),
			Empfaengertyp.LEHRER);

		// Act
		ResponsePayload responsePayload = this.scheduleInTransaction(auftrag);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("WARN", messagePayload.getLevel());
		assertEquals("Newsletter wurde bereits am 26.04.2021 10:10:23 an LEHRER versendet", messagePayload.getMessage());

		VersandinfoAPIModel veransdinfoAPIModel = (VersandinfoAPIModel) responsePayload.getData();
		assertEquals(versandBegonnenAm, veransdinfoAPIModel.versandBegonnenAm());
		assertEquals(versandBeendetAm, veransdinfoAPIModel.versandBeendetAm());
		assertEquals(anzahlEmpfaenger, veransdinfoAPIModel.anzahlEmpaenger());
		assertEquals(anzahlEmpfaenger, veransdinfoAPIModel.anzahlAktuellVersendet());
	}

	@Test
	@Order(5)
	void should_scheduleToPrivatveranstalter() {

		// Arrange
		String versandBegonnenAm = "26.04.2021 13:05:10";
		String versandBeendetAm = "26.04.2021 13:12:24";
		int anzahlEmpfaenger = 35;

		List<Newsletter> allNewsletters = newsletterRepository.loadAll();
		assertEquals(1, allNewsletters.size());

		Newsletter theNewsletter = allNewsletters.get(0);

		NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(theNewsletter.identifier().identifier(),
			Empfaengertyp.PRIVATVERANSTALTER);

		// Act
		ResponsePayload responsePayload = this.scheduleInTransaction(auftrag);

		// Assert
		assertTrue(responsePayload.isOk());
		VersandinfoAPIModel veransdinfoAPIModel = (VersandinfoAPIModel) responsePayload.getData();
		assertNull(veransdinfoAPIModel.versandBegonnenAm());
		assertNull(veransdinfoAPIModel.versandBeendetAm());
		assertEquals(0, veransdinfoAPIModel.anzahlEmpaenger());
		assertEquals(0, veransdinfoAPIModel.anzahlAktuellVersendet());

		Optional<Versandinformation> optVersandinfo = versandinfoRepo.ofId(new Identifier(veransdinfoAPIModel.uuid()));

		assertTrue(optVersandinfo.isPresent());

		waitQuietly(5);

		NewsletterversandProgress progressEventPayload = new NewsletterversandProgress().withAktuellVersendet(anzahlEmpfaenger)
			.withAnzahlEmpfaenger(anzahlEmpfaenger).withVersandBegonnenAm(versandBegonnenAm).withUuid(veransdinfoAPIModel.uuid());

		this.propagateProgressEventInTransaction(progressEventPayload);

		NewsletterversandFinished finishedEventPayload = new NewsletterversandFinished()
			.withMessage("Wird durch Test ausgeloest - Zahlen stimmen daher eher nicht")
			.withUuid(veransdinfoAPIModel.uuid())
			.withVersandBeendetAm(versandBeendetAm);

		this.propagateFinishedEventInTransaction(finishedEventPayload);

		optVersandinfo = versandinfoRepo.ofId(new Identifier(veransdinfoAPIModel.uuid()));

		assertTrue(optVersandinfo.isPresent());

		Versandinformation versandinfo = optVersandinfo.get();
		assertEquals(versandBegonnenAm, versandinfo.versandBegonnenAm());
		assertEquals(versandBeendetAm, versandinfo.versandBeendetAm());
		assertEquals(anzahlEmpfaenger, versandinfo.anzahlEmpaenger());
		assertEquals(anzahlEmpfaenger, versandinfo.anzahlAktuellVersendet());
	}

	@Test
	@Order(6)
	void should_scheduleToPrivatveranstalterOnlyOnce() {

		// Arrange
		String versandBegonnenAm = "26.04.2021 13:05:10";
		String versandBeendetAm = "26.04.2021 13:12:24";
		int anzahlEmpfaenger = 35;

		List<Newsletter> allNewsletters = newsletterRepository.loadAll();
		assertEquals(1, allNewsletters.size());

		Newsletter theNewsletter = allNewsletters.get(0);

		NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(theNewsletter.identifier().identifier(),
			Empfaengertyp.PRIVATVERANSTALTER);

		// Act
		ResponsePayload responsePayload = this.scheduleInTransaction(auftrag);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("WARN", messagePayload.getLevel());
		assertEquals("Newsletter wurde bereits am 26.04.2021 13:12:24 an PRIVATVERANSTALTER versendet",
			messagePayload.getMessage());

		VersandinfoAPIModel veransdinfoAPIModel = (VersandinfoAPIModel) responsePayload.getData();
		assertEquals(versandBegonnenAm, veransdinfoAPIModel.versandBegonnenAm());
		assertEquals(versandBeendetAm, veransdinfoAPIModel.versandBeendetAm());
		assertEquals(anzahlEmpfaenger, veransdinfoAPIModel.anzahlEmpaenger());
		assertEquals(anzahlEmpfaenger, veransdinfoAPIModel.anzahlAktuellVersendet());
	}

	@Test
	@Order(7)
	void should_scheduleToTestempfaenger() {

		// Arrange
		String versandBegonnenAm = "26.04.2021 07:05:10";
		String versandBeendetAm = "26.04.2021 07:12:24";
		int anzahlEmpfaenger = 1;

		List<Newsletter> allNewsletters = newsletterRepository.loadAll();
		assertEquals(1, allNewsletters.size());

		Newsletter theNewsletter = allNewsletters.get(0);

		NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(theNewsletter.identifier().identifier(),
			Empfaengertyp.TEST);

		// Act
		ResponsePayload responsePayload = this.scheduleInTransaction(auftrag);

		// Assert
		assertTrue(responsePayload.isOk());
		VersandinfoAPIModel veransdinfoAPIModel = (VersandinfoAPIModel) responsePayload.getData();
		assertNull(veransdinfoAPIModel.versandBegonnenAm());
		assertNull(veransdinfoAPIModel.versandBeendetAm());
		assertEquals(0, veransdinfoAPIModel.anzahlEmpaenger());
		assertEquals(0, veransdinfoAPIModel.anzahlAktuellVersendet());

		Optional<Versandinformation> optVersandinfo = versandinfoRepo.ofId(new Identifier(veransdinfoAPIModel.uuid()));

		assertTrue(optVersandinfo.isPresent());

		waitQuietly(5);

		NewsletterversandProgress progressEventPayload = new NewsletterversandProgress().withAktuellVersendet(anzahlEmpfaenger)
			.withAnzahlEmpfaenger(anzahlEmpfaenger).withVersandBegonnenAm(versandBegonnenAm).withUuid(veransdinfoAPIModel.uuid());

		this.propagateProgressEventInTransaction(progressEventPayload);

		NewsletterversandFinished finishedEventPayload = new NewsletterversandFinished()
			.withMessage("Wird durch Test ausgeloest - Zahlen stimmen daher eher nicht")
			.withUuid(veransdinfoAPIModel.uuid())
			.withVersandBeendetAm(versandBeendetAm);

		this.propagateFinishedEventInTransaction(finishedEventPayload);

		optVersandinfo = versandinfoRepo.ofId(new Identifier(veransdinfoAPIModel.uuid()));

		assertTrue(optVersandinfo.isPresent());

		Versandinformation versandinfo = optVersandinfo.get();
		assertEquals(versandBegonnenAm, versandinfo.versandBegonnenAm());
		assertEquals(versandBeendetAm, versandinfo.versandBeendetAm());
		assertEquals(anzahlEmpfaenger, versandinfo.anzahlEmpaenger());
		assertEquals(anzahlEmpfaenger, versandinfo.anzahlAktuellVersendet());
	}

	@Test
	@Order(8)
	void should_scheduleToTestempfaengerTwice() {

		// Arrange
		String versandBegonnenAm = "26.04.2021 07:45:10";
		String versandBeendetAm = "26.04.2021 07:51:32";
		int anzahlEmpfaenger = 1;

		List<Newsletter> allNewsletters = newsletterRepository.loadAll();
		assertEquals(1, allNewsletters.size());

		Newsletter theNewsletter = allNewsletters.get(0);

		NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create(theNewsletter.identifier().identifier(),
			Empfaengertyp.TEST);

		// Act
		ResponsePayload responsePayload = this.scheduleInTransaction(auftrag);

		// Assert
		VersandinfoAPIModel veransdinfoAPIModel = (VersandinfoAPIModel) responsePayload.getData();
		assertEquals("26.04.2021 07:05:10", veransdinfoAPIModel.versandBegonnenAm());
		assertEquals("26.04.2021 07:12:24", veransdinfoAPIModel.versandBeendetAm());
		assertEquals(1, veransdinfoAPIModel.anzahlEmpaenger());
		assertEquals(1, veransdinfoAPIModel.anzahlAktuellVersendet());

		Optional<Versandinformation> optVersandinfo = versandinfoRepo.ofId(new Identifier(veransdinfoAPIModel.uuid()));

		assertTrue(optVersandinfo.isPresent());

		waitQuietly(5);

		NewsletterversandProgress progressEventPayload = new NewsletterversandProgress().withAktuellVersendet(anzahlEmpfaenger)
			.withAnzahlEmpfaenger(anzahlEmpfaenger).withVersandBegonnenAm(versandBegonnenAm).withUuid(veransdinfoAPIModel.uuid());

		this.propagateProgressEventInTransaction(progressEventPayload);

		NewsletterversandFinished finishedEventPayload = new NewsletterversandFinished()
			.withMessage("Wird durch Test ausgeloest - Zahlen stimmen daher eher nicht")
			.withUuid(veransdinfoAPIModel.uuid())
			.withVersandBeendetAm(versandBeendetAm);

		this.propagateFinishedEventInTransaction(finishedEventPayload);

		optVersandinfo = versandinfoRepo.ofId(new Identifier(veransdinfoAPIModel.uuid()));

		assertTrue(optVersandinfo.isPresent());

		Versandinformation versandinfo = optVersandinfo.get();
		assertEquals(versandBegonnenAm, versandinfo.versandBegonnenAm());
		assertEquals(versandBeendetAm, versandinfo.versandBeendetAm());
		assertEquals(anzahlEmpfaenger, versandinfo.anzahlEmpaenger());
		assertEquals(anzahlEmpfaenger, versandinfo.anzahlAktuellVersendet());
	}

	private NewsletterAPIModel speichernInTransaction(final NewsletterAPIModel model) {

		EntityTransaction trx = entityManager.getTransaction();

		try {

			trx.begin();

			NewsletterAPIModel result = newsletterService.newsletterSpeichern(model);

			commit(trx);

			return result;

		} catch (PersistenceException e) {

			rollback(trx);
			e.printStackTrace();
			throw new RuntimeException("PersistenceException beim Speichern eines Newsletters");

		}

	}

	private ResponsePayload scheduleInTransaction(final NewsletterVersandauftrag auftrag) {

		EntityTransaction trx = entityManager.getTransaction();

		try {

			trx.begin();

			ResponsePayload result = newsletterService.scheduleAndStartMailversand(auftrag);

			commit(trx);

			return result;

		} catch (PersistenceException e) {

			rollback(trx);
			e.printStackTrace();
			throw new RuntimeException("PersistenceException beim Speichern eines Newsletters");

		}
	}

	private void propagateProgressEventInTransaction(final NewsletterversandProgress progressEventPayload) {

		EntityTransaction trx = entityManager.getTransaction();

		try {

			trx.begin();

			versandinfoService.handleDomainEvent(progressEventPayload);

			commit(trx);

		} catch (PersistenceException e) {

			rollback(trx);
			e.printStackTrace();
			throw new RuntimeException("PersistenceException beim Speichern des Fortschritts eines  Newsletterversands");

		}
	}

	private void propagateFinishedEventInTransaction(final NewsletterversandFinished finishedEventPayload) {

		EntityTransaction trx = entityManager.getTransaction();

		try {

			trx.begin();

			versandinfoService.handleDomainEvent(finishedEventPayload);

			commit(trx);

		} catch (PersistenceException e) {

			rollback(trx);
			e.printStackTrace();
			throw new RuntimeException("PersistenceException beim Speichern des Endes eines  Newsletterversands");

		}
	}

	private void waitQuietly(final int seconds) {

		try {

			Thread.sleep(seconds * 1000);

			System.err.println("============> " + seconds + " Sekundenpause beendet");
		} catch (InterruptedException e) {

		}

	}

}
