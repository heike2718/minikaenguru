// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.statistik.impl.ErfassungLoesungszettelInfoServiceImpl;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.AnonymisierteTeilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.TeilnahmenHibernateRepository;

/**
 * AnonymisierteTeilnahmenService
 */
@ApplicationScoped
public class AnonymisierteTeilnahmenService {

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	ErfassungLoesungszettelInfoService erfassungLoesungszettelInfoService;

	@Inject
	AuthorizationService authorizationService;

	public static AnonymisierteTeilnahmenService createForIntegrationTest(final EntityManager em) {

		AnonymisierteTeilnahmenService result = new AnonymisierteTeilnahmenService();
		result.teilnahmenRepository = TeilnahmenHibernateRepository.createForIntegrationTest(em);
		result.authorizationService = AuthorizationService.createForIntegrationTest(em);
		result.erfassungLoesungszettelInfoService = ErfassungLoesungszettelInfoServiceImpl.createForIntegrationTests(em);
		return result;
	}

	public List<AnonymisierteTeilnahmeAPIModel> loadAnonymisierteTeilnahmen(final String teilnahmenummer, final String userUuid) {

		this.authorizationService.checkPermissionForTeilnahmenummerAndReturnRolle(new Identifier(userUuid),
			new Identifier(teilnahmenummer), "[loadAnonymisierteTeilnahmen - " + teilnahmenummer + "]");

		List<Teilnahme> alleTeilnahmen = teilnahmenRepository.ofTeilnahmenummer(teilnahmenummer);

		List<AnonymisierteTeilnahmeAPIModel> result = new ArrayList<>();

		alleTeilnahmen.forEach(teilnahme -> {

			TeilnahmeIdentifier identifier = TeilnahmeIdentifier.createFromTeilnahme(teilnahme);

			Map<Auswertungsquelle, Long> auswertungsquellenMap = erfassungLoesungszettelInfoService
				.ermittleLoesungszettelMitAuswertungsquellenForTeilnahme(teilnahme.teilnahmeIdentifier());

			long anzahlOnline = auswertungsquellenMap.get(Auswertungsquelle.ONLINE);
			long anzahlUpload = auswertungsquellenMap.get(Auswertungsquelle.UPLOAD);

			final long anzahlLoesungszettel = anzahlOnline + anzahlUpload;

			result.add(AnonymisierteTeilnahmeAPIModel.create(identifier).withAnzahlKinder(anzahlLoesungszettel)
				.withAnzahlLoesungszettelOnline(anzahlOnline).withAnzahlLoesungszettelUpload(anzahlUpload));

		});

		Collections.sort(result, new AnonymisierteTeilnahmenDescendingComparator());

		return result;
	}

}
