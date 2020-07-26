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
import de.egladil.web.mk_gateway.domain.apimodel.FileResource;
import de.egladil.web.mk_gateway.domain.apimodel.LandPayload;
import de.egladil.web.mk_gateway.domain.apimodel.OrtPayload;
import de.egladil.web.mk_gateway.domain.apimodel.SchulePayload;
import de.egladil.web.mk_gateway.infrastructure.messaging.MkKatalogeRestClient;

/**
 * MkKatalogeResourceAdapter
 */
@ApplicationScoped
public class MkKatalogeResourceAdapter extends AbstractMkResourceAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(MkKatalogeResourceAdapter.class);

	@Inject
	@RestClient
	MkKatalogeRestClient restClient;

	public Response findItems(final String katalogTyp, final String searchTerm) {

		try {

			Response response = restClient.findItems(katalogTyp, searchTerm);
			return response;
		} catch (Exception e) {

			return handleException(e, LOG, "[findItems]");
		}
	}

	public Response findOrteInLand(final String landKuerzel, final String searchTerm) {

		try {

			Response response = restClient.findOrteInLand(landKuerzel, searchTerm);
			return response;
		} catch (Exception e) {

			return handleException(e, LOG, "[findOrteInLand]");
		}

	}

	public Response findSchulenInOrt(final String ortkuerzel, final String searchTerm) {

		try {

			Response response = restClient.findSchulenInOrt(ortkuerzel, searchTerm);
			return response;
		} catch (Exception e) {

			return handleException(e, LOG, "[findSchulenInOrt]");
		}
	}

	/**
	 * @param  kommaseparierteSchulkuerzel
	 * @return
	 */
	public Response findSchulen(final String kommaseparierteSchulkuerzel) {

		try {

			Response response = restClient.findSchulenMitKuerzeln(kommaseparierteSchulkuerzel);
			return response;
		} catch (Exception e) {

			return handleException(e, LOG, "[findSchulen]");
		}
	}

	public Response loadLaender(final String uuid, final String secret) {

		try {

			Response response = restClient.loadLaender(uuid, secret);
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

	public Response renameLand(final String uuid, final String secret, final LandPayload payload) {

		try {

			Response response = restClient.renameLand(uuid, secret, payload);
			return response;

		} catch (Exception e) {

			return handleException(e, LOG, "[renameLand]");
		}
	}

	public Response renameOrt(final String uuid, final String secret, final OrtPayload payload) {

		try {

			Response response = restClient.renameOrt(uuid, secret, payload);

			LOG.debug("Ort umbenannt: {}", payload);
			return response;

		} catch (Exception e) {

			return handleException(e, LOG, "[renameOrt]");
		}
	}

	public Response renameSchule(final String uuid, final String secret, final SchulePayload payload) {

		try {

			Response response = restClient.renameSchule(uuid, secret, payload);
			return response;

		} catch (Exception e) {

			return handleException(e, LOG, "[renameSchule]");
		}
	}

	public Response createSchule(final String uuid, final String secret, final SchulePayload payload) {

		try {

			Response response = restClient.createSchule(uuid, secret, payload);
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

	@Deprecated(forRemoval = true)
	public Response uploadSchulkatalog(final String secret, final FileResource input) {

		try {

			Response response = restClient.uploadSchulkatalog(secret, input);
			return response;

		} catch (Exception e) {

			return handleException(e, LOG, "[uploadSchulkatalog]");
		}

	}

	@Override
	protected String endpointName() {

		return "mk-kataloge";
	}
}
