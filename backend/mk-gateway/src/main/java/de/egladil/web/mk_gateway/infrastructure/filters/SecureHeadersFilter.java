// =====================================================
// Projekt: mkadmin-server
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_gateway.infrastructure.filters;

import java.io.IOException;

import de.egladil.web.mk_gateway.MkGatewayApp;
import de.egladil.web.mk_gateway.infrastructure.config.ConfigService;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

/**
 * SecureHeadersFilter packt die SecureHeaders in den Response.
 */
@Provider
public class SecureHeadersFilter implements ContainerResponseFilter {

	private static final String CONTENT_SECURITY_POLICY = "Content-Security-Policy";

	@Inject
	ConfigService configService;

	@Override
	public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {

		final MultivaluedMap<String, Object> headers = responseContext.getHeaders();

		// addCORSHeaders(headers);

		if (headers.get("Cache-Control") == null) {

			headers.add("Cache-Control", "no-store, no-cache, must-revalidate max-age=0");
		}

		if (headers.get("Pragma") == null) {

			headers.add("Pragma", "no-cache");
		}

		if (headers.get("expires") == null) {

			headers.add("expires", "0");
		}

		if (headers.get("X-Content-Type-Options") == null) {

			headers.add("X-Content-Type-Options", "nosniff");
		}

		if (headers.get("X-Frame-Options") == null) {

			headers.add("X-Frame-Options", "DENY");
		}

		if (headers.get("Server") == null) {

			headers.add("Server", "Hex oder HAL");
		}

		if (headers.get("X-Powered-By") == null) {

			headers.add("X-Powered-By", "Ponder Stibbons");
		}

		if (headers.get("Vary") == null) {

			headers.add("Vary", "Origin");
		}

		if (headers.get(CONTENT_SECURITY_POLICY) == null) {

			responseContext.getHeaders().add(CONTENT_SECURITY_POLICY, "default-src 'self'; ");
		}

		if (!MkGatewayApp.STAGE_DEV.equals(configService.getStage()) && headers.get("Strict-Transport-Security") == null) {

			headers.add("Strict-Transport-Security", "max-age=63072000; includeSubdomains");

		}

		if (headers.get("X-XSS-Protection") == null) {

			headers.add("X-XSS-Protection", "1; mode=block");
		}

		if (headers.get("X-Frame-Options") == null) {

			headers.add("X-Frame-Options", "deny");
		}
	}
}
