// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.domain.kinder;

import org.junit.jupiter.api.BeforeEach;

import de.egladil.web.mk_gateway.domain.kinder.KinderService;
import de.egladil.web.mk_gateway.domain.kinder.KlassenService;
import de.egladil.web.mkv_server_tests.AbstractIT;

/**
 * KlassenUndKinderServiceCombinedTest
 */
public class KlassenUndKinderServiceCombinedTest extends AbstractIT {

	private KlassenService klassenService;

	private KinderService kinderService;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		klassenService = KlassenService.createForIntegrationTest(entityManager);
		kinderService = KinderService.createForIntegrationTest(entityManager);
	}

}
