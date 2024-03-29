// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.application.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_kataloge.application.KatalogFacade;
import de.egladil.web.mk_kataloge.domain.KatalogItem;
import de.egladil.web.mk_kataloge.domain.KatalogItemNameComparator;
import de.egladil.web.mk_kataloge.domain.KatalogeRepository;
import de.egladil.web.mk_kataloge.domain.apimodel.SchuleAPIModel;
import de.egladil.web.mk_kataloge.domain.event.DataInconsistencyRegistered;
import de.egladil.web.mk_kataloge.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Land;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.LandToKatalogItemMapper;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Ort;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.OrtToKatalogItemMapper;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.SchuleToKatalogItemMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

/**
 * KatalogFacadeImpl
 */
@ApplicationScoped
public class KatalogFacadeImpl implements KatalogFacade {

	private static final Logger LOG = LoggerFactory.getLogger(KatalogFacadeImpl.class);

	@Inject
	KatalogeRepository katalogRepository;

	@Inject
	Event<DataInconsistencyRegistered> dataInconcistencyEvent;

	@Inject
	LoggableEventDelegate eventDelegate;

	@Override
	public List<KatalogItem> loadLaender() {

		List<Land> laender = katalogRepository.loadLaender();

		final LandToKatalogItemMapper mapper = new LandToKatalogItemMapper();

		final List<KatalogItem> result = laender.stream().map(land -> mapper.apply(land)).collect(Collectors.toList());
		Collections.sort(result, new KatalogItemNameComparator());
		return result;
	}

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
		return mapSchulenToKatalogItems(schulen);
	}

	@Override
	public List<SchuleAPIModel> findSchulen(final String commaseparatedKuerzel) {

		String[] kuerzeltokens = StringUtils.split(commaseparatedKuerzel, ",");

		List<String> relevanteKuerzel = Arrays.stream(kuerzeltokens).filter(k -> !k.isBlank()).map(k -> k.trim())
			.collect(Collectors.toList());

		if (relevanteKuerzel.size() < kuerzeltokens.length) {

			String msg = "einige der Schulkürzel waren null oder leer. Diese wurden ignoriert: commaseparatedKuerzel="
				+ commaseparatedKuerzel;

			LOG.warn(msg);

			eventDelegate.fireDataInconsistencyEvent(msg, dataInconcistencyEvent);
		}

		List<Schule> schulen = katalogRepository.findSchulenWithKuerzeln(relevanteKuerzel);

		List<SchuleAPIModel> result = schulen.stream()
			.map(s -> new SchuleAPIModel(s.getKuerzel(), s.getName(), s.getOrtName(), s.getLandName(), s.getLandKuerzel()))
			.collect(Collectors.toList());

		return result;
	}

	/**
	 * @param  schulen
	 * @return
	 */
	private List<KatalogItem> mapSchulenToKatalogItems(final List<Schule> schulen) {

		final SchuleToKatalogItemMapper mapper = new SchuleToKatalogItemMapper(katalogRepository);

		final List<KatalogItem> result = schulen.stream().map(schule -> mapper.apply(schule)).collect(Collectors.toList());
		Collections.sort(result, new KatalogItemNameComparator());
		return result;
	}

}
