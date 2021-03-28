// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.domain.wettbewerb;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mkv_server_tests.AbstractIT;

/**
 * WettbewerbServiceTest
 */
public class WettbewerbServiceTest extends AbstractIT {

	private WettbewerbService wettbewerbService;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		wettbewerbService = WettbewerbService.createForIntegrationTest(entityManagerWettbewerbDB);
	}

	@Test
	void should_aktuellerWettbewerbImAnmeldemodusReturn2020() {

		// Act
		Wettbewerb wettbewerb = wettbewerbService.aktuellerWettbewerbImAnmeldemodus();

		// Assert
		assertEquals(Integer.valueOf(2020), wettbewerb.id().jahr());
	}

	@Test
	void should_aktuellerWettbewerbReturn2020() {

		// Act
		Optional<Wettbewerb> wettbewerb = wettbewerbService.aktuellerWettbewerb();

		// Assert
		assertEquals(Integer.valueOf(2020), wettbewerb.get().id().jahr());
	}

}
