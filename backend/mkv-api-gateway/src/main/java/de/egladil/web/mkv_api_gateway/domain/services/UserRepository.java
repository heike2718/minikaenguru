// =====================================================
// Project: mkv-api-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_api_gateway.domain.services;

import java.util.Optional;

import de.egladil.web.mkv_api_gateway.infrastructure.persistence.entities.User;

/**
 * UserRepository
 */
public interface UserRepository {

	/**
	 * Sucht den User mit der gegebenen UUID.
	 *
	 * @param  uuid
	 * @return
	 */
	Optional<User> ofId(String uuid);

	/**
	 * Fügt den gegebenen User hinzu oder aktualisiert ihn, wenn er bereits vorhanden ist. Rückgabe wegen Mockito.
	 *
	 * @param  user
	 *              User
	 * @return      User
	 */
	User addUser(User user);

}
