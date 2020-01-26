// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.dao.impl;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.IUserDao;
import de.egladil.web.mk_commons.domain.impl.User;
import io.quarkus.arc.DefaultBean;

/**
 * UserDao
 */
@RequestScoped
@DefaultBean
public class UserDao extends BaseDao implements IUserDao {

	/**
	 *
	 */
	public UserDao() {

	}

	/**
	 * @param em
	 */
	public UserDao(final EntityManager em) {

		super(em);

	}

	@Override
	public Optional<User> findByUUId(final String uuid) {

		List<User> trefferliste = getEm().createNamedQuery(User.FIND_BY_UUID, User.class).setParameter("uuid", uuid)
			.getResultList();

		return trefferliste.isEmpty() ? Optional.empty() : Optional.of(trefferliste.get(0));
	}

}
