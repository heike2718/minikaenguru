// =====================================================
// Project: mkv-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server.duplicated.dao;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.domain.impl.User;

/**
 * MkvUserDao
 */
@Deprecated(forRemoval = true)
@RequestScoped
public class MkvUserDao implements IMkvUserDao {

	@Inject
	EntityManager em;

	/**
	 *
	 */
	public MkvUserDao() {

	}

	@Override
	public Optional<User> findByUUId(final String uuid) {

		List<User> trefferliste = em.createNamedQuery(User.FIND_BY_UUID, User.class).setParameter("uuid", uuid)
			.getResultList();

		return trefferliste.isEmpty() ? Optional.empty() : Optional.of(trefferliste.get(0));
	}

}
