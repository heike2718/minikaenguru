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

import de.egladil.web.mk_gateway.domain.AbstractMkResourceAdapter;
import de.egladil.web.mk_gateway.infrastructure.messaging.MkKatalogeRestClient;
import de.egladil.web.mk_gateway.infrastructure.messaging.MkKatalogeRestException;

/**
 * MkKatalogeResourceAdapter
 */
@ApplicationScoped
public class MkKatalogeResourceAdapter extends AbstractMkResourceAdapter {

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
		} catch (Exception e) {

			return handleException(e, LOG, "[findSchulen]");
		}
	}

	public Response loadLaender(final String secret) {

		try {

			Response response = restClient.loadLaender(secret);
			return response;

		} catch (Exception e) {

			return handleException(e, LOG, "[loadLaender]");
		}

	}

	public Response loadOrteInLand(final String kuerzel) {

		try {

			Response response = restClient.loadOrteInLand(kuerzel);
			return response;

		} catch (Exception e) {

			return handleException(e, LOG, "[loadOrteInLand]");
		}

	}

	public Response loadSchulenInOrt(final String kuerzel) {

		try {

			Response response = restClient.loadSchulenInOrt(kuerzel);
			return response;

		} catch (Exception e) {

			return handleException(e, LOG, "[loadSchulenInOrt]");
		}

	}

	public Response sucheItems(final String typ, final String searchTerm) {

		try {

			Response response = restClient.searchItems(typ, searchTerm);
			return response;

		} catch (Exception e) {

			return handleException(e, LOG, "[sucheItems]");
		}

	}

	@Override
	protected String endpointName() {

		return "mk-katalog-api";
	}
}
