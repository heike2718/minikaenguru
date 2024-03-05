// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kataloge;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.AbstractMkResourceAdapter;
import de.egladil.web.mk_gateway.domain.apimodel.StringsAPIModel;
import de.egladil.web.mk_gateway.domain.kataloge.api.LandPayload;
import de.egladil.web.mk_gateway.domain.kataloge.api.OrtPayload;
import de.egladil.web.mk_gateway.domain.kataloge.api.SchulePayload;
import de.egladil.web.mk_gateway.domain.kataloge.api.SchulkatalogAntrag;
import de.egladil.web.mk_gateway.infrastructure.restclient.MkKatalogeRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

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

	public Response sendeSchulkatalogAntrag(final SchulkatalogAntrag antrag) {

		try {

			Response response = restClient.sendeSchulkatalogAntrag(antrag);
			return response;

		} catch (Exception e) {

			return handleException(e, LOG, "[sendeSchulkatalogAntrag]");
		}
	}

	public Response getHeartbeat(final String heartbeatSecret) {

		try {

			Response response = restClient.checkKataloge(heartbeatSecret);
			return response;
		} catch (Exception e) {

			return handleException(e, LOG, "[getHeartbeat]");
		}

	}

	public Response loadSchulen(final StringsAPIModel schulkuerzel) {

		try {

			String kommaseparierteKuerzel = StringUtils.join(schulkuerzel.getStrings(), ",");
			Response response = restClient.loadSchulenMitKuerzeln(kommaseparierteKuerzel);
			return response;

		} catch (Exception e) {

			return handleException(e, LOG, "[getHeartbeat]");
		}

	}

	@Override
	protected String endpointName() {

		return "mk-kataloge";
	}
}
