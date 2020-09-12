// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import de.egladil.web.mk_gateway.domain.user.UserRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.User;

/**
 * UserRepositoryHibernate
 */
@RequestScoped
public class UserRepositoryHibernate implements UserRepository {

	@Inject
	EntityManager em;

	@Override
	public Optional<User> ofId(final String uuid) {

		User user = em.find(User.class, uuid);

		return user == null ? Optional.empty() : Optional.of(user);

	}

	@Override
	@Transactional
	public User addUser(final User user) {

		this.em.persist(user);

		return user;
	}

	@Override
	public void removeUser(final String uuid) {

		User persistenterUser = em.find(User.class, uuid);

		if (persistenterUser != null) {

			em.remove(persistenterUser);
		}

	}

}
