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
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_kataloge.domain.InverseKatalogItem;
import de.egladil.web.mk_kataloge.domain.KatalogItem;
import de.egladil.web.mk_kataloge.domain.KatalogItemNameComparator;
import de.egladil.web.mk_kataloge.domain.Katalogtyp;
import de.egladil.web.mk_kataloge.persistence.KatalogRepository;
import de.egladil.web.mk_kataloge.persistence.impl.entities.Land;
import de.egladil.web.mk_kataloge.persistence.impl.entities.LandToInverseKatalogItemMapper;
import de.egladil.web.mk_kataloge.persistence.impl.entities.OrtInverse;
import de.egladil.web.mk_kataloge.persistence.impl.entities.OrtInverseToInverseKatalogItemMapper;
import de.egladil.web.mk_kataloge.persistence.impl.entities.SchuleInverse;
import de.egladil.web.mk_kataloge.persistence.impl.entities.SchuleInverseToInverseKatalogItemMapper;

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

		List<Land> laender = loadLandEntities();

		final LandKatalogItemMapper mapper = new LandKatalogItemMapper();
		final KatalogItemNameComparator comparator = new KatalogItemNameComparator();

		List<KatalogItem> result = laender.stream().map(land -> mapper.apply(land)).collect(Collectors.toList());

		Collections.sort(result, comparator);

		return result;
	}

	/**
	 * @return
	 */
	private List<Land> loadLandEntities() {

		List<Land> laender = em.createQuery("select l from Land l where l.freigeschaltet = :freigeschaltet", Land.class)
			.setParameter("freigeschaltet", true).getResultList();
		return laender;
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

	@Override
	public List<InverseKatalogItem> findKatalogItems(final Katalogtyp typ, final String searchTerm) {

		switch (typ) {

		case LAND:

			List<Land> laender = this.loadLandEntities();
			final LandToInverseKatalogItemMapper mapper = new LandToInverseKatalogItemMapper();

			return laender.stream().map(l -> mapper.apply(l)).collect(Collectors.toList());

		case ORT:

			return this.findOrteWithName(searchTerm);

		case SCHULE:

			return this.findSchulenWithName(searchTerm);

		default:
			throw new IllegalArgumentException("unbekannter Katalogtyp " + typ);
		}
	}

	private List<InverseKatalogItem> findOrteWithName(final String searchTerm) {

		final OrtInverseToInverseKatalogItemMapper mapper = new OrtInverseToInverseKatalogItemMapper();

		String stmt = "select o from OrtInverse o where o.name like :name";

		TypedQuery<OrtInverse> query = em.createQuery(stmt, OrtInverse.class);
		query.setParameter("name", searchTerm + "%");

		List<OrtInverse> trefferliste = query.getResultList();

		return trefferliste.stream().map(o -> mapper.apply(o)).collect(Collectors.toList());
	}

	private List<InverseKatalogItem> findSchulenWithName(final String searchTerm) {

		final SchuleInverseToInverseKatalogItemMapper mapper = new SchuleInverseToInverseKatalogItemMapper();

		String stmt = "select s from SchuleInverse s where s.name like :name";

		TypedQuery<SchuleInverse> query = em.createQuery(stmt, SchuleInverse.class);
		query.setParameter("name", searchTerm + "%");

		List<SchuleInverse> trefferliste = query.getResultList();

		return trefferliste.stream().map(o -> mapper.apply(o)).collect(Collectors.toList());
	}

}
