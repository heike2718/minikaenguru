// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.mk_gateway.domain.error.AuthException;
import de.egladil.web.mk_gateway.infrastructure.config.ConfigService;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.NoContentException;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

/**
 * OriginRefererFilter
 */
@ApplicationScoped
@Provider
@PreMatching
@Priority(900)
public class OriginRefererFilter implements ContainerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(OriginRefererFilter.class);

	private static final List<String> NO_CONTENT_PATHS = Arrays.asList(new String[] { "/favicon.ico" });

	@Inject
	ConfigService configService;

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {

		UriInfo uriInfo = requestContext.getUriInfo();
		String pathInfo = uriInfo.getPath();

		LOGGER.debug("path={}", pathInfo);

		if ("OPTIONS".equals(requestContext.getMethod()) || NO_CONTENT_PATHS.contains(pathInfo)) {

			throw new NoContentException(pathInfo);
		}

		this.validateOriginAndRefererHeader(requestContext);
	}

	private void validateOriginAndRefererHeader(final ContainerRequestContext requestContext) throws IOException {

		final String origin = requestContext.getHeaderString("Origin");
		final String referer = requestContext.getHeaderString("Referer");

		LOGGER.debug("Origin = [{}], Referer=[{}]", origin, referer);

		if (StringUtils.isBlank(origin) && StringUtils.isBlank(referer)) {

			final String details = "Header Origin UND Referer fehlen";

			if (configService.isBlockOnMissingOriginReferer()) {

				logErrorAndThrow(details, requestContext);
			}
		}

		if (!StringUtils.isBlank(origin)) {

			checkHeaderTarget(origin, requestContext);
		}

		if (!StringUtils.isBlank(referer)) {

			checkHeaderTarget(referer, requestContext);
		}
	}

	private void checkHeaderTarget(final String headerValue, final ContainerRequestContext requestContext) throws IOException {

		final String extractedValue = CommonHttpUtils.extractOrigin(headerValue);

		if (extractedValue == null) {

			return;
		}

		String targetOrigin = configService.getTargetOrigin();

		if (targetOrigin != null) {

			List<String> allowedOrigins = Arrays.asList(targetOrigin.split(","));

			if (!allowedOrigins.contains(extractedValue)) {

				final String details = "targetOrigin != extractedOrigin: [targetOrigin=" + targetOrigin
					+ ", extractedOriginOrReferer="
					+ extractedValue + "]";
				logErrorAndThrow(details, requestContext);
			}
		}
	}

	private void logErrorAndThrow(final String details, final ContainerRequestContext requestContext) throws IOException {

		final String dump = CommonHttpUtils.getRequestInfos(requestContext);

		String msg = "Possible CSRF-Attack: " + details + ", " + dump;

		LOGGER.warn(msg);

		throw new AuthException();
	}

}
