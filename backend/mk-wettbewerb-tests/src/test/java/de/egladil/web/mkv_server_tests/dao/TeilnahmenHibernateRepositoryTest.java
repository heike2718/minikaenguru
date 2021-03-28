// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.dao;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.TeilnahmenHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIT;

/**
 * TeilnahmenHibernateRepositoryTest
 */
public class TeilnahmenHibernateRepositoryTest extends AbstractIT {

	TeilnahmenRepository teilnahmenRepository;

	@BeforeEach
	protected void setUp() {

		super.setUp();
		teilnahmenRepository = TeilnahmenHibernateRepository.createForIntegrationTest(entityManagerWettbewerbDB);

	}

	@Test
	void testSelect() {

		// Act
		List<Teilnahme> teilnahmen = teilnahmenRepository.ofTeilnahmenummer("AUFNUR0WEG");

		// Assert
		assertTrue(teilnahmen.size() >= 5);
	}

	@Test
	void testLoadAllForWettbewerb() {

		// Arrange
		WettbewerbID wettbewerbID = new WettbewerbID(2020);

		// Act
		List<Teilnahme> teilnahmen = teilnahmenRepository.loadAllForWettbewerb(wettbewerbID);

		// Assert
		assertEquals(30, teilnahmen.size());

	}
}
