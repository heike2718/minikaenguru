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
import de.egladil.web.mk_gateway.domain.apimodel.NeueSchulePayload;
import de.egladil.web.mk_gateway.domain.apimodel.RenameKatalogItemPayload;
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

	public Response renameLand(final String kuerzel, final RenameKatalogItemPayload payload, final String secret) {

		try {

			Response response = restClient.renameLand(kuerzel, payload, secret);
			return response;

		} catch (Exception e) {

			return handleException(e, LOG, "[renameLand]");
		}
	}

	public Response renameOrt(final String kuerzel, final RenameKatalogItemPayload payload, final String secret) {

		try {

			Response response = restClient.renameOrt(kuerzel, payload, secret);
			return response;

		} catch (Exception e) {

			return handleException(e, LOG, "[renameOrt]");
		}
	}

	public Response renameSchule(final String kuerzel, final RenameKatalogItemPayload payload, final String secret) {

		try {

			Response response = restClient.renameSchule(kuerzel, payload, secret);
			return response;

		} catch (Exception e) {

			return handleException(e, LOG, "[renameSchule]");
		}
	}

	public Response createSchule(final NeueSchulePayload payload, final String secret) {

		try {

			Response response = restClient.createSchule(payload, secret);
			return response;

		} catch (Exception e) {

			return handleException(e, LOG, "[renameSchule]");
		}
	}

	public Response generateKuerzel(final String secret) {

		try {

			Response response = restClient.generateKuerzel(secret);
			return response;

		} catch (Exception e) {

			return handleException(e, LOG, "[generateKuerzel]");
		}
	}

	@Override
	protected String endpointName() {

		return "mk-katalog-api";
	}
}
