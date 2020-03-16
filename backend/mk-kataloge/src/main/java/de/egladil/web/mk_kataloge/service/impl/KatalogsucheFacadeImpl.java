// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import de.egladil.web.mk_kataloge.domain.KatalogItem;
import de.egladil.web.mk_kataloge.domain.KatalogItemNameComparator;
import de.egladil.web.mk_kataloge.persistence.KatalogeRepository;
import de.egladil.web.mk_kataloge.persistence.impl.entities.Land;
import de.egladil.web.mk_kataloge.persistence.impl.entities.LandToKatalogItemMapper;
import de.egladil.web.mk_kataloge.persistence.impl.entities.Ort;
import de.egladil.web.mk_kataloge.persistence.impl.entities.OrtToKatalogItemMapper;
import de.egladil.web.mk_kataloge.persistence.impl.entities.Schule;
import de.egladil.web.mk_kataloge.persistence.impl.entities.SchuleToKatalogItemMapper;
import de.egladil.web.mk_kataloge.service.KatalogsucheFacade;

/**
 * KatalogsucheFacadeImpl
 */
@RequestScoped
public class KatalogsucheFacadeImpl implements KatalogsucheFacade {

	@Inject
	KatalogeRepository katalogeRepository;

	@Override
	public List<KatalogItem> sucheLaenderMitNameBeginnendMit(final String suchbegriff) {

		List<Land> laender = katalogeRepository.findLander(suchbegriff);
		List<KatalogItem> result = mapAndSortLaender(laender);

		return result;
	}

	@Override
	public List<KatalogItem> sucheOrteMitNameBeginnendMit(final String suchbegriff) {

		List<Ort> orte = katalogeRepository.findOrte(suchbegriff);
		List<KatalogItem> result = mapAndSortOrte(orte);
		return result;
	}

	@Override
	public List<KatalogItem> sucheSchulenMitNameBeginnendMit(final String suchbegriff) {

		List<Schule> schulen = katalogeRepository.findSchulen(suchbegriff);
		List<KatalogItem> result = mapAndSortSchulen(schulen);
		return result;
	}

	@Override
	public List<KatalogItem> sucheOrteInLandMitNameBeginnendMit(final String landkuerzel, final String suchbegriff) {

		List<Ort> orte = katalogeRepository.findOrteInLand(landkuerzel, suchbegriff);
		List<KatalogItem> result = mapAndSortOrte(orte);
		return result;
	}

	@Override
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
