// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.testdaten;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.user.UserRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.User;

/**
 * InMemoryUserRepository
 */
public class InMemoryUserRepository implements UserRepository {

	private List<User> users = new ArrayList<>();

	public InMemoryUserRepository() {

		{

			User user = new User();
			user.setRolle(Rolle.PRIVAT);
			user.setUuid("UUID_PRIVAT");

			users.add(user);
		}

		{

			User user = new User();
			user.setRolle(Rolle.PRIVAT);
			user.setUuid("UUID_PRIVAT_GESPERRT");

			users.add(user);
		}

		{

			User user = new User();
			user.setRolle(Rolle.PRIVAT);
			user.setUuid("UUID_PRIVAT_NICHT_ANGEMELDET");

			users.add(user);
		}

		{

			User user = new User();
			user.setRolle(Rolle.PRIVAT);
			user.setUuid("UUID_PRIVAT_NICHT_ANGEMELDET");

			users.add(user);
		}

		{

			User user = new User();
			user.setRolle(Rolle.PRIVAT);
			user.setUuid("UUID_PRIVAT_MEHRERE_TEILNAHMEKURZEL");

			users.add(user);
		}

		{

			User user = new User();
			user.setRolle(Rolle.PRIVAT);
			user.setUuid("UUID_PRIVAT_KEIN_TEILNAHMEKURZEL");

			users.add(user);
		}

		{

			User user = new User();
			user.setRolle(Rolle.LEHRER);
			user.setUuid("UUID_LEHRER_1");

			users.add(user);
		}

		{

			User user = new User();
			user.setRolle(Rolle.LEHRER);
			user.setUuid("UUID_LEHRER_2");

			users.add(user);
		}

		{

			User user = new User();
			user.setRolle(Rolle.LEHRER);
			user.setUuid("UUID_LEHRER_3");

			users.add(user);
		}

		{

			User user = new User();
			user.setRolle(Rolle.LEHRER);
			user.setUuid("UUID_LEHRER_GESPERRT");

			users.add(user);
		}

		{

			User user = new User();
			user.setRolle(Rolle.LEHRER);
			user.setUuid("UUID_LEHRER_ANDERE_SCHULE");

			users.add(user);
		}

	}

	@Override
	public Optional<User> ofId(final String uuid) {

		return users.stream().filter(u -> u.getUuid().equals(uuid)).findFirst();
	}

	@Override
	public User addUser(final User user) {

		return null;
	}

	@Override
	public void removeUser(final String uuid) {

	}

}
