// =====================================================
// Project: mja-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.infrastructure.filters;

import java.io.IOException;
import java.util.UUID;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.minikaenguru_statistik.MinikaenguruStatistikApplication;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.Provider;

/**
 * MDCHeaderFilter
 */
@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class MDCHeaderFilter implements ContainerRequestFilter, ContainerResponseFilter {

	private static final String USER_AGENT = "User-Agent";

	private static final String UNKNOWN_CLIENT_ID = "unknown";

	private static final Logger LOGGER = LoggerFactory.getLogger(MDCHeaderFilter.class);

	private static final String X_CORRELATION_ID_HEADER_NAME = "X-CORRELATION-ID";

	private static final String MDC_KEY_CLIENT_ID = "clientId";

	private static final String MDC_KEY_CORRELATION_ID = "correlationId";

	@Context
	private HttpHeaders headers;

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {

		String method = requestContext.getMethod();

		if (!"OPTIONS".equals(method)) {

			String path = requestContext.getUriInfo().getPath();
			String correlationId = getOrCreateCorrelationId();
			String clientId = getClientId();

			MDC.put(MDC_KEY_CORRELATION_ID, correlationId);
			MDC.put(MDC_KEY_CLIENT_ID, clientId);

			if (!UNKNOWN_CLIENT_ID.equals(clientId)) {

				LOGGER.info("request.path={}", path);
			}
		}

	}

	@Override
	public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {

		MDC.remove(MDC_KEY_CORRELATION_ID);
		MDC.remove(MDC_KEY_CLIENT_ID);
	}

	private String getOrCreateCorrelationId() {

		String correlationId = headers.getHeaderString(X_CORRELATION_ID_HEADER_NAME);
		return correlationId != null ? correlationId : UUID.randomUUID().toString();

	}

	private String getClientId() {

		String clientId = headers.getHeaderString(MinikaenguruStatistikApplication.X_CLIENT_ID_HEADER_NAME);

		if (clientId != null) {

			return clientId;
		}

		String userAgent = headers.getHeaderString(USER_AGENT);
		return userAgent != null ? userAgent : UNKNOWN_CLIENT_ID;
	}

}
