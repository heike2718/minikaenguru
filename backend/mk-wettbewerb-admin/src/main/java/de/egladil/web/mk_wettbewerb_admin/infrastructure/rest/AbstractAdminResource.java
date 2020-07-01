// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.infrastructure.rest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_wettbewerb_admin.domain.error.AccessDeniedException;
import de.egladil.web.mk_wettbewerb_admin.domain.error.MkWettbewerbAdminRuntimeException;
import de.egladil.web.mk_wettbewerb_admin.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_wettbewerb_admin.domain.event.SecurityIncidentRegistered;

/**
 * AbstractAdminResource
 */
public abstract class AbstractAdminResource {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractAdminResource.class);

	@ConfigProperty(name = "admin.uuid")
	String adminUuid;

	@Inject
	Event<SecurityIncidentRegistered> securityEvent;

	private SecurityIncidentRegistered securityIncident;

	/**
	 * Prüft nochmals, ob die Resource auch aufgerufen werden darf. Aufruf erfolgt innerhalb eines lokalen Docket-Networks.
	 * Autorisierung für ganz außen erfolgt im mk-gateway.
	 *
	 * @param  uuid
	 * @throws AccessDeniedException
	 */
	protected void checkAccess(final String uuid, final String methodName) throws AccessDeniedException {

		if (!adminUuid.equals(uuid)) {

			String msg = "Unberechtigter Aufruf von '" + methodName + "' mit UUID=" + uuid;

			LOG.warn(msg);

			this.securityIncident = new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);

			throw new AccessDeniedException();
		}
	}

	/**
	 * Wandelt locationString in URI um und dabei eine eventuelle URISyntaxException in eine MkWettbewerbAdminRuntimeException
	 *
	 * @param  locationString
	 * @return                URI
	 */
	protected URI createdUri(final String locationString) {

		try {

			return new URI(locationString);
		} catch (URISyntaxException e) {

			LOG.error("Fehlerhafte URI {}: {} ", locationString, e.getMessage(), e);
			throw new MkWettbewerbAdminRuntimeException("Fehlerhafte URI: " + locationString, e);
		}
	}

	protected SecurityIncidentRegistered getSecurityIncident() {

		return securityIncident;
	}
}
