// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.signup;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.mk_gateway.domain.apimodel.migration.ZuMigrierenderBenutzer;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.User;

/**
 * MigrationService
 */
@ApplicationScoped
public class MigrationService {

	@Inject
	SignUpService signupService;

	/**
	 * Importiert den gegebenen veranstalter.
	 *
	 * @param veranstalter
	 */
	public User veranstalterImportieren(final ZuMigrierenderBenutzer veranstalter) {

		Rolle rolle = veranstalter.rolle();

		String nonce = null;

		switch (rolle) {

		case LEHRER:
			if (StringUtils.isBlank(veranstalter.schulkuerzel())) {

				throw new IllegalArgumentException("Lehrer kann nur mit Schulkuerzel migriert werden!");
			}
			nonce = rolle + "-" + veranstalter.schulkuerzel();
			break;

		case PRIVAT:
			nonce = rolle.toString();
			break;

		default:
			throw new SecurityException("unerlaubte Rolle " + rolle + " fuer den Import von Veranstaltern");
		}

		if (veranstalter.mailbenachrichtigung()) {

			nonce += "-TRUE";
		}

		SignUpResourceOwner resourceOwner = new SignUpResourceOwner(veranstalter.uuid(), veranstalter.fullName(), nonce);
		User user = signupService.createUser(resourceOwner, veranstalter.isAnonym());
		return user;
	}
}
