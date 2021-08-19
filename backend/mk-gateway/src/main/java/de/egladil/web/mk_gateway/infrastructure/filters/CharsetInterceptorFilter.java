// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.filters;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CharsetInterceptorFilter
 */
// @Provider
public class CharsetInterceptorFilter implements ContainerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CharsetInterceptorFilter.class);

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {

		Object charsetProperty = requestContext.getProperty(InputPart.DEFAULT_CHARSET_PROPERTY);

		if (charsetProperty == null || !"UTF-8".equalsIgnoreCase(charsetProperty.toString())) {

			requestContext.setProperty(InputPart.DEFAULT_CHARSET_PROPERTY, "UTF-8");

			Object newCharsetProperty = requestContext.getProperty(InputPart.DEFAULT_CHARSET_PROPERTY);

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("{}:  DEFAULT_CHARSET_PROPERTY war {} und wurde zu {}", requestContext.getUriInfo().getAbsolutePath(),
					charsetProperty == null ? "null" : charsetProperty.toString(),
					newCharsetProperty == null ? "null" : newCharsetProperty.toString());
			}
		}
	}
}