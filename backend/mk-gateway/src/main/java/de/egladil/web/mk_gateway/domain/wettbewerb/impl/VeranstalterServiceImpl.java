// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.wettbewerb.VeranstalterService;
import de.egladil.web.mk_gateway.infrastructure.messaging.MkWettbewerbRestClient;

/**
 * VeranstalterServiceImpl
 */
@ApplicationScoped
public class VeranstalterServiceImpl implements VeranstalterService {

	private static final Logger LOG = LoggerFactory.getLogger(VeranstalterServiceImpl.class);

	@Inject
	@RestClient
	MkWettbewerbRestClient restClient;

	@Override
	public Response getTeilnahmenummern(final String uuid) {

		Response response = restClient.getTeilnahmenummern(uuid);
		return response;
	}

}
