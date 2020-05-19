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

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.infrastructure.messaging.MkWettbewerbRestClient;
import de.egladil.web.mk_gateway.infrastructure.messaging.MkWettbewerbRestException;

/**
 * MkWettbewerbResourceAdapter
 */
@ApplicationScoped
public class MkWettbewerbResourceAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(MkWettbewerbResourceAdapter.class);

	@Inject
	@RestClient
	MkWettbewerbRestClient restClient;

	public Response getTeilnahmenummern(final String uuid) {

		try {

			Response response = restClient.getTeilnahmenummern(uuid);
			return response;
		} catch (MkWettbewerbRestException e) {

			LOG.error("Konnte Teilnahmenummern nicht laden: " + e.getMessage());
			return Response.serverError()
				.entity(ResponsePayload
					.messageOnly(MessagePayload.error("unerwarteter Fehler beim Auruf des mk-wettbewerb-endpoints aufgetreten")))
				.build();
		}
	}

	public Response getSchuleDashboardModel(final String schulkuerzel, final String uuid) {

		try {

			Response response = restClient.getSchuleDetails(schulkuerzel, uuid);
			return response;
		} catch (MkWettbewerbRestException e) {

			LOG.error("Konnte SchulDashboardModels nicht laden: " + e.getMessage());
			return Response.serverError()
				.entity(ResponsePayload
					.messageOnly(MessagePayload.error("unerwarteter Fehler beim Auruf des mk-wettbewerb-Endpoints aufgetreten")))
				.build();
		}

	}

}
