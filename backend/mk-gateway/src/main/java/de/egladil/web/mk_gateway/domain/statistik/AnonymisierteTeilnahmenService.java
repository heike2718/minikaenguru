// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.tuple.Pair;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.AnonymisierteTeilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.TeilnahmenHibernateRepository;

/**
 * AnonymisierteTeilnahmenService
 */
@ApplicationScoped
public class AnonymisierteTeilnahmenService {

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	@Inject
	AuthorizationService authorizationService;

	public static AnonymisierteTeilnahmenService createForIntegrationTest(final EntityManager em) {

		AnonymisierteTeilnahmenService result = new AnonymisierteTeilnahmenService();
		result.teilnahmenRepository = TeilnahmenHibernateRepository.createForIntegrationTest(em);
		result.loesungszettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(em);
		result.authorizationService = AuthorizationService.createForIntegrationTest(em);
		return result;
	}

	public static AnonymisierteTeilnahmenService createForTest(final AuthorizationService authorizationService, final TeilnahmenRepository teilnahmenRepository, final LoesungszettelRepository loesungszettelRepository) {

		AnonymisierteTeilnahmenService result = new AnonymisierteTeilnahmenService();
		result.authorizationService = authorizationService;
		result.loesungszettelRepository = loesungszettelRepository;
		result.teilnahmenRepository = teilnahmenRepository;
		return result;
	}

	public List<AnonymisierteTeilnahmeAPIModel> loadAnonymisierteTeilnahmen(final String teilnahmenummer, final String userUuid) {

		this.authorizationService.checkPermissionForTeilnahmenummerAndReturnRolle(new Identifier(userUuid),
			new Identifier(teilnahmenummer), "[loadAnonymisierteTeilnahmen - " + teilnahmenummer + "]");

		List<Teilnahme> alleTeilnahmen = teilnahmenRepository.ofTeilnahmenummer(teilnahmenummer);

		List<AnonymisierteTeilnahmeAPIModel> result = new ArrayList<>();

		alleTeilnahmen.forEach(teilnahme -> {

			TeilnahmeIdentifier identifier = TeilnahmeIdentifier.createFromTeilnahme(teilnahme);

			final List<Pair<Auswertungsquelle, Integer>> quellenMitAnzahl = loesungszettelRepository
				.getAuswertungsquellenMitAnzahl(identifier);

			int anzahlOnline = 0;
			int anzahlUpload = 0;

			if (!quellenMitAnzahl.isEmpty()) {

				Optional<Pair<Auswertungsquelle, Integer>> optOnline = quellenMitAnzahl.stream()
					.filter(q -> Auswertungsquelle.ONLINE == q.getLeft()).findFirst();

				if (optOnline.isPresent()) {

					anzahlOnline = optOnline.get().getRight().intValue();
				}

				Optional<Pair<Auswertungsquelle, Integer>> optUpload = quellenMitAnzahl.stream()
					.filter(q -> Auswertungsquelle.UPLOAD == q.getLeft()).findFirst();

				if (optUpload.isPresent()) {

					anzahlUpload = optUpload.get().getRight().intValue();
				}
			}

			final int anzahlLoesungszettel = anzahlOnline + anzahlUpload;

			result.add(AnonymisierteTeilnahmeAPIModel.create(identifier).withAnzahlKinder(anzahlLoesungszettel)
				.withAnzahlLoesungszettelOnline(anzahlOnline).withAnzahlLoesungszettelUpload(anzahlUpload));

		});

		Collections.sort(result, new AnonymisierteTeilnahmenDescendingComparator());

		return result;
	}

}
