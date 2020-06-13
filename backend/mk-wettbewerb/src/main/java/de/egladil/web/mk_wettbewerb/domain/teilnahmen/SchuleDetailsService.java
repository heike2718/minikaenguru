// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.apimodel.SchuleDetails;
import de.egladil.web.mk_wettbewerb.domain.error.AccessDeniedException;
import de.egladil.web.mk_wettbewerb.domain.personen.Person;
import de.egladil.web.mk_wettbewerb.domain.personen.SchulkollegienRepository;
import de.egladil.web.mk_wettbewerb.domain.personen.Schulkollegium;
import de.egladil.web.mk_wettbewerb.domain.personen.Veranstalter;
import de.egladil.web.mk_wettbewerb.domain.personen.VeranstalterRepository;
import de.egladil.web.mk_wettbewerb.domain.semantik.DomainService;

/**
 * SchuleDetailsService
 */
@ApplicationScoped
@DomainService
public class SchuleDetailsService {

	@Inject
	AktuelleTeilnahmeService aktuelleTeilnahmeService;

	@Inject
	SchulkollegienRepository schulkollegienRepository;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	VeranstalterRepository veranstalterRepository;

	public static SchuleDetailsService createForTest(final AktuelleTeilnahmeService aktuelleTeilnahmeService, final SchulkollegienRepository schulkollegienRepository, final TeilnahmenRepository teilnahmenRepository, final VeranstalterRepository veranstalterRepository) {

		SchuleDetailsService result = new SchuleDetailsService();

		result.aktuelleTeilnahmeService = aktuelleTeilnahmeService;
		result.schulkollegienRepository = schulkollegienRepository;
		result.teilnahmenRepository = teilnahmenRepository;
		result.veranstalterRepository = veranstalterRepository;

		return result;
	}

	/**
	 * Ermittelt die Details der gegeben Schule.
	 *
	 * @param  schuleID
	 * @param  lehrerID
	 * @return
	 * @throws AccessDeniedException
	 */
	public SchuleDetails ermittleSchuldetails(final Identifier schuleID, final Identifier lehrerID) throws AccessDeniedException {

		Optional<Schulkollegium> optKollegium = schulkollegienRepository.ofSchulkuerzel(schuleID);

		SchuleDetails result = new SchuleDetails(schuleID.identifier());

		if (optKollegium.isPresent()) {

			Schulkollegium kollegium = optKollegium.get();

			List<Person> andere = kollegium.alleLehrerUnmodifiable().stream().filter(p -> !p.uuid().equals(lehrerID.identifier()))
				.collect(Collectors.toList());

			result.withKollegen(andere);
		}

		List<Teilnahme> teilnahmen = teilnahmenRepository.ofTeilnahmenummer(schuleID.identifier());
		result.withAnzahlTeilnahmen(teilnahmen.size());

		Optional<Teilnahme> optTeilnahme = aktuelleTeilnahmeService.aktuelleTeilnahme(teilnahmen);

		if (optTeilnahme.isPresent()) {

			Teilnahme aktuelle = optTeilnahme.get();

			if (aktuelle.teilnahmeart() == Teilnahmeart.SCHULE) {

				Schulteilnahme schulteilnahme = (Schulteilnahme) aktuelle;
				Identifier veranstalterID = schulteilnahme.angemeldetDurchVeranstalterId();

				Optional<Veranstalter> optAnmelder = veranstalterRepository.ofId(veranstalterID);

				if (optAnmelder.isPresent()) {

					result.withAngemeldetDurch(optAnmelder.get().person());
				}

				result.withNameUrkunde(schulteilnahme.nameSchule());

			}
		}

		return result;
	}

}
