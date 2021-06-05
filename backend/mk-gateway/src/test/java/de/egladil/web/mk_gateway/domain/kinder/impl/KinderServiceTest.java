// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindRequestData;
import de.egladil.web.mk_gateway.domain.kinder.impl.KinderServiceImpl;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;

/**
 * KinderServiceTest
 */
public class KinderServiceTest extends AbstractDomainServiceTest {

	private KinderServiceImpl service;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();

		AuthorizationService authService = AuthorizationService.createForTest(getVeranstalterRepository(), getUserRepository());
		LoesungszettelService loesungszettelService = LoesungszettelService.createForTest(authService, getWettbewerbService(),
			getKinderRepository(),
			getLoesungszettelRepository());

		service = KinderServiceImpl.createForTest(authService, getKinderRepository(), getTeilnahmenRepository(),
			getVeranstalterRepository(), getWettbewerbService(), loesungszettelService, getKlassenRepository());
	}

	@Test
	void should_pruefeDublettePrivat_call_TeilnahmenRepository() {

		// Arrange
		KindRequestData data = createTestData();

		// Act
		boolean result = service.pruefeDublette(data, UUID_PRIVAT);

		// Assert
		assertFalse(result);
	}

	@Test
	void should_pruefeDublettePrivatThrowNotFound_when_VeranstalterNichtAngemeldet() {

		// Arrange
		KindRequestData data = createTestData();

		// Act
		try {

			service.pruefeDublette(data, UUID_PRIVAT_NICHT_ANGEMELDET);
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			System.err.println(e.getMessage());

			assertEquals(
				"KinderServiceImpl.pruefeDublette(...): Veranstalter mit UUID=UUID_PRIVAT_NICHT_ANGEMELDET ist nicht zum aktuellen Wettbewerb (2020) angemeldet",
				e.getMessage());
		}
	}

	@Test
	void should_kindAnlegen_triggerEvent() {

		// Arrange
		KindRequestData data = createTestData();

		// Act
		service.kindAnlegen(data, UUID_PRIVAT);

		// Assert
		assertNotNull(service.getKindCreated());

	}

	@Test
	void should_kindAnlegenThrowNotFound_when_VeranstalterNichtAngemeldet() {

		// Arrange
		KindRequestData data = createTestData();

		// Act
		try {

			service.kindAnlegen(data, UUID_PRIVAT_NICHT_ANGEMELDET);
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			System.err.println(e.getMessage());

			assertNull(service.getKindCreated());
			assertEquals(
				"KinderServiceImpl.kindAnlegen(...): Veranstalter mit UUID=UUID_PRIVAT_NICHT_ANGEMELDET ist nicht zum aktuellen Wettbewerb (2020) angemeldet",
				e.getMessage());
		}
	}

	@Test
	void should_koennteDubletteSein_work_whenGleicheUuid() {

		// Arrange
		Kind kind = new Kind(new Identifier("UUID-UUID"))
			.withKlassenstufe(Klassenstufe.EINS)
			.withNachname("Paschulke")
			.withVorname("Heinz")
			.withSprache(Sprache.de);

		List<Kind> kinder = Arrays.asList(new Kind[] { kind });

		// Act
		boolean koennte = service.koennteDubletteSein(kind, kinder);

		// Assert
		assertFalse(koennte);

	}

	private KindRequestData createTestData() {

		KindEditorModel kind = new KindEditorModel(Klassenstufe.EINS, Sprache.de)
			.withNachname("Paschulke")
			.withVorname("Heinz");

		return new KindRequestData().withKind(kind);
	}

}
