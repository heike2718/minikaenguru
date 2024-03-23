// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityTransaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.newsletters.Newsletter;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.NewsletterHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;

/**
 * NewsletterHibernateRepositoryIT
 */
public class NewsletterHibernateRepositoryIT extends AbstractIntegrationTest {

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
		List<Newsletter> alle = repo.loadAll();
		int expectedAnzahl = alle.size() + 1;

		Newsletter newsletter = new Newsletter().withBetreff("Test")
			.withText("MjaApiRestClient blabla blablablabla bla bla\nblubb blubberdiblubb.");

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

		alle = repo.loadAll();

		assertEquals(expectedAnzahl, alle.size());

		{

			Newsletter theNewsletter = alle.get(0);
			assertEquals(generatedID, theNewsletter.identifier());
			assertEquals("Test", theNewsletter.betreff());
			assertEquals("MjaApiRestClient blabla blablablabla bla bla\nblubb blubberdiblubb.", theNewsletter.text());
		}

		Optional<Newsletter> optNewsletter = repo.ofId(generatedID);

		assertTrue(optNewsletter.isPresent());

		newsletter = optNewsletter.get();
		assertEquals(generatedID, newsletter.identifier());
		assertEquals("Test", newsletter.betreff());
		assertEquals("MjaApiRestClient blabla blablablabla bla bla\nblubb blubberdiblubb.", newsletter.text());

		newsletter.withBetreff("Testbetreff");
		newsletter.withText("Nönö nö! " + newsletter.text());

		trx = entityManager.getTransaction();

		try {

			trx.begin();

			Newsletter geaenderter = repo.updateNewsletter(newsletter);

			assertEquals(generatedID, geaenderter.identifier());
			assertEquals("Testbetreff", geaenderter.betreff());
			assertEquals("Nönö nö! MjaApiRestClient blabla blablablabla bla bla\nblubb blubberdiblubb.", geaenderter.text());

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
