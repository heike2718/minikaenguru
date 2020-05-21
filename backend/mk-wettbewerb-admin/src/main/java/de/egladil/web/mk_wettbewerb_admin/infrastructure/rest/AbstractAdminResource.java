// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.infrastructure.rest;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_wettbewerb_admin.domain.error.AccessDeniedException;

/**
 * AbstractAdminResource
 */
public abstract class AbstractAdminResource {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractAdminResource.class);

	@ConfigProperty(name = "admin.uuid")
	String adminUuid;

	/**
	 * Prüft nochmals, ob die Resource auch aufgerufen werden darf. Aufruf erfolgt innerhalb eines lokalen Docket-Networks.
	 * Autorisierung für ganz außen erfolgt im mk-gateway.
	 *
	 * @param  uuid
	 * @throws AccessDeniedException
	 */
	protected void checkAccess(final String uuid) throws AccessDeniedException {

		if (!adminUuid.equals(uuid)) {

			LOG.warn("Achtung! unberechtigter Zugriff mit UUID={}", uuid);

			throw new AccessDeniedException();
		}
	}

}
