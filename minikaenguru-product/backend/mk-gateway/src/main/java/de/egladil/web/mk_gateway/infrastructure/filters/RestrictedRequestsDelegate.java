// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.filters;

import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.container.ContainerRequestContext;

/**
 * RestrictedRequestsDelegate gibt nur die URLs zurück, die per s2s authentifiziert werden müssen.
 */
public class RestrictedRequestsDelegate {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestrictedRequestsDelegate.class);

	private static final String[] RESTRICED_PATHS_PREFIX = new String[] { "/mkbiza/" };

	public boolean needsS2SAuthorization(final ContainerRequestContext requestContext) {

		String method = requestContext.getMethod();

		if ("OPTIONS".equals(method)) {

			LOGGER.debug("keine Auth bei OPTIONS");

			return false;
		}

		String path = requestContext.getUriInfo().getPath();

		Optional<String> optTreffer = Arrays.stream(RESTRICED_PATHS_PREFIX).filter(ip -> path.startsWith(ip)).findFirst();

		return optTreffer.isPresent();
	}

	public static String[] getRestricedPathsPrefix() {

		return RESTRICED_PATHS_PREFIX;
	}

}
