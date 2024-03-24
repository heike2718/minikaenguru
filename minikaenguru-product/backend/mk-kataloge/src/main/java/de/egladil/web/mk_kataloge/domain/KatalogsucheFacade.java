// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Land;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.LandToKatalogItemMapper;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Ort;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.OrtToKatalogItemMapper;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.SchuleToKatalogItemMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * KatalogsucheFacade
 */
@ApplicationScoped
public class KatalogsucheFacade {

	@Inject
	KatalogeRepository katalogeRepository;

	/**
	 * Gibt alle Länder zurück, deren Name mit suchbegriff beginnt.
	 *
	 * @param  suchbegriff
	 *                     String
	 * @return             List
	 */
	public List<KatalogItem> sucheLaenderMitNameBeginnendMit(final String suchbegriff) {

		List<Land> laender = katalogeRepository.findLander(suchbegriff);
		List<KatalogItem> result = mapAndSortLaender(laender);

		return result;
	}

	/**
	 * Gibt alle Orte zurück, deren Name mit suchbegriff beginnt.
	 *
	 * @param  suchbegriff
	 * @return             List
	 */
	public List<KatalogItem> sucheOrteMitNameBeginnendMit(final String suchbegriff) {

		List<Ort> orte = katalogeRepository.findOrte(suchbegriff);
		List<KatalogItem> result = mapAndSortOrte(orte);
		return result;
	}

	/**
	 * Gibt alle Schulen zurück, deren Name mit suchbegriff beginnt.
	 *
	 * @param  suchbegriff
	 *                     String
	 * @return             List
	 */
	public List<KatalogItem> sucheSchulenMitNameEnthaltend(final String suchbegriff) {

		List<Schule> schulen = katalogeRepository.findSchulen(suchbegriff);
		List<KatalogItem> result = mapAndSortSchulen(schulen);
		return result;
	}

	/**
	 * Gibt alle Orte im Land mit dem Kuerzel landkuerzel zurück, deren Name mit suchbegriff beginnt.
	 *
	 * @param  landkuerzel
	 *                     String
	 * @param  suchbegriff
	 *                     String
	 * @return             List
	 */
	public List<KatalogItem> sucheOrteInLandMitNameBeginnendMit(final String landkuerzel, final String suchbegriff) {

		List<Ort> orte = katalogeRepository.findOrteInLand(landkuerzel, suchbegriff);
		List<KatalogItem> result = mapAndSortOrte(orte);
		return result;
	}

	/**
	 * Gibt alle Schulen im Ort mit dem Kuerzel ortkuerzel zurück, deren Name mit suchbegriff beginnt.
	 *
	 * @param  ortkuerzel
	 *                     String
	 * @param  suchbegriff
	 *                     String
	 * @return             List
	 */
	public List<KatalogItem> sucheSchulenInOrtMitNameEnthaltend(final String ortkuerzel, final String suchbegriff) {

		List<Schule> schulen = katalogeRepository.findSchulenInOrt(ortkuerzel, suchbegriff);

		List<KatalogItem> result = mapAndSortSchulen(schulen);
		return result;
	}

	private List<KatalogItem> mapAndSortSchulen(final List<Schule> schulen) {

		final SchuleToKatalogItemMapper mapper = new SchuleToKatalogItemMapper(this.katalogeRepository);
		List<KatalogItem> result = schulen.stream().map(s -> mapper.apply(s)).collect(Collectors.toList());
		Collections.sort(result, new KatalogItemNameComparator());
		return result;
	}

	private List<KatalogItem> mapAndSortOrte(final List<Ort> orte) {

		final OrtToKatalogItemMapper mapper = new OrtToKatalogItemMapper(this.katalogeRepository);
		List<KatalogItem> result = orte.stream().map(o -> mapper.apply(o)).collect(Collectors.toList());
		Collections.sort(result, new KatalogItemNameComparator());
		return result;
	}

	private List<KatalogItem> mapAndSortLaender(final List<Land> laender) {

		final LandToKatalogItemMapper mapper = new LandToKatalogItemMapper();
		List<KatalogItem> result = laender.stream().map(l -> mapper.apply(l)).collect(Collectors.toList());
		Collections.sort(result, new KatalogItemNameComparator());
		return result;
	}

}
