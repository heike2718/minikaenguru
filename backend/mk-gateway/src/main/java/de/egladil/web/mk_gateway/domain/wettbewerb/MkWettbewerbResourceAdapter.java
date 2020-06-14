// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.AbstractMkResourceAdapter;
import de.egladil.web.mk_gateway.infrastructure.messaging.MkWettbewerbRestClient;
import de.egladil.web.mk_gateway.infrastructure.messaging.MkWettbewerbRestException;

/**
 * MkWettbewerbResourceAdapter
 */
@ApplicationScoped
public class MkWettbewerbResourceAdapter extends AbstractMkResourceAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(MkWettbewerbResourceAdapter.class);

	@Inject
	@RestClient
	MkWettbewerbRestClient restClient;

	/**
	 * Läd die Details fürs Schule-Dashboard.
	 *
	 * @param  schulkuerzel
	 * @param  uuid
	 * @return
	 */
	public Response getSchuleDashboardModel(final String schulkuerzel, final String uuid) {

		try {

			Response response = restClient.getSchuleDetails(schulkuerzel, uuid);
			return response;
		} catch (Exception e) {

			return handleException(e, LOG, "[getSchuleDashboardModel]");
		}

	}

	/**
	 * Läd die Schulen des gegebenen Lehrers.
	 *
	 * @param  commaseparatedSchulkuerzel
	 * @param  uuid
	 *                                    String UUID eines Lehrers.
	 * @return                            Response
	 */
	public Response findSchulen(final String uuid) throws MkWettbewerbRestException {

		try {

			Response response = restClient.findSchulen(uuid);
			return response;
		} catch (Exception e) {

			return handleException(e, LOG, "[findSchulen]");
		}
	}

	public Response getAktuellenWettbewerb() throws MkWettbewerbRestException {

		try {

			Response response = restClient.getAktuellenWettbewerb();
			return response;
		} catch (Exception e) {

			return handleException(e, LOG, "[getAktuellenWettbewerb]");
		}

	}

	public Response getStatusZugangUnterlagen(final String uuid) {

		try {

			Response response = restClient.getStatusZugangUnterlagen(uuid);
			return response;
		} catch (Exception e) {

			return handleException(e, LOG, "[getStatusZugangUnterlagen]");
		}

	}

	@Override
	protected String endpointName() {

		return "mk-wettbewerb";
	}

}
