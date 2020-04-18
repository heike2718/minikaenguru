// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.egladil.web.mk_kataloge.domain.KatalogItem;
import de.egladil.web.mk_kataloge.domain.KatalogItemNameComparator;
import de.egladil.web.mk_kataloge.persistence.KatalogeRepository;
import de.egladil.web.mk_kataloge.persistence.impl.entities.Ort;
import de.egladil.web.mk_kataloge.persistence.impl.entities.OrtToKatalogItemMapper;
import de.egladil.web.mk_kataloge.persistence.impl.entities.Schule;
import de.egladil.web.mk_kataloge.persistence.impl.entities.SchuleToKatalogItemMapper;
import de.egladil.web.mk_kataloge.service.KatalogFacade;

/**
 * KatalogFacadeImpl
 */
@ApplicationScoped
public class KatalogFacadeImpl implements KatalogFacade {

	@Inject
	KatalogeRepository katalogRepository;

	@Override
	public int countOrteInLand(final String kuerzel) {

		return katalogRepository.countOrteInLand(kuerzel);
	}

	@Override
	public List<KatalogItem> loadOrteInLand(final String kuerzel) {

		List<Ort> orte = katalogRepository.loadOrteInLand(kuerzel);

		final OrtToKatalogItemMapper mapper = new OrtToKatalogItemMapper(katalogRepository);

		final List<KatalogItem> result = orte.stream().map(ort -> mapper.apply(ort)).collect(Collectors.toList());
		Collections.sort(result, new KatalogItemNameComparator());
		return result;
	}

	@Override
	public int countSchulenInOrt(final String kuerzel) {

		return katalogRepository.countSchulenInOrt(kuerzel);
	}

	@Override
	public List<KatalogItem> loadSchulenInOrt(final String kuerzel) {

		List<Schule> schulen = katalogRepository.loadSchulenInOrt(kuerzel);
		final SchuleToKatalogItemMapper mapper = new SchuleToKatalogItemMapper(katalogRepository);

		final List<KatalogItem> result = schulen.stream().map(schule -> mapper.apply(schule)).collect(Collectors.toList());
		Collections.sort(result, new KatalogItemNameComparator());
		return result;
	}

}
