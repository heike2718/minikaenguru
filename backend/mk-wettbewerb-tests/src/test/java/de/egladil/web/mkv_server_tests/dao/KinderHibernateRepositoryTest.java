// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.dao;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KinderHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIT;

/**
 * KinderHibernateRepositoryTest
 */
public class KinderHibernateRepositoryTest extends AbstractIT {

	private KinderHibernateRepository repository;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		repository = KinderHibernateRepository.createForIntegrationTest(entityManager);
	}

	@Test
	void should_addKind_work() {

		// Arrange
		TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifier = new TeilnahmeIdentifierAktuellerWettbewerb("5SBB0D8PUR",
			Teilnahmeart.PRIVAT);

		Kind kind = new Kind().withKlassenstufe(Klassenstufe.ZWEI).withNachname("Meier").withSprache(Sprache.de)
			.withVorname("Emma").withTeilnahmeIdentifier(teilnahmeIdentifier);

		EntityTransaction trx = entityManager.getTransaction();

		try {

			trx.begin();

			Kind gespeichertes = repository.addKind(kind);

			trx.commit();

			assertNotNull(gespeichertes.identifier());
		} catch (PersistenceException e) {

			trx.rollback();
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
