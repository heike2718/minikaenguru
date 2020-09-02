// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.net.URI;
import java.net.URISyntaxException;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * AbstractAdminResource
 */
public abstract class AbstractAdminResource {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractAdminResource.class);

	@ConfigProperty(name = "admin.created.uri.prefix", defaultValue = "https://mathe-jung-alt.de/mk-gateway/admin")
	String createdUriPrefix;

	@Context
	SecurityContext securityContext;

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
	@Deprecated(forRemoval = true)
	// FIXME: ablösen durch Interceptor!!!!
	protected void checkAccess(final String methodName) throws AccessDeniedException {

		if (!securityContext.isUserInRole(Rolle.ADMIN.toString())) {

			String msg = "Unberechtigter Aufruf von '" + methodName + "' mit UUID=" + securityContext.getUserPrincipal().getName();

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
			throw new MkGatewayRuntimeException("Fehlerhafte URI: " + locationString, e);
		}
	}

	protected SecurityIncidentRegistered getSecurityIncident() {

		return securityIncident;
	}

}
