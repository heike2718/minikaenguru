// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.auswertungen.LoesungszettelService;
import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.PrivatkindRequestData;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Privatteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.veranstalter.PrivatveranstalterService;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * PrivatkinderServiceTest
 */
public class PrivatkinderServiceTest extends AbstractDomainServiceTest {

	private PrivatkinderService service;

	private KinderRepository kinderRepository;

	private PrivatveranstalterService privatveranstalterService;

	private WettbewerbService wettbewerbService;

	private AuthorizationService authService;

	private LoesungszettelService loesungszettelService;

	class TestKinderRepository implements KinderRepository {

		private final Kind expectedKind;

		TestKinderRepository(final Kind expectedKind) {

			this.expectedKind = expectedKind;
		}

		@Override
		public Kind addKind(final Kind kind) {

			return expectedKind;
		}

		@Override
		public List<Kind> findKinderWithTeilnahme(final Teilnahme teilnahme) {

			return Arrays.asList(new Kind[] { expectedKind });
		}

		@Override
		public Optional<Kind> findKindWithIdentifier(final Identifier identifier, final WettbewerbID wettbewerbID) {

			return Optional.of(expectedKind);
		}

		@Override
		public boolean changeKind(final Kind kind) {

			return true;
		}

		@Override
		public boolean removeKind(final Kind kind) {

			return true;
		}

	}

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();

		wettbewerbService = Mockito.mock(WettbewerbService.class);
		Wettbewerb aktuellerWettbewerb = new Wettbewerb(new WettbewerbID(WETTBEWERBSJAHR_AKTUELL));

		Mockito.when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));

		kinderRepository = Mockito.mock(KinderRepository.class);
		privatveranstalterService = PrivatveranstalterService.createForTest(getVeranstalterRepository(),
			getZugangUnterlagenService(), getWettbewerbService(), getTeilnahmenRepository(), getPrivatteilnameKuerzelService());

		authService = Mockito.mock(AuthorizationService.class);
		loesungszettelService = Mockito.mock(LoesungszettelService.class);

		service = PrivatkinderService.createForTest(authService, kinderRepository, getTeilnahmenRepository(),
			privatveranstalterService, wettbewerbService, loesungszettelService);
	}

	@Test
	void should_pruefeDublettePrivat_call_TeilnahmenRepository() {

		// Arrange
		PrivatkindRequestData data = createTestData();

		Teilnahme teilnahme = new Privatteilnahme(new WettbewerbID(WETTBEWERBSJAHR_AKTUELL),
			new Identifier(TEILNAHMENUMMER_PRIVAT));

		Mockito.when(kinderRepository.findKinderWithTeilnahme(teilnahme))
			.thenReturn(new ArrayList<>());

		// Act
		boolean result = service.pruefeDublettePrivat(data, UUID_PRIVAT);

		// Assert
		assertFalse(result);
		Mockito.verify(kinderRepository, Mockito.times(1)).findKinderWithTeilnahme(teilnahme);
	}

	@Test
	void should_pruefeDublettePrivatThrowNotFound_when_VeranstalterNichtAngemeldet() {

		// Arrange
		PrivatkindRequestData data = createTestData();

		Mockito.when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(new Wettbewerb(new WettbewerbID(2018))));

		// Act
		try {

			service.pruefeDublettePrivat(data, UUID_PRIVAT);
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			System.err.println(e.getMessage());

			assertEquals(
				"PrivatkinderService.pruefeDublettePrivat(...): Privatveranstalter mit UUID=UUID_PRIVAT ist nicht zum aktuellen Wettbewerb (2018) angemeldet",
				e.getMessage());
		}
	}

	@Test
	void should_privatkindAnlegen_triggerEvent() {

		// Arrange
		PrivatkindRequestData data = createTestData();
		Kind gespeichertesKind = new Kind(new Identifier("UUID-UUID"))
			.withKlassenstufe(Klassenstufe.EINS)
			.withNachname("Paschulke")
			.withVorname("Heinz")
			.withSprache(Sprache.de);

		Teilnahme teilnahme = new Privatteilnahme(new WettbewerbID(WETTBEWERBSJAHR_AKTUELL),
			new Identifier(TEILNAHMENUMMER_PRIVAT));

		Mockito.when(kinderRepository.findKinderWithTeilnahme(teilnahme))
			.thenReturn(new ArrayList<>());

		PrivatkinderService theService = PrivatkinderService.createForTest(authService, new TestKinderRepository(gespeichertesKind),
			getTeilnahmenRepository(), privatveranstalterService, wettbewerbService, loesungszettelService);

		// Act
		theService.privatkindAnlegen(data, UUID_PRIVAT);

		// Assert
		assertNotNull(theService.getKindCreated());

	}

	@Test
	void should_privatkindAnlegenThrowNotFound_when_VeranstalterNichtAngemeldet() {

		// Arrange
		PrivatkindRequestData data = createTestData();

		Mockito.when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(new Wettbewerb(new WettbewerbID(2018))));

		PrivatkinderService theService = PrivatkinderService.createForTest(authService, kinderRepository,
			getTeilnahmenRepository(), privatveranstalterService, wettbewerbService, loesungszettelService);

		// Act
		try {

			theService.privatkindAnlegen(data, UUID_PRIVAT);
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			System.err.println(e.getMessage());

			assertNull(theService.getKindCreated());
			assertEquals(
				"PrivatkinderService.privatkindAnlegen(...): Privatveranstalter mit UUID=UUID_PRIVAT ist nicht zum aktuellen Wettbewerb (2018) angemeldet",
				e.getMessage());
		}
	}

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

	private PrivatkindRequestData createTestData() {

		KindEditorModel kind = new KindEditorModel(Klassenstufe.EINS, Sprache.de)
			.withNachname("Paschulke")
			.withVorname("Heinz");

		return new PrivatkindRequestData().withKind(kind);
	}

}
