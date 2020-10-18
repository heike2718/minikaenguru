// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.KlassenstufeAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.SpracheAPIModel;
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

	private PrivatkinderService service;

	private KinderRepository kinderRepository;

	private AuthorizationService authService;

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

		Mockito.when(kinderRepository.findKinderWithTeilnahme(data.teilnahmeIdentifier())).thenReturn(new ArrayList<>());

		// Act
		boolean result = service.pruefeDublettePrivat(data, veranstalterUuid);

		// Assert
		assertFalse(result);
		Mockito.verify(authService, Mockito.times(1)).checkPermissionForTeilnahmenummer(userIdentifier, teilnahmeID);
		Mockito.verify(kinderRepository, Mockito.times(1)).findKinderWithTeilnahme(data.teilnahmeIdentifier());
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

	private PrivatkindRequestData createTestData() {

		KindAPIModel kind = new KindAPIModel()
			.withKlassenstufe(KlassenstufeAPIModel.create(Klassenstufe.EINS))
			.withNachname("Paschulke")
			.withSprache(SpracheAPIModel.create(Sprache.de))
			.withVorname("Heinz");

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.PRIVAT)
			.withTeilnahmenummer("AAAAAAAAAA").withWettbewerbID(new WettbewerbID(2020));

		return new PrivatkindRequestData().withKind(kind).withTeilnahmeIdentifier(teilnahmeIdentifier);
	}
}
