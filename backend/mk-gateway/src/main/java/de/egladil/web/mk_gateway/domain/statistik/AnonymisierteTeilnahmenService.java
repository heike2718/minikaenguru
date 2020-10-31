// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.AnonymisierteTeilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;

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

	public static AnonymisierteTeilnahmenService createForTest(final AuthorizationService authorizationService, final TeilnahmenRepository teilnahmenRepository, final LoesungszettelRepository loesungszettelRepository) {

		AnonymisierteTeilnahmenService result = new AnonymisierteTeilnahmenService();
		result.authorizationService = authorizationService;
		result.loesungszettelRepository = loesungszettelRepository;
		result.teilnahmenRepository = teilnahmenRepository;
		return result;
	}

	public List<AnonymisierteTeilnahmeAPIModel> loadAnonymisierteTeilnahmen(final String teilnahmenummer, final String userUuid) {

		this.authorizationService.checkPermissionForTeilnahmenummer(new Identifier(userUuid),
			new Identifier(teilnahmenummer), "[loadAnonymisierteTeilnahmen - " + teilnahmenummer + "]");

		List<Teilnahme> alleTeilnahmen = teilnahmenRepository.ofTeilnahmenummer(teilnahmenummer);

		List<AnonymisierteTeilnahmeAPIModel> result = new ArrayList<>();

		alleTeilnahmen.forEach(teilnahme -> {

			TeilnahmeIdentifier identifier = TeilnahmeIdentifier.createFromTeilnahme(teilnahme);
			final int anzahlLoesungszettel = loesungszettelRepository.anzahlLoesungszettel(identifier);

			result.add(AnonymisierteTeilnahmeAPIModel.create(identifier).withAnzahlKinder(anzahlLoesungszettel));

		});

		Collections.sort(result, new AnonymisierteTeilnahmenDescendingComparator());

		return result;
	}

}
