// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.kinder.api.KindAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.PrivatkindRequestData;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * PrivatkinderServiceTest
 */
public class PrivatkinderServiceTest extends AbstractDomainServiceTest {

	private static final WettbewerbID WETTBEWERB_ID = new WettbewerbID(2018);

	private PrivatkinderService service;

	private KinderRepository kinderRepository;

	private AuthorizationService authService;

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
		public List<Kind> findKinderWithTeilnahme(final TeilnahmeIdentifier teilnahmeIdentifier, final WettbewerbID wettbewerbID) {

			return Arrays.asList(new Kind[] { expectedKind });
		}

	}

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();

		kinderRepository = Mockito.mock(KinderRepository.class);
		authService = Mockito.mock(AuthorizationService.class);

		service = PrivatkinderService.createForTest(kinderRepository, getTeilnahmenRepository(), authService);
	}

	@Test
	void should_pruefeDublettePrivat_call_TeilnahmenRepository() {

		// Arrange
		PrivatkindRequestData data = createTestData();

		String veranstalterUuid = "gduqgugug";
		Identifier userIdentifier = new Identifier(veranstalterUuid);
		Identifier teilnahmeID = new Identifier("AAAAAAAAAA");
		Mockito.when(authService.checkPermissionForTeilnahmenummer(userIdentifier, teilnahmeID))
			.thenReturn(Boolean.TRUE);

		Mockito.when(kinderRepository.findKinderWithTeilnahme(data.teilnahmeIdentifier(), WETTBEWERB_ID))
			.thenReturn(new ArrayList<>());

		// Act
		boolean result = service.pruefeDublettePrivat(data, veranstalterUuid);

		// Assert
		assertFalse(result);
		Mockito.verify(authService, Mockito.times(1)).checkPermissionForTeilnahmenummer(userIdentifier, teilnahmeID);
		Mockito.verify(kinderRepository, Mockito.times(1)).findKinderWithTeilnahme(data.teilnahmeIdentifier(),
			WETTBEWERB_ID);
	}

	@Test
	void should_privatkindAnlegen_callAuthService() {

		// Arrange
		PrivatkindRequestData data = createTestData();
		String veranstalterUuid = "gduqgugug";
		Identifier userIdentifier = new Identifier(veranstalterUuid);
		Identifier teilnahmeID = new Identifier("AAAAAAAAAA");
		Mockito.when(authService.checkPermissionForTeilnahmenummer(userIdentifier, teilnahmeID))
			.thenThrow(new AccessDeniedException("Tja"));

		// Act + Assert
		try {

			service.privatkindAnlegen(data, veranstalterUuid);
			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			assertEquals("Tja", e.getMessage());

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

		String veranstalterUuid = "ggggiiozioio";

		Mockito.when(authService.checkPermissionForTeilnahmenummer(new Identifier(veranstalterUuid),
			new Identifier(data.teilnahmeIdentifier().teilnahmenummer()))).thenReturn(Boolean.TRUE);

		PrivatkinderService theService = PrivatkinderService.createForTest(new TestKinderRepository(gespeichertesKind),
			getTeilnahmenRepository(), authService);

		// Act
		theService.privatkindAnlegen(data, veranstalterUuid);

		// Assert
		assertNotNull(theService.getKindCreated());

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

		KindAPIModel kind = KindAPIModel.create(Klassenstufe.EINS, Sprache.de)
			.withNachname("Paschulke")
			.withVorname("Heinz");

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.PRIVAT)
			.withTeilnahmenummer("AAAAAAAAAA").withWettbewerbID(new WettbewerbID(2020));

		return new PrivatkindRequestData().withKind(kind).withTeilnahmeIdentifier(teilnahmeIdentifier);
	}

}
