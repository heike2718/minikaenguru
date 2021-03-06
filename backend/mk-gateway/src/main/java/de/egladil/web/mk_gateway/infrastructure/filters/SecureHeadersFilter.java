// =====================================================
// Projekt: mkadmin-server
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_gateway.infrastructure.filters;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import de.egladil.web.mk_gateway.MkGatewayApp;
import de.egladil.web.mk_gateway.infrastructure.config.ConfigService;

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

		addCORSHeaders(headers);

		if (headers.get("Cache-Control") == null) {

			headers.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
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

	/**
	 * Theoretisch könnte man dies auch über die Quarkus-Konfigurationsprameter machen. Es hat sich aber herausgestellt, dass dies
	 * zu
	 * volatil ist und die Browser mit den Konstanten nicht gut zurecht kommen und CORS-Blockaden erzeugen. Daher bitte nicht in
	 * application.properties mit den Quarkus-CORS-Parametern konfigurieren, sondern hier.
	 *
	 * @param headers
	 */
	private void addCORSHeaders(final MultivaluedMap<String, Object> headers) {

		if (headers.get("Access-Control-Allow-Origin") == null) {

			headers.add("Access-Control-Allow-Origin", configService.getAllowedOrigin());
		}

		if (headers.get("Access-Control-Allow-Credentials") == null) {

			headers.add("Access-Control-Allow-Credentials", "true");
		}

		// Achtung: mod-security verbietet standardmäßig PUT und DELETE.
		// Daher parallel in /etc/apache2/sites-available/opa-wetterwachs.conf die rule 911100 für checklistenserver entfernen,
		// sonst bekommt man 403
		if (headers.get("Access-Control-Allow-Methods") == null) {

			headers.add("Access-Control-Allow-Methods", "POST, PUT, GET, HEAD, OPTIONS, DELETE");
		}

		if (headers.get("Access-Control-Allow-Headers") == null) {

			headers.add("Access-Control-Allow-Headers",
				"Content-Type, Accept, X-Requested-With, Content-Disposition, X-SESSIONID");
		}

		if (headers.get("Access-Control-Max-Age") == null) {

			headers.add("Access-Control-Max-Age", "3600");
		}

		if (headers.get("Access-Control-Expose-Headers") == null) {

			headers.add("Access-Control-Expose-Headers",
				"Content-Type, Content-Disposition, X-SESSIONID");
		}
	}
}
