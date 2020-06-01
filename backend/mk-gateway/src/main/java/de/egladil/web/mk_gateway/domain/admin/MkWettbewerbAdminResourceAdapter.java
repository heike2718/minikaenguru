// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.admin;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.AbstractMkResourceAdapter;
import de.egladil.web.mk_gateway.domain.apimodel.WettbewerbAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.messaging.MkWettbewerbAdminRestClient;

/**
 * MkWettbewerbAdminResourceAdapter
 */
@ApplicationScoped
public class MkWettbewerbAdminResourceAdapter extends AbstractMkResourceAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(MkWettbewerbAdminResourceAdapter.class);

	@Inject
	@RestClient
	MkWettbewerbAdminRestClient restClient;

	public Response loadWettbewerbe(final String principalName) {

		try {

			Response response = restClient.loadWettbewerbe(principalName);
			return response;
		} catch (Exception e) {

			return this.handleException(e, LOG, "[loadWettbewerbe]");
		}
	}

	public Response wettbewerbMitJahr(final Integer jahr, final String principalName) {

		if (jahr == null) {

			LOG.warn("Aufruf mit jahr = null. Geben 404 zurück");
			return Response.status(Status.NOT_FOUND)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("kein Wettbwerb mit Jahr null bekannt"))).build();
		}

		try {

			Response response = restClient.getWettbewerbMitJahr(jahr, principalName);
			return response;
		} catch (Exception e) {

			return this.handleException(e, LOG, "[wettbewerbMitJahr]");
		}

	}

	public Response createWettbewerb(final WettbewerbAPIModel data, final String principalName) {

		try {

			Response response = restClient.createWettbewerb(data, principalName);
			return response;
		} catch (Exception e) {

			return this.handleException(e, LOG, "[createWettbewerb]");
		}
	}

	public Response moveWettbwerbOn(final WettbewerbID data, final String principalName) {

		try {

			Response response = restClient.moveWettbewerbOn(data, principalName);
			return response;
		} catch (Exception e) {

			return this.handleException(e, LOG, "[moveWettbwerbOn]");
		}
	}

	@Override
	protected String endpointName() {

		return "mk-wettbewerb-admin";
	}

}
