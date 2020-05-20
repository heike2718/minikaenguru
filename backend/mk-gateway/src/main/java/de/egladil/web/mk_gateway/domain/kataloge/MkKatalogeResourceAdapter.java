// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kataloge;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.infrastructure.messaging.MkKatalogeRestClient;
import de.egladil.web.mk_gateway.infrastructure.messaging.MkKatalogeRestException;

/**
 * MkKatalogeResourceAdapter
 */
@ApplicationScoped
public class MkKatalogeResourceAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(MkKatalogeResourceAdapter.class);

	@Inject
	@RestClient
	MkKatalogeRestClient restClient;

	/**
	 * @param  kommaseparierteSchulkuerzel
	 * @return
	 */
	public Response findSchulen(final String kommaseparierteSchulkuerzel) throws MkKatalogeRestException {

		try {

			Response response = restClient.findSchulenMitKuerzeln(kommaseparierteSchulkuerzel);
			return response;
		} catch (MkKatalogeRestException e) {

			LOG.error("Konnte Schulen nicht laden: " + e.getMessage());
			throw e;
		}
	}

}
