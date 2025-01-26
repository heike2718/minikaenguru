// =====================================================
// Project: bv-admin
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_gateway.infrastructure.filters;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class LoggingClientRequestFilter implements ClientRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingClientRequestFilter.class);

	@Override
	public void filter(final ClientRequestContext requestContext) throws IOException {

		LOGGER.info(">>>>> Request URL: {} <<<<<", requestContext.getUri());
	}
}
