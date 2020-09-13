// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.teilnahmen.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * StatistikService
 */
@ApplicationScoped
public class StatistikService {

	@Inject
	AuthorizationService authorizationService;

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	static StatistikService createForTest(final AuthorizationService authService) {

		StatistikService result = new StatistikService();
		result.authorizationService = authService;
		return result;
	}

	/**
	 * Ertsellt ein PDF-Dokument, das die Auswertungsdaten der gegebenen Einzelteilnahme enthält, sofern der USER diese sehen darf.
	 *
	 * @param  teilnahmeIdentifier
	 * @param  userUuid
	 * @return                     DownloadFata
	 */
	public DownloadData erstelleStatistikFuerEinzelteilnahme(final TeilnahmeIdentifier teilnahmeIdentifier, final String userUuid) {

		authorizationService.checkPermissionForTeilnahmenummer(new Identifier(userUuid),
			new Identifier(teilnahmeIdentifier.teilnahmenummer()), true);

		List<Loesungszettel> alleLoesungszettel = loesungszettelRepository.loadAll(teilnahmeIdentifier);

		final Map<Klassenstufe, List<Loesungszettel>> klassenstufenMap = sortByKlassenstufe(alleLoesungszettel);

		return null;
	}

	final Map<Klassenstufe, List<Loesungszettel>> sortByKlassenstufe(final List<Loesungszettel> alleLoesungszettel) {

		final Map<Klassenstufe, List<Loesungszettel>> klassenstufenMap = new HashMap<>();

		for (Loesungszettel loesungszettel : alleLoesungszettel) {

			Klassenstufe klassenstufe = loesungszettel.klassenstufe();
			List<Loesungszettel> loesungszettelDerKlassenstufe = klassenstufenMap.getOrDefault(klassenstufe,
				new ArrayList<>());
			loesungszettelDerKlassenstufe.add(loesungszettel);
			klassenstufenMap.put(klassenstufe, loesungszettelDerKlassenstufe);
		}

		return klassenstufenMap;
	}

}
