// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.mk_commons.exception.AuthException;

/**
 * OriginRefererFilterDelegate
 */
public class OriginRefererFilterDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(OriginRefererFilterDelegate.class);

	private final boolean blockOnMissingOriginReferer;

	private final String targetOrigin;

	/**
	 * @param blockOnMissingOriginReferer
	 * @param targetOrigin
	 */
	public OriginRefererFilterDelegate(final boolean blockOnMissingOriginReferer, final String targetOrigin) {

		this.blockOnMissingOriginReferer = blockOnMissingOriginReferer;
		this.targetOrigin = targetOrigin;
	}

	/**
	 * Prüft origin und referer. Das ist die wesentliche Funktionalität des Filters. Der Filter selbst wird nicht angesprochen, wenn
	 * er nicht direkt im quarkus-server liegt.
	 *
	 * @param  requestContext
	 * @throws IOException
	 */
	public void validateOriginAndRefererHeader(final ContainerRequestContext requestContext) throws IOException {

		final String origin = requestContext.getHeaderString("Origin");
		final String referer = requestContext.getHeaderString("Referer");

		LOG.debug("Origin = [{}], Referer=[{}]", origin, referer);

		if (StringUtils.isBlank(origin) && StringUtils.isBlank(referer)) {

			final String details = "Header Origin UND Referer fehlen";

			if (blockOnMissingOriginReferer) {

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

		if (targetOrigin != null) {

			List<String> allowedOrigins = Arrays.asList(targetOrigin.split(" "));

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
		LOG.warn("Possible CSRF-Attack: {} - {}", details, dump);
		throw new AuthException();
	}

}
