// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.persistence.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import de.egladil.web.mk_kataloge.domain.InverseKatalogItem;
import de.egladil.web.mk_kataloge.persistence.KatalogRepository;
import de.egladil.web.mk_kataloge.persistence.impl.entities.LandInverse;
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

	@Inject
	EntityManager em;

	/**
	 *
	 */
	public KatalogRepositoryImpl() {

	}

	@Override
	public List<InverseKatalogItem> loadLaenderInverse() {

		final LandToInverseKatalogItemMapper mapper = new LandToInverseKatalogItemMapper();

		String stmt = "select l from LandInverse l";

		TypedQuery<LandInverse> query = em.createQuery(stmt, LandInverse.class);

		List<LandInverse> trefferliste = query.getResultList();

		return trefferliste.stream().map(o -> mapper.apply(o)).collect(Collectors.toList());
	}

	@Override
	public List<InverseKatalogItem> loadOrteInLand(final String landKuerzel) {

		String stmt = "select o from OrtInverse o where o.landKuerzel = :landKuerzel and o.name like :name";

		TypedQuery<OrtInverse> query = em.createQuery(stmt, OrtInverse.class);
		query.setParameter("landKuerzel", landKuerzel);

		return getOrte(query);
	}

	@Override
	public List<InverseKatalogItem> findOrteInLand(final String landKuerzel, final String searchTerm) {

		String stmt = "select o from OrtInverse o where o.landKuerzel = :landKuerzel and o.name like :name";

		TypedQuery<OrtInverse> query = em.createQuery(stmt, OrtInverse.class);
		query.setParameter("name", searchTerm + "%");
		query.setParameter("landKuerzel", landKuerzel);

		return getOrte(query);
	}

	@Override
	public List<InverseKatalogItem> findOrte(final String searchTerm) {

		String stmt = "select o from OrtInverse o where o.name like :name";

		TypedQuery<OrtInverse> query = em.createQuery(stmt, OrtInverse.class);
		query.setParameter("name", searchTerm + "%");

		return getOrte(query);
	}

	private List<InverseKatalogItem> getOrte(final TypedQuery<OrtInverse> query) {

		final OrtInverseToInverseKatalogItemMapper mapper = new OrtInverseToInverseKatalogItemMapper();

		List<OrtInverse> trefferliste = query.getResultList();

		return trefferliste.stream().map(o -> mapper.apply(o)).collect(Collectors.toList());

	}

	@Override
	public List<InverseKatalogItem> loadSchulenInOrt(final String ortKuerzel) {

		String stmt = "select s from SchuleInverse s where s.ortKuerzel = :ortKuerzel";

		TypedQuery<SchuleInverse> query = em.createQuery(stmt, SchuleInverse.class);
		query.setParameter("ortKuerzel", ortKuerzel);

		return getSchulen(query);
	}

	public List<InverseKatalogItem> findSchulenInOrt(final String ortKuerzel, final String searchTerm) {

		String stmt = "select s from SchuleInverse s where s.ortKuerzel = :ortKuerzel and s.name like :name";

		TypedQuery<SchuleInverse> query = em.createQuery(stmt, SchuleInverse.class);
		query.setParameter("name", searchTerm + "%");
		query.setParameter("ortKuerzel", ortKuerzel);

		return getSchulen(query);
	}

	private List<InverseKatalogItem> getSchulen(final TypedQuery<SchuleInverse> query) {

		final SchuleInverseToInverseKatalogItemMapper mapper = new SchuleInverseToInverseKatalogItemMapper();

		List<SchuleInverse> trefferliste = query.getResultList();

		return trefferliste.stream().map(o -> mapper.apply(o)).collect(Collectors.toList());

	}

}
