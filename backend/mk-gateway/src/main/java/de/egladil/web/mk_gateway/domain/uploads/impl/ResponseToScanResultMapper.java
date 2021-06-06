// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.impl;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.infrastructure.upload.ScanResult;

/**
 * ResponseToScanResultMapper
 */
public class ResponseToScanResultMapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResponseToScanResultMapper.class);

	public ScanResult getScanResult(final Response response) {

		ResponsePayload responsePayload = response.readEntity(ResponsePayload.class);

		try {

			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>) responsePayload.getData();

			return ScanResult.withAttributes(data);

		} catch (ClassCastException e) {

			LOGGER.error(e.getMessage(), e);
			throw new MkGatewayRuntimeException("Konnte ResponsePayload von filescanner nicht verarbeiten");
		}
	}

}
