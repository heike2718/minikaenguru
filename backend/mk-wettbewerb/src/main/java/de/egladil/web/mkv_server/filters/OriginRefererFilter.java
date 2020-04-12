// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.egladil.web.mk_commons.filters.OriginRefererFilterDelegate;

/**
 * OriginRefererFilter
 */
@ApplicationScoped
@Provider
@PreMatching
@Priority(900)
public class OriginRefererFilter implements ContainerRequestFilter {

	private static final List<String> NO_CONTENT_PATHS = Arrays.asList(new String[] { "/favicon.ico" });

	@ConfigProperty(name = "block.on.missing.origin.referer", defaultValue = "false")
	boolean blockOnMissingOriginReferer;

	@ConfigProperty(name = "target.origin")
	String targetOrigin;

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {

		UriInfo uriInfo = requestContext.getUriInfo();
		String pathInfo = uriInfo.getPath();

		if ("OPTIONS".equals(requestContext.getMethod()) || NO_CONTENT_PATHS.contains(pathInfo)) {

			throw new NoContentException(pathInfo);
		}

		new OriginRefererFilterDelegate(blockOnMissingOriginReferer, targetOrigin).validateOriginAndRefererHeader(requestContext);
	}

}
