// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.messaging;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MkWettbewerbResponseExceptionMapper
 */
@Provider
public class MkWettbewerbResponseExceptionMapper implements ResponseExceptionMapper<MkWettbewerbRestException> {

	private static final Logger LOG = LoggerFactory.getLogger(MkWettbewerbResponseExceptionMapper.class);

	@Override
	public MkWettbewerbRestException toThrowable(final Response response) {

		int status = response.getStatus();
		String path = response.getLocation().getPath();

		String msg = "Client answered with status=" + status + " to call " + path;
		LOG.error(msg);

		return new MkWettbewerbRestException(msg);
	}

}
