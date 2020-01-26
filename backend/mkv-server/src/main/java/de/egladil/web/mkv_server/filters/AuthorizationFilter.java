// =====================================================
// Project: mkv-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.mk_commons.exception.AuthException;
import de.egladil.web.mk_commons.session.MkSecurityContext;
import de.egladil.web.mk_commons.session.Session;
import de.egladil.web.mkv_server.MkvServerApp;
import de.egladil.web.mkv_server.session.MkvSessionService;

/**
 * AuthorizationFilter
 */
@ApplicationScoped
@Provider
@PreMatching
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationFilter.class);

	private static final List<String> AUTHORIZED_PATHS = Arrays.asList(new String[] { "/signup" });

	@ConfigProperty(name = "stage")
	String stage;

	@Context
	ResourceInfo resourceInfo;

	@Inject
	MkvSessionService sessionService;

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {

		String method = requestContext.getMethod();

		if ("OPTIONS".equals(method)) {

			return;
		}

		String path = requestContext.getUriInfo().getPath();
		LOG.debug("entering AuthorizationFilter: path={}", path);

		if (!needsSession(path)) {

			return;
		}

		String sessionId = CommonHttpUtils.getSessionId(requestContext, stage, MkvServerApp.CLIENT_COOKIE_PREFIX);

		if (sessionId == null) {

			throw new AuthException("Keine Berechtigung");
		}

		Session session = sessionService.getAndRefreshSessionIfValid(sessionId);

		if (session == null) {

			throw new AuthException("Keine gültige Session vorhanden");
		}

		MkSecurityContext securityContext = new MkSecurityContext(session);
		requestContext.setSecurityContext(securityContext);
	}

	private boolean needsSession(final String path) {

		Optional<String> optPath = AUTHORIZED_PATHS.stream().filter(p -> path.toLowerCase().startsWith(p)).findFirst();

		return optPath.isPresent();
	}

}
