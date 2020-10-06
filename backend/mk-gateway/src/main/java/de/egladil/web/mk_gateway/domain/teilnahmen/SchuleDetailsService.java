// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.adv.VertragAuftragsdatenverarbeitung;
import de.egladil.web.mk_gateway.domain.adv.VertragAuftragsverarbeitungRepository;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.semantik.DomainService;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.SchulteilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.veranstalter.Person;
import de.egladil.web.mk_gateway.domain.veranstalter.SchulkollegienRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.Schulkollegium;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleDetails;

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

	@Inject
	VertragAuftragsverarbeitungRepository advRepository;

	public static SchuleDetailsService createForTest(final AktuelleTeilnahmeService aktuelleTeilnahmeService, final SchulkollegienRepository schulkollegienRepository, final TeilnahmenRepository teilnahmenRepository, final VeranstalterRepository veranstalterRepository, final VertragAuftragsverarbeitungRepository advRepository) {

		SchuleDetailsService result = new SchuleDetailsService();

		result.aktuelleTeilnahmeService = aktuelleTeilnahmeService;
		result.schulkollegienRepository = schulkollegienRepository;
		result.teilnahmenRepository = teilnahmenRepository;
		result.veranstalterRepository = veranstalterRepository;
		result.advRepository = advRepository;

		return result;
	}

	/**
	 * Ermittelt die Details der gegeben Schule.
	 *
	 * @param  schuleID
	 *                               Identifier der Schule
	 * @param  lehrerIdentifier
	 *                               Identifier die UUID eines Lehrers. Kann null sein, wenn die Methode durch ADMIN-Resource
	 *                               aufgerufen wird.
	 * @return
	 * @throws AccessDeniedException
	 */
	public SchuleDetails ermittleSchuldetails(final Identifier schuleID, final Identifier lehrerIdentifier) throws AccessDeniedException {

		Optional<Schulkollegium> optKollegium = schulkollegienRepository.ofSchulkuerzel(schuleID);

		SchuleDetails result = null;

		if (optKollegium.isPresent()) {

			result = new SchuleDetails(schuleID.identifier());

			Schulkollegium kollegium = optKollegium.get();

			List<Person> andere = kollegium.alleLehrerUnmodifiable().stream()
				.filter(p -> lehrerIdentifier == null || !p.uuid().equals(lehrerIdentifier.identifier()))
				.collect(Collectors.toList());

			result.withKollegen(andere);

			List<Teilnahme> teilnahmen = teilnahmenRepository.ofTeilnahmenummer(schuleID.identifier());

			Optional<Teilnahme> optTeilnahme = aktuelleTeilnahmeService.aktuelleTeilnahme(teilnahmen);

			if (optTeilnahme.isPresent()) {

				Teilnahme aktuelle = optTeilnahme.get();

				if (aktuelle.teilnahmeart() == Teilnahmeart.SCHULE) {

					Schulteilnahme schulteilnahme = (Schulteilnahme) aktuelle;
					Identifier veranstalterID = schulteilnahme.angemeldetDurchVeranstalterId();

					if (veranstalterID != null) {

						Optional<Veranstalter> optAnmelder = veranstalterRepository.ofId(veranstalterID);

						if (optAnmelder.isPresent()) {

							result.withAngemeldetDurch(optAnmelder.get().person());
						}
					}
				}

				result.withTeilnahme(SchulteilnahmeAPIModel.create((Schulteilnahme) aktuelle));
			}

			result.withAnzahlTeilnahmen(teilnahmen.size());

			Optional<VertragAuftragsdatenverarbeitung> optVertrag = advRepository.findVertragForSchule(schuleID);

			result.withHatAdv(optVertrag.isPresent());
		}

		return result;
	}

}
