// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.Empfaengertyp;
import de.egladil.web.mk_gateway.domain.mail.Newsletter;
import de.egladil.web.mk_gateway.domain.mail.Versandinformation;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.NewsletterHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.VersandinformationenHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIT;

/**
 * VersandinformationenHibernateRepositoryTest
 */
public class VersandinformationenHibernateRepositoryTest extends AbstractIT {

	private NewsletterHibernateRepository newsletterRepo;

	private VersandinformationenHibernateRepository versandinfoRepo;

	private Identifier newsletterID;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();

		newsletterRepo = NewsletterHibernateRepository.createForIntegrationTest(entityManagerWettbewerbDB);
		versandinfoRepo = VersandinformationenHibernateRepository.createForTest(entityManagerWettbewerbDB);

		createANewsletter();
	}

	/**
	 *
	 */
	private void createANewsletter() {

		EntityTransaction trx = entityManagerWettbewerbDB.getTransaction();

		try {

			Newsletter newsletter = new Newsletter().withBetreff("Test")
				.withText("Bla blabla blablablabla bla bla. Blubb blubberdiblubb.");

			trx.begin();

			Newsletter neuer = newsletterRepo.addNewsletter(newsletter);

			trx.commit();

			newsletterID = neuer.identifier();

		} catch (Exception e) {

			rollback(trx);
			fail(e.getMessage());
		}

	}

	@Test
	void should_insertUpdateDeleteWork() {

		Identifier generatedID = null;

		Versandinformation versandinfo = new Versandinformation()
			.withAnzahlAktuellVersendet(0)
			.withAnzahlEmpaenger(0)
			.withEmpfaengertyp(Empfaengertyp.LEHRER)
			.withNewsletterID(newsletterID);

		EntityTransaction trx = entityManagerWettbewerbDB.getTransaction();

		try {

			trx.begin();

			Versandinformation neue = versandinfoRepo.addVersandinformation(versandinfo);

			trx.commit();
			generatedID = neue.identifier();

		} catch (Exception e) {

			rollback(trx);
			fail(e.getMessage());
		}

		Versandinformation zweite = new Versandinformation()
			.withAnzahlAktuellVersendet(0)
			.withAnzahlEmpaenger(0)
			.withEmpfaengertyp(Empfaengertyp.TEST)
			.withNewsletterID(newsletterID);

		trx = entityManagerWettbewerbDB.getTransaction();

		try {

			trx.begin();

			Versandinformation zweiteGespeichert = versandinfoRepo.addVersandinformation(zweite);

			trx.commit();

			Optional<Versandinformation> optPersistente = versandinfoRepo.ofId(zweiteGespeichert.identifier());

			assertTrue(optPersistente.isPresent());

		} catch (Exception e) {

			rollback(trx);
			fail(e.getMessage());
		}

		versandinfo.withIdentifier(generatedID).withAnzahlEmpaenger(119).withVersandBegonnenAm("18.12.2020 06:48:02");

		trx = entityManagerWettbewerbDB.getTransaction();

		try {

			trx.begin();

			Versandinformation geaenderte = versandinfoRepo.updateVersandinformation(versandinfo);

			trx.commit();

			assertEquals("18.12.2020 06:48:02", geaenderte.versandBegonnenAm());

		} catch (Exception e) {

			rollback(trx);
			fail(e.getMessage());
		}

		List<Versandinformation> alle = versandinfoRepo.findForNewsletter(newsletterID);

		assertEquals(2, alle.size());

		final Identifier theID = new Identifier(generatedID.identifier());

		Versandinformation erste = alle.stream().filter(v -> theID.equals(v.identifier())).findFirst().get();

		assertEquals("18.12.2020 06:48:02", erste.versandBegonnenAm());

		trx = entityManagerWettbewerbDB.getTransaction();

		try {

			trx.begin();

			versandinfoRepo.removeAll(newsletterID);

			trx.commit();

		} catch (Exception e) {

			rollback(trx);
			fail(e.getMessage());
		}

		alle = versandinfoRepo.findForNewsletter(newsletterID);

		assertEquals(0, alle.size());

	}

}
