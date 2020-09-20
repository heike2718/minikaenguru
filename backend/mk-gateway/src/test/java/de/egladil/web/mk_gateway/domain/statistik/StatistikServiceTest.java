// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.teilnahmen.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.auswertungen.StatistikTestUtils;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * StatistikServiceTest
 */
public class StatistikServiceTest {

	@Test
	void should_erstelleStatistikFuerEinzelteilnahme_call_AuthService() {

		// Arrange
		String teilnahmenummer = "ashichw";
		String userUuid = "ucgwug";
		Identifier userIdentifier = new Identifier(userUuid);
		Identifier teilnahmeID = new Identifier(teilnahmenummer);

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE.toString())
			.withTeilnahmenummer(teilnahmenummer).withWettbewerbID(new WettbewerbID(2016));

		AuthorizationService authService = Mockito.mock(AuthorizationService.class);
		Mockito.when(authService.checkPermissionForTeilnahmenummer(userIdentifier, teilnahmeID))
			.thenThrow(new AccessDeniedException());

		StatistikService statistikService = StatistikService.createForTest(authService);

		// Act
		try {

			statistikService.erstelleStatistikPDFEinzelteilnahme(teilnahmeIdentifier, userUuid);
			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			// nix
		}

	}

	@Test
	void should_sortByKlassenstufe_work() throws Exception {

		// Arrange
		List<Loesungszettel> alleLoesungszettel = StatistikTestUtils.loadTheLoesungszettel();
		assertEquals(12, alleLoesungszettel.size());

		StatistikService statistikService = new StatistikService();

		// Act
		Map<Klassenstufe, List<Loesungszettel>> klassenstufeLoesungszettelMap = statistikService
			.sortByKlassenstufe(alleLoesungszettel);

		// Assert
		assertEquals(3, klassenstufeLoesungszettelMap.size());

		List<Loesungszettel> zettelIKIDS = klassenstufeLoesungszettelMap.get(Klassenstufe.IKID);
		assertEquals(3, zettelIKIDS.size());

		List<Loesungszettel> zettelEINS = klassenstufeLoesungszettelMap.get(Klassenstufe.EINS);
		assertEquals(5, zettelEINS.size());

		List<Loesungszettel> zettelZWEI = klassenstufeLoesungszettelMap.get(Klassenstufe.ZWEI);
		assertEquals(4, zettelZWEI.size());

	}

}
