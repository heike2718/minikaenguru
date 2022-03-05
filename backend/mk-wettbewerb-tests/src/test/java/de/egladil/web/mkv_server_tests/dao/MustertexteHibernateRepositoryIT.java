// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mustertexte.Mustertext;
import de.egladil.web.mk_gateway.domain.mustertexte.Mustertextkategorie;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterMustertext;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.MustertexteHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;
import de.egladil.web.mkv_server_tests.TestAssertsDao;

/**
 * MustertexteHibernateRepositoryIT
 */
public class MustertexteHibernateRepositoryIT extends AbstractIntegrationTest {

	MustertexteHibernateRepository repository;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		repository = MustertexteHibernateRepository.createForIntegrationTests(entityManager);
	}

	@Test
	void should_addUpdateLoadDelete() {

		// =============================
		// Add
		// =============================

		// Arrange
		Mustertext mustertext = new Mustertext().withKategorie(Mustertextkategorie.NEWSLETTER)
			.withName("Freischaltung Unterlagen Lehrer")
			.withText("Die Unterlagen für den Wettbewerb 2022 wurden zum Download freigeschaltet");

		// Act add
		EntityTransaction trx = startTransaction();
		Mustertext result = repository.addOrUpdate(mustertext);
		commit(trx);

		// Assert add
		assertNotNull(result);
		assertEquals(mustertext.getKategorie(), result.getKategorie());
		assertEquals(mustertext.getName(), result.getName());
		assertEquals(mustertext.getText(), result.getText());

		PersistenterMustertext ausDB = new TestAssertsDao(entityManager).findByIdentifier(PersistenterMustertext.class,
			result.getIdentifier().identifier());

		assertNotNull(ausDB);

		entityManager.detach(ausDB);

		assertEquals(result.getIdentifier().toString(), ausDB.getUuid());
		assertEquals(mustertext.getKategorie(), ausDB.getKategorie());
		assertEquals(mustertext.getName(), ausDB.getName());
		assertEquals(mustertext.getText(), ausDB.getText());

		// =============================
		// Update
		// =============================
		String expectedText = "geänderter Text";

		mustertext = new Mustertext(new Identifier(ausDB.getUuid())).withKategorie(Mustertextkategorie.NEWSLETTER)
			.withName(mustertext.getName()).withText("geänderter Text");

		// Act update
		trx = startTransaction();
		result = repository.addOrUpdate(mustertext);
		commit(trx);

		// Assert update
		assertNotNull(result);
		assertEquals(mustertext.getKategorie(), result.getKategorie());
		assertEquals(mustertext.getName(), result.getName());
		assertEquals(expectedText, result.getText());

		ausDB = new TestAssertsDao(entityManager).findByIdentifier(PersistenterMustertext.class,
			result.getIdentifier().identifier());

		assertNotNull(ausDB);

		entityManager.detach(ausDB);

		assertEquals(mustertext.getKategorie(), ausDB.getKategorie());
		assertEquals(mustertext.getName(), ausDB.getName());
		assertEquals(mustertext.getText(), ausDB.getText());

		// =============================
		// Insert fails
		// =============================

		mustertext = new Mustertext().withName(result.getName()).withKategorie(Mustertextkategorie.NEWSLETTER)
			.withText("ein neuer Text für diesen Test");

		// Act insert fails

		trx = startTransaction();

		try {

			repository.addOrUpdate(mustertext);
			commit(trx);

			fail("keine RollbackException");
		} catch (RollbackException e) {

			Throwable cause = e.getCause().getCause();
			assertTrue(cause instanceof ConstraintViolationException);

			ConstraintViolationException exception = (ConstraintViolationException) cause;
			assertEquals("uk_mustertexte_1", exception.getConstraintName());
		}

		// =============================
		// Load
		// =============================

		// Act load
		List<Mustertext> trefferliste = repository.loadMustertexteByKategorie(Mustertextkategorie.NEWSLETTER);

		// Assert load
		assertEquals(1, trefferliste.size());
		mustertext = trefferliste.get(0);

		assertEquals(ausDB.getUuid(), mustertext.getIdentifier().identifier());
		assertEquals(Mustertextkategorie.NEWSLETTER, mustertext.getKategorie());
		assertEquals(ausDB.getName(), mustertext.getName());
		assertNull(mustertext.getText());

		// =============================
		// Delete
		// =============================

		// Act delete
		trx = startTransaction();
		boolean deleted = repository.deleteMustertext(mustertext.getIdentifier());
		commit(trx);

		// Assert delete
		assertTrue(deleted);
		trefferliste = repository.loadMustertexteByKategorie(Mustertextkategorie.NEWSLETTER);
		assertTrue(trefferliste.isEmpty());

	}

}
