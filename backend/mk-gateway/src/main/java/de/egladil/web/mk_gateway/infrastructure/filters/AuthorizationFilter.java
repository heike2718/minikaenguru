// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.filters;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.mk_gateway.MkGatewayApp;
import de.egladil.web.mk_gateway.domain.auth.session.LoggedInUser;
import de.egladil.web.mk_gateway.domain.auth.session.MkSessionService;
import de.egladil.web.mk_gateway.domain.auth.session.MkvSecurityContext;
import de.egladil.web.mk_gateway.domain.auth.session.Session;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.error.AuthException;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.permissions.PermittedRolesRepository;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.infrastructure.config.ConfigService;

/**
 * AuthorizationFilter
 */
@ApplicationScoped
@Provider
@PreMatching
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationFilter.class);

	@Inject
	ConfigService configService;

	@Context
	ResourceInfo resourceInfo;

	@Inject
	MkSessionService sessionService;

	@Inject
	PermittedRolesRepository permittedRolesRepository;

	@Inject
	Event<SecurityIncidentRegistered> securityEvent;

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {

		String method = requestContext.getMethod();

		if ("OPTIONS".equals(method)) {

			LOG.info("keine Auth bei OPTIONS");

			return;
		}

		String path = requestContext.getUriInfo().getPath();
		LOG.debug("entering AuthorizationFilter: path={}", path);

		List<Rolle> rollen = permittedRolesRepository.permittedRollen(path, method);

		if (rollen.isEmpty()) {

			return;
		}

		String sessionId = CommonHttpUtils.getSessionId(requestContext, configService.getStage(),
			MkGatewayApp.CLIENT_COOKIE_PREFIX);

		if (sessionId == null) {

			String msg = "restricted path " + path + " ohne sessionId aufgerufen";
			LOG.warn(msg);

			new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);

			throw new AuthException();
		}

		Session session = sessionService.getAndRefreshSessionIfValid(sessionId);

		if (session == null) {

			String msg = "restricted path " + path + " ohne gueltige Session aufgerufen";

			LOG.warn(msg);

			new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);
			throw new AuthException();
		}

		LoggedInUser user = session.user();

		if (user == null) {

			String msg = "restricted path " + path + " mit anonymer Session aufgerufen";

			LOG.warn(msg);

			new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);
			throw new AuthException();
		}

		Optional<Rolle> optPermittedRolle = rollen.stream().filter(r -> r == user.rolle()).findFirst();

		if (optPermittedRolle.isEmpty()) {

			String msg = "[" + method + " " + path + "] durch user " + user + " aufgerufen. Das ist nicht erlaubt";
			LOG.warn(msg);

			new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);
			throw new AccessDeniedException("keine Berechtigung, diese API aufzurufen");
		}

		boolean secure = !configService.getStage().equals(MkGatewayApp.STAGE_DEV);
		MkvSecurityContext securityContext = new MkvSecurityContext(session, secure);
		requestContext.setSecurityContext(securityContext);

	}
}
