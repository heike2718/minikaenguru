// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * SchulenAnmeldeinfoServiceWithInMemoryDatabaseTest
 */
public class SchulenOverviewServiceTest extends AbstractDomainServiceTest {

	private SchulenOverviewService service;

	@BeforeEach
	@Override
	protected void setUp() {

		super.setUp();
		WettbewerbService wettbewerbService = WettbewerbService.createForTest(getMockitoBasedWettbewerbRepository());
		AktuelleTeilnahmeService aktuelleTeilnahmeService = AktuelleTeilnahmeService.createForTest(getTeilnahmenRepository(),
			wettbewerbService, getVeranstalterRepository());

		service = SchulenOverviewService.createForTest(getVeranstalterRepository(), aktuelleTeilnahmeService);
	}

	@Test
	void should_ermittleAnmeldedatenFuerSchulenReturnSchulen_when_zweiSchulen() {

		// Arrange
		Identifier identifier = new Identifier(UUID_LEHRER_1);

		// Act
		List<SchuleAPIModel> schulen = service.ermittleAnmeldedatenFuerSchulen(identifier);

		// Assert
		assertEquals(2, schulen.size());
		assertNull(service.getSecurityIncidentRegistered());

		{

			SchuleAPIModel schule = schulen.get(0);
			assertEquals(SCHULKUERZEL_1, schule.kuerzel());
			assertTrue(schule.aktuellAngemeldet());
			assertNull(schule.land());
			assertNull(schule.ort());
			assertNull(schule.name());
			assertNull(schule.details());
		}

		{

			SchuleAPIModel schule = schulen.get(1);
			assertEquals(SCHULKUERZEL_2, schule.kuerzel());
			assertFalse(schule.aktuellAngemeldet());
			assertNull(schule.land());
			assertNull(schule.ort());
			assertNull(schule.name());
			assertNull(schule.details());
		}

	}

	@Test
	void should_ermittleAnmeldedatenFuerSchulenReturnSchulen_when_eineSchulen() {

		// Arrange
		Identifier identifier = new Identifier(UUID_LEHRER_2);

		// Act
		List<SchuleAPIModel> schulen = service.ermittleAnmeldedatenFuerSchulen(identifier);

		// Assert
		assertEquals(1, schulen.size());
		assertNull(service.getSecurityIncidentRegistered());

		{

			SchuleAPIModel schule = schulen.get(0);
			assertEquals(SCHULKUERZEL_1, schule.kuerzel());
			assertTrue(schule.aktuellAngemeldet());
			assertNull(schule.land());
			assertNull(schule.ort());
			assertNull(schule.name());
			assertNull(schule.details());
		}

	}

	@Test
	void should_ermittleAnmeldedatenFuerSchulen_returnEmptyArray_when_LehrerUnbekannt() {

		// Act
		List<SchuleAPIModel> schulen = service.ermittleAnmeldedatenFuerSchulen(new Identifier("lajsodzqowzo"));

		// Assert
		assertEquals(0, schulen.size());
		assertNotNull(service.getSecurityIncidentRegistered());
	}
}
