// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_api_gateway.infrastructure.persistence.impl;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import de.egladil.web.mkv_api_gateway.domain.services.UserRepository;
import de.egladil.web.mkv_api_gateway.infrastructure.persistence.entities.User;

/**
 * UserRepositoryHibernate
 */
@RequestScoped
public class UserRepositoryHibernate implements UserRepository {

	@Inject
	EntityManager em;

	@Override
	public Optional<User> ofId(final String uuid) {

		TypedQuery<User> query = em.createNamedQuery(User.FIND_USER_BY_UUID_QUERY, User.class).setParameter("uuid", uuid);

		try {

			User user = query.getSingleResult();
			return Optional.of(user);

		} catch (NoResultException e) {

			return Optional.empty();
		}

	}

	@Override
	public User addUser(final User user) {

		return null;
	}

}
