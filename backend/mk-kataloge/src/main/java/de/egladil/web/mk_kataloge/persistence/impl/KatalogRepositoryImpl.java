// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.persistence.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_kataloge.domain.KatalogItem;
import de.egladil.web.mk_kataloge.domain.KatalogItemNameComparator;
import de.egladil.web.mk_kataloge.persistence.KatalogRepository;
import de.egladil.web.mk_kataloge.persistence.impl.entities.Land;

/**
 * KatalogRepositoryImpl
 */
@RequestScoped
public class KatalogRepositoryImpl implements KatalogRepository {

	private static final Logger LOG = LoggerFactory.getLogger(KatalogRepositoryImpl.class);

	@Inject
	EntityManager em;

	/**
	 *
	 */
	public KatalogRepositoryImpl() {

	}

	@Override
	public List<KatalogItem> loadLaender() {

		List<Land> laender = em.createQuery("select l from Land l where l.freigeschaltet = :freigeschaltet", Land.class)
			.setParameter("freigeschaltet", true).getResultList();

		final LandKatalogItemMapper mapper = new LandKatalogItemMapper();
		final KatalogItemNameComparator comparator = new KatalogItemNameComparator();

		List<KatalogItem> result = laender.stream().map(land -> mapper.apply(land)).collect(Collectors.toList());

		Collections.sort(result, comparator);

		return result;
	}

	@Override
	public List<KatalogItem> loadOrte(final String landKuerzel) {

		Land land = this.findLandWithKuerzel(landKuerzel).get();

		final OrtKatalogItemMapper mapper = new OrtKatalogItemMapper();
		final KatalogItemNameComparator comparator = new KatalogItemNameComparator();

		List<KatalogItem> result = land.getOrte().stream().map(ort -> mapper.apply(ort)).collect(Collectors.toList());
		Collections.sort(result, comparator);

		return result;
	}

	private Optional<Land> findLandWithKuerzel(final String landKuerzel) {

		if (StringUtils.isBlank(landKuerzel)) {

			LOG.error("landKuerzel ist blank");
			throw new NotFoundException("Land mit landKuerzel=blank existiert nicht");
		}

		String stmt = "select l from Land l where l.kuerzel = :kuerzel";
		List<Land> trefferliste = em.createQuery(stmt, Land.class).setParameter("kuerzel", landKuerzel).getResultList();

		if (trefferliste.isEmpty()) {

			LOG.error("Land mit kuerzel={} existiert nicht", landKuerzel);
			throw new NotFoundException("Land mit landKuerzel=" + landKuerzel + " existiert nicht");
		}

		return Optional.of(trefferliste.get(0));

	}

}
