// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.statistik.admin.AdminStatistikAuspraegung;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * KinderHibernateRepositoryTest
 */
@QuarkusTest
public class KinderHibernateRepositoryTest {

	@Inject
	KinderHibernateRepository repository;

	@Test
	void should_countAuspraegungenByColumnNameReurnTheAuspraegungen_when_klassenstufe() {

		// Act
		List<AdminStatistikAuspraegung> auspraegungen = repository.countAuspraegungenByColumnName("KLASSENSTUFE");

		// Assert
		assertEquals(3, auspraegungen.size());

		for (AdminStatistikAuspraegung auspraegung : auspraegungen) {

			System.out.println(auspraegung.toString());
		}

	}

	@Test
	void should_countAuspraegungenByColumnNameReurnTheAuspraegungen_when_teilnahmeart() {

		// Act
		List<AdminStatistikAuspraegung> auspraegungen = repository.countAuspraegungenByColumnName("TEILNAHMEART");

		// Assert
		assertEquals(2, auspraegungen.size());

		for (AdminStatistikAuspraegung auspraegung : auspraegungen) {

			System.out.println(auspraegung.toString());
		}

	}

	@Test
	void should_countAuspraegungenByColumnNameReurnTheAuspraegungen_when_sprache() {

		// Act
		List<AdminStatistikAuspraegung> auspraegungen = repository.countAuspraegungenByColumnName("SPRACHE");

		// Assert
		assertEquals(2, auspraegungen.size());

		for (AdminStatistikAuspraegung auspraegung : auspraegungen) {

			System.out.println(auspraegung.toString());
		}

	}

	@Test
	void should_countAuspraegungenByColumnNameReurnTheAuspraegungen_when_erfassungsart() {

		// Act
		List<AdminStatistikAuspraegung> auspraegungen = repository.countAuspraegungenByColumnName("IMPORTIERT");

		// Assert
		assertFalse(auspraegungen.isEmpty());

		for (AdminStatistikAuspraegung auspraegung : auspraegungen) {

			System.out.println(auspraegung.toString());
		}

	}

}
