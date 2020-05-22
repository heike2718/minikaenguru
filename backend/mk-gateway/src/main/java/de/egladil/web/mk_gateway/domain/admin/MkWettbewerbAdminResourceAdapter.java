// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.admin;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.AbstractMkResourceAdapter;
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

	@Override
	protected String endpointName() {

		return "mk-wettbewerb-admin";
	}

}
