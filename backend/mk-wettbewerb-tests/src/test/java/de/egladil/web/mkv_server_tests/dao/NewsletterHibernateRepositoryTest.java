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
import de.egladil.web.mk_gateway.domain.mail.Newsletter;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.NewsletterHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIT;

/**
 * NewsletterHibernateRepositoryTest
 */
public class NewsletterHibernateRepositoryTest extends AbstractIT {

	private NewsletterHibernateRepository repo;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		repo = NewsletterHibernateRepository.createForIntegrationTest(entityManager);
	}

	@Test
	void should_insertUpdateDeleteWork() {

		Identifier generatedID = null;

		// Arrange
		Newsletter newsletter = new Newsletter().withBetreff("Test")
			.withText("Bla blabla blablablabla bla bla\nblubb blubberdiblubb.");

		EntityTransaction trx = entityManager.getTransaction();

		try {

			trx.begin();

			Newsletter gespeicherter = repo.addNewsletter(newsletter);
			generatedID = gespeicherter.identifier();

			trx.commit();

		} catch (Exception e) {

			rollback(trx);
			fail(e.getMessage());
		}

		List<Newsletter> alle = repo.loadAll();

		assertEquals(1, alle.size());

		{

			Newsletter theNewsletter = alle.get(0);
			assertEquals(generatedID, theNewsletter.identifier());
			assertEquals("Test", theNewsletter.betreff());
			assertEquals("Bla blabla blablablabla bla bla\nblubb blubberdiblubb.", theNewsletter.text());
		}

		Optional<Newsletter> optNewsletter = repo.ofId(generatedID);

		assertTrue(optNewsletter.isPresent());

		newsletter = optNewsletter.get();
		assertEquals(generatedID, newsletter.identifier());
		assertEquals("Test", newsletter.betreff());
		assertEquals("Bla blabla blablablabla bla bla\nblubb blubberdiblubb.", newsletter.text());

		newsletter.withBetreff("Testbetreff");
		newsletter.withText("Nönö nö! " + newsletter.text());

		trx = entityManager.getTransaction();

		try {

			trx.begin();

			Newsletter geaenderter = repo.updateNewsletter(newsletter);

			assertEquals(generatedID, geaenderter.identifier());
			assertEquals("Testbetreff", geaenderter.betreff());
			assertEquals("Nönö nö! Bla blabla blablablabla bla bla\nblubb blubberdiblubb.", geaenderter.text());

			trx.commit();

		} catch (Exception e) {

			rollback(trx);
			fail(e.getMessage());
		}

		trx = entityManager.getTransaction();

		try {

			trx.begin();

			repo.removeNewsletter(generatedID);

			trx.commit();

		} catch (Exception e) {

			rollback(trx);
			fail(e.getMessage());
		}

		optNewsletter = repo.ofId(generatedID);

		assertTrue(optNewsletter.isEmpty());

	}

}
