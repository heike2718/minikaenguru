// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.veranstalter.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * SchulenOverviewServiceTest
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

	@Test
	void should_ermittleSchuleMitKuerzelFuerLehrerThrowNotFound_when_FalschesSchulkuerzel() {

		// Arrange
		Identifier lehrerId = new Identifier(UUID_LEHRER_2);

		// Act + Assert
		try {

			service.ermittleSchuleMitKuerzelFuerLehrer(lehrerId, SCHULKUERZEL_2);
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			assertNotNull(service.getSecurityIncidentRegistered());
		}

	}

	@Test
	void should_ermittleSchuleMitKuerzelFuerLehrerWork() {

		// Arrange
		Identifier lehrerId = new Identifier(UUID_LEHRER_2);

		// Act
		SchuleAPIModel schule = service.ermittleSchuleMitKuerzelFuerLehrer(lehrerId, SCHULKUERZEL_1);

		// Assert
		assertEquals(SCHULKUERZEL_1, schule.kuerzel());
		assertTrue(schule.aktuellAngemeldet());
		assertNull(schule.land());
		assertNull(schule.ort());
		assertNull(schule.name());
		assertNull(schule.details());

	}

}
