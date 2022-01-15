// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Auspraegung;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KinderHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;

/**
 * KinderHibernateRepositoryIT
 */
public class KinderHibernateRepositoryIT extends AbstractIntegrationTest {

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

	@Nested
	class StatistikTests {

		@Test
		void should_countAuspraegungenByColumnNameReurnTheAuspraegungen_when_klassenstufe() {

			// Act
			List<Auspraegung> auspraegungen = repository.countAuspraegungenByColumnName("KLASSENSTUFE");

			// Assert
			assertEquals(3, auspraegungen.size());

			for (Auspraegung auspraegung : auspraegungen) {

				System.out.println(auspraegung.toString());
			}

		}

		@Test
		void should_countAuspraegungenByColumnNameReurnTheAuspraegungen_when_teilnahmeart() {

			// Act
			List<Auspraegung> auspraegungen = repository.countAuspraegungenByColumnName("TEILNAHMEART");

			// Assert
			assertEquals(2, auspraegungen.size());

			for (Auspraegung auspraegung : auspraegungen) {

				System.out.println(auspraegung.toString());
			}

		}

		@Test
		void should_countAuspraegungenByColumnNameReurnTheAuspraegungen_when_sprache() {

			// Act
			List<Auspraegung> auspraegungen = repository.countAuspraegungenByColumnName("SPRACHE");

			// Assert
			assertEquals(2, auspraegungen.size());

			for (Auspraegung auspraegung : auspraegungen) {

				System.out.println(auspraegung.toString());
			}

		}

		@Test
		void should_countAuspraegungenByColumnNameReurnTheAuspraegungen_when_erfassungsart() {

			// Act
			List<Auspraegung> auspraegungen = repository.countAuspraegungenByColumnName("IMPORTIERT");

			// Assert
			assertEquals(1, auspraegungen.size());

			for (Auspraegung auspraegung : auspraegungen) {

				System.out.println(auspraegung.toString());
			}

		}

	}

}
