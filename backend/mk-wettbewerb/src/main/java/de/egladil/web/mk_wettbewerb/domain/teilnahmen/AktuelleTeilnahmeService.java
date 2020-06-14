// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.mk_wettbewerb.domain.semantik.DomainService;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbService;

/**
 * AktuelleTeilnahmeService
 */
@RequestScoped
@DomainService
public class AktuelleTeilnahmeService {

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	static AktuelleTeilnahmeService createForTest(final TeilnahmenRepository teilnahmenRepository, final WettbewerbService wettbewerbService) {

		AktuelleTeilnahmeService result = new AktuelleTeilnahmeService();
		result.teilnahmenRepository = teilnahmenRepository;
		result.wettbewerbService = wettbewerbService;
		return result;
	}

	/**
	 * Sucht die aktuelle Teilnahme mit der gegebenen teilnahmenummer.
	 *
	 * @param  teilnahmenummer
	 * @return                 boolean
	 */
	public Optional<Teilnahme> aktuelleTeilnahme(final String teilnahmenummer) {

		if (StringUtils.isBlank(teilnahmenummer)) {

			throw new IllegalArgumentException("teilnahmenummer darf nicht blank sein.");
		}

		List<Teilnahme> teilnahmen = teilnahmenRepository.ofTeilnahmenummer(teilnahmenummer);

		return this.aktuelleTeilnahme(teilnahmen);
	}

	/**
	 * Wenn man irgendwo her die Teilnahmen bereits hat, ist das einfacher.
	 *
	 * @param  teilnahmen
	 *                    List
	 * @return            Optional
	 */
	public Optional<Teilnahme> aktuelleTeilnahme(final List<Teilnahme> teilnahmen) {

		if (teilnahmen == null) {

			throw new IllegalArgumentException("teilnahmen darf nicht null sein.");
		}

		Optional<Wettbewerb> optLaufend = wettbewerbService.aktuellerWettbewerb();

		if (optLaufend.isEmpty()) {

			return Optional.empty();
		}

		final Wettbewerb laufenderWettbewerb = optLaufend.get();

		Optional<Teilnahme> optAktuelle = teilnahmen.stream().filter(t -> t.wettbewerbID().equals(laufenderWettbewerb.id()))
			.findFirst();

		return optAktuelle;

	}

	/**
	 *
	 */
	public void meldePrivatpersonZumAktuellenWettbewerbAn(final String uuid) {

	}

}
