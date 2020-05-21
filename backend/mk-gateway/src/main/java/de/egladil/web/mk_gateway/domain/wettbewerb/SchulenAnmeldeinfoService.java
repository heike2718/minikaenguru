// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.apimodel.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;
import de.egladil.web.mk_gateway.domain.semantik.DomainService;

/**
 * SchulenAnmeldeinfoService
 */
@ApplicationScoped
@DomainService
public class SchulenAnmeldeinfoService {

	@Inject
	MkKatalogeResourceAdapter katalogeAdapter;

	@Inject
	MkWettbewerbResourceAdapter wettbewerbAdapter;

	static SchulenAnmeldeinfoService createForTest(final MkKatalogeResourceAdapter katalogeAdapter, final MkWettbewerbResourceAdapter wettbewerbAdapter) {

		SchulenAnmeldeinfoService result = new SchulenAnmeldeinfoService();
		result.katalogeAdapter = katalogeAdapter;
		result.wettbewerbAdapter = wettbewerbAdapter;
		return result;

	}

	public List<SchuleAPIModel> findSchulenMitAnmeldeinfo(final String lehrerUUID) {

		Response schulenWettbewerbResponse = wettbewerbAdapter.findSchulen(lehrerUUID);

		if (schulenWettbewerbResponse.getStatus() >= 400) {

			throw new MkGatewayRuntimeException("Fehler beim Laden der Schulen des Lehrers");
		}

		final List<SchuleAPIModel> schulenOfLehrer = this.getSchulenFromWettbewerbAPI(schulenWettbewerbResponse);

		List<String> kuerzel = schulenOfLehrer.stream().map(s -> s.kuerzel()).collect(Collectors.toList());

		final String kommaseparierteSchulkuerzel = StringUtils.join(kuerzel, ",");

		Response katalogItemsResponse = katalogeAdapter.findSchulen(kommaseparierteSchulkuerzel);

		if (katalogItemsResponse.getStatus() >= 400) {

			throw new MkGatewayRuntimeException("Fehler beim Laden der Schulen aus dem Katalog");
		}

		final List<SchuleAPIModel> schulenAusKatalg = this.getSchulenFromKatalogeAPI(katalogItemsResponse);

		return mergeDataFromSchulenOfLehrer(schulenAusKatalg, schulenOfLehrer);
	}

	List<SchuleAPIModel> mergeDataFromSchulenOfLehrer(final List<SchuleAPIModel> schulenAusKatalg, final List<SchuleAPIModel> schulenOfLehrer) {

		schulenAusKatalg.stream().forEach(schule -> {

			Optional<SchuleAPIModel> opt = schulenOfLehrer.stream()
				.filter(ks -> ks.kuerzel().equals(schule.kuerzel())).findFirst();

			if (opt.isPresent()) {

				schule.withAngemeldet(opt.get().aktuellAngemeldet());
			}
		});

		return schulenAusKatalg;
	}

	private List<SchuleAPIModel> getSchulenFromWettbewerbAPI(final Response response) {

		ResponsePayload responsePayload = response.readEntity(ResponsePayload.class);

		MessagePayload messagePayliad = responsePayload.getMessage();

		List<SchuleAPIModel> result = new ArrayList<>();

		if (!messagePayliad.isOk()) {

			return result;
		}

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> data = (List<Map<String, Object>>) responsePayload.getData();

		for (Map<String, Object> keyValueMap : data) {

			result.add(new SchuleAPIModel().withAttributes(keyValueMap));
		}

		return result;

	}

	private List<SchuleAPIModel> getSchulenFromKatalogeAPI(final Response response) {

		List<SchuleAPIModel> result = new ArrayList<>();

		ResponsePayload responsePayload = response.readEntity(ResponsePayload.class);

		MessagePayload messagePayload = responsePayload.getMessage();

		if (messagePayload.isOk()) {

			@SuppressWarnings("unchecked")
			List<Map<String, Object>> data = (List<Map<String, Object>>) responsePayload.getData();

			for (Map<String, Object> keyValueMap : data) {

				result.add(new SchuleAPIModel().withAttributes(keyValueMap));
			}

		}

		return result;

	}

}
