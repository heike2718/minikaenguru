// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.filters;

import java.io.IOException;
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
import de.egladil.web.mk_gateway.domain.error.AuthException;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.permissions.RestrictedUrlPath;
import de.egladil.web.mk_gateway.domain.permissions.RestrictedUrlPathRepository;
import de.egladil.web.mk_gateway.domain.session.LoggedInUser;
import de.egladil.web.mk_gateway.domain.session.MkSessionService;
import de.egladil.web.mk_gateway.domain.session.MkvSecurityContext;
import de.egladil.web.mk_gateway.domain.session.Session;
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
	RestrictedUrlPathRepository restrictedPathsRepository;

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

		final Optional<RestrictedUrlPath> optRestrictedPath = this.getRestirctedPath(path);

		if (!optRestrictedPath.isPresent()) {

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

		RestrictedUrlPath restrictedPath = optRestrictedPath.get();

		if (!restrictedPath.isAllowedForRolle(user.rolle())) {

			String msg = "restricted path " + path + " durch user " + user + " aufgerufen (falsche Rolle)";

			LOG.warn(msg);

			new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);
			throw new AuthException();
		}

		if (restrictedPath.isRestrictedForMethod(method)) {

			String msg = method + " " + path + " durch user " + user + " aufgerufen (falsche HttpMethod)";

			LOG.warn(msg);

			new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);
			throw new AuthException();

		}

		boolean secure = !configService.getStage().equals(MkGatewayApp.STAGE_DEV);
		MkvSecurityContext securityContext = new MkvSecurityContext(session, secure);
		requestContext.setSecurityContext(securityContext);

	}

	private Optional<RestrictedUrlPath> getRestirctedPath(final String path) {

		return restrictedPathsRepository.ofPath(path);
	}

}
