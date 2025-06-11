// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.dao;

import java.util.Optional;

import de.egladil.web.mk_gateway.domain.user.UserRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities.User;
import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

/**
 * UserHibernateRepository
 */
@RequestScoped
public class UserHibernateRepository implements UserRepository {

	@Inject
	@PersistenceUnit("wettbewerb")
	EntityManager em;

	public static UserHibernateRepository createForIntegrationTest(final EntityManager em) {

		UserHibernateRepository result = new UserHibernateRepository();
		result.em = em;
		return result;
	}

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
