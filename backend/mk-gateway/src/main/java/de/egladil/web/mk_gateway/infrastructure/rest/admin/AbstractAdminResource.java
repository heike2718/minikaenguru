// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;

/**
 * AbstractAdminResource
 */
public abstract class AbstractAdminResource {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractAdminResource.class);

	@ConfigProperty(name = "admin.created.uri.prefix", defaultValue = "https://mathe-jung-alt.de/mk-gateway/admin")
	String createdUriPrefix;

	/**
	 * Wandelt locationString in URI um und dabei eine eventuelle URISyntaxException in eine MkWettbewerbAdminRuntimeException
	 *
	 * @param  locationString
	 * @return                URI
	 */
	protected URI createdUri(final String locationString) {

		try {

			return new URI(locationString);
		} catch (URISyntaxException e) {

			LOG.error("Fehlerhafte URI {}: {} ", locationString, e.getMessage(), e);
			throw new MkGatewayRuntimeException("Fehlerhafte URI: " + locationString, e);
		}
	}
}
