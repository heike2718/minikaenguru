// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.apimodel.veranstalter.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;

/**
 * SchuleKatalogResponseMapperTest
 */
public class SchuleKatalogResponseMapper {

	private static final Logger LOG = LoggerFactory.getLogger(SchuleKatalogResponseMapper.class);

	public List<SchuleAPIModel> getSchulenFromKatalogeAPI(final Response response) {

		List<SchuleAPIModel> result = new ArrayList<>();

		ResponsePayload responsePayload = response.readEntity(ResponsePayload.class);

		MessagePayload messagePayload = responsePayload.getMessage();

		if (messagePayload.isOk()) {

			try {

				@SuppressWarnings("unchecked")
				List<Map<String, Object>> data = (List<Map<String, Object>>) responsePayload.getData();

				for (Map<String, Object> keyValueMap : data) {

					result.add(SchuleAPIModel.withAttributes(keyValueMap));
				}
			} catch (ClassCastException e) {

				LOG.error(e.getMessage(), e);
				throw new MkGatewayRuntimeException("Konnte ResponsePayload von mk-kataloge nicht verarbeiten");

			}

		}

		return result;

	}

}
