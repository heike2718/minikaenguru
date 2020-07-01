// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.admin;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.AbstractMkResourceAdapter;
import de.egladil.web.mk_gateway.domain.apimodel.WettbewerbAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.WettbewerbID;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
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

	@Inject
	Event<SecurityIncidentRegistered> securityIncidentEvent;

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

			String msg = "Abfrage Wettbewerb mit jahr=null durch " + principalName;

			LOG.warn(msg);
			new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);

			throw new BadRequestException();
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

	public Response updateWettbewerb(final WettbewerbAPIModel data, final String principalName) {

		try {

			Response response = restClient.updateWettbewerb(data, principalName);
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
