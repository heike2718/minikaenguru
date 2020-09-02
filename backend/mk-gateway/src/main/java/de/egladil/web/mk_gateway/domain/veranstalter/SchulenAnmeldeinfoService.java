// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.apimodel.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DataInconsistencyRegistered;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;
import de.egladil.web.mk_gateway.domain.semantik.DomainService;

/**
 * SchulenAnmeldeinfoService
 */
@ApplicationScoped
@DomainService
public class SchulenAnmeldeinfoService {

	private static final Logger LOG = LoggerFactory.getLogger(SchulenAnmeldeinfoService.class);

	@Inject
	MkKatalogeResourceAdapter katalogeAdapter;

	@Inject
	MkWettbewerbResourceAdapter wettbewerbAdapter;

	@Inject
	Event<DataInconsistencyRegistered> dataInconsistencyEvent;

	private DataInconsistencyRegistered dataInconsistencyRegistered;

	static SchulenAnmeldeinfoService createForTest(final MkKatalogeResourceAdapter katalogeAdapter, final MkWettbewerbResourceAdapter wettbewerbAdapter) {

		SchulenAnmeldeinfoService result = new SchulenAnmeldeinfoService();
		result.katalogeAdapter = katalogeAdapter;
		result.wettbewerbAdapter = wettbewerbAdapter;
		return result;

	}

	public List<SchuleAPIModel> findSchulenMitAnmeldeinfo(final String lehrerUUID) {

		Response schulenWettbewerbResponse = wettbewerbAdapter.findSchulen(lehrerUUID);

		if (schulenWettbewerbResponse.getStatus() >= 400) {

			LOG.error("mk-wettbewerb: Status={}, beim Laden der Schulen - Lehrer-UUID={}",
				schulenWettbewerbResponse.getStatus(), StringUtils.abbreviate(lehrerUUID, 11));

			throw new MkGatewayRuntimeException("Fehler beim Laden der Schulen des Lehrers");
		}

		final List<SchuleAPIModel> schulenOfLehrer = this.getSchulenFromWettbewerbAPI(schulenWettbewerbResponse);

		List<String> kuerzel = schulenOfLehrer.stream().map(s -> s.kuerzel()).collect(Collectors.toList());

		final String kommaseparierteSchulkuerzel = StringUtils.join(kuerzel, ",");

		Response katalogItemsResponse = katalogeAdapter.findSchulen(kommaseparierteSchulkuerzel);

		if (katalogItemsResponse.getStatus() >= 400) {

			LOG.error("mk-kataloge: Status={}, beim Laden der Schulen - Lehrer-UUID={}",
				katalogItemsResponse.getStatus(), StringUtils.abbreviate(lehrerUUID, 11));

			throw new MkGatewayRuntimeException("Fehler beim Laden der Schulen aus dem Katalog");
		}

		final List<SchuleAPIModel> schulenAusKatalg = this.getSchulenFromKatalogeAPI(katalogItemsResponse);

		return mergeDataFromSchulenOfLehrer(schulenAusKatalg, schulenOfLehrer);
	}

	/**
	 * Läd die Details für die Schule des gegebenen Lehrers aus den Katalogen und aus der Wettbewerbe-API.
	 *
	 * @param  schulkuerzel
	 * @param  lehrerUUID
	 * @return              SchuleAPIModel
	 */
	public SchuleAPIModel getSchuleWithWettbewerbsdetails(final String schulkuerzel, final String lehrerUUID) {

		Response katalogItemsResponse = katalogeAdapter.findSchulen(schulkuerzel);

		if (katalogItemsResponse.getStatus() >= 400) {

			LOG.error("mk-kataloge: Status={}, beim Laden der Schule - kuerzel={}, Lehrer-UUID={}",
				katalogItemsResponse.getStatus(), schulkuerzel, StringUtils.abbreviate(lehrerUUID, 11));

			throw new MkGatewayRuntimeException("Fehler beim Laden der Schulen aus dem Katalog");
		}

		final List<SchuleAPIModel> schulenAusKatalg = this.getSchulenFromKatalogeAPI(katalogItemsResponse);

		SchuleAPIModel schuleAusKatalog = null;

		if (schulenAusKatalg.isEmpty()) {

			LOG.error("mk-kataloge: Status={}, Kein Katalogeintrag für Schule - kuerzel={}, Lehrer-UUID={}",
				katalogItemsResponse.getStatus(), schulkuerzel, StringUtils.abbreviate(lehrerUUID, 11));

			schuleAusKatalog = SchuleAPIModel.withKuerzel(schulkuerzel).markKatalogeintragUnknown();
		} else {

			schuleAusKatalog = schulenAusKatalg.get(0);
		}

		Response schuleWettbewerbDetailsResponse = wettbewerbAdapter.getSchuleDashboardModel(schulkuerzel, lehrerUUID);

		if (schuleWettbewerbDetailsResponse.getStatus() >= 400) {

			if (schuleWettbewerbDetailsResponse.getStatus() == 403) {

				LOG.warn("mk-wettbewerbe: Status={}, beim Laden der Schuldetails - kuerzel={}, Lehrer-UUID={}",
					schuleWettbewerbDetailsResponse.getStatus(), schulkuerzel, StringUtils.abbreviate(lehrerUUID, 11));

				throw new AccessDeniedException();

			} else {

				LOG.error("mk-wettbewerbe: Status={}, beim Laden der Schuldetails - kuerzel={}, Lehrer-UUID={}",
					schuleWettbewerbDetailsResponse.getStatus(), schulkuerzel, StringUtils.abbreviate(lehrerUUID, 11));

				throw new MkGatewayRuntimeException("Fehler beim Laden Schulwettbewerbdetails des Lehrers");
			}

		}

		SchuleAPIModel schuleAusWettbewerbAPI = this.getSchuleAusWettbewerbAPIResponse(schuleWettbewerbDetailsResponse);

		SchuleAPIModel result = SchuleAPIModel.merge(schuleAusKatalog, schuleAusWettbewerbAPI);

		return result;

	}

	List<SchuleAPIModel> mergeDataFromSchulenOfLehrer(final List<SchuleAPIModel> schulenAusKatalg, final List<SchuleAPIModel> schulenOfLehrer) {

		final List<SchuleAPIModel> nurLehrer = schulenAusKatalg.stream().filter(s -> schulenOfLehrer.contains(s))
			.collect(Collectors.toList());

		nurLehrer.stream().forEach(schule -> {

			Optional<SchuleAPIModel> opt = schulenOfLehrer.stream()
				.filter(ks -> ks.kuerzel().equals(schule.kuerzel())).findFirst();

			if (opt.isPresent()) {

				schule.withAngemeldet(opt.get().aktuellAngemeldet());
			}
		});

		if (nurLehrer.size() != schulenOfLehrer.size()) {

			String msg = "Nicht alle Schulen auf beiden Seiten gefunden: Kataloge: " + schulenAusKatalg.toString() + ", Lehrer: "
				+ schulenOfLehrer.toString();

			LOG.warn(msg);

			this.dataInconsistencyRegistered = new LoggableEventDelegate().fireDataInconsistencyEvent(msg, dataInconsistencyEvent);
		}

		if (schulenOfLehrer.size() > schulenAusKatalg.size()) {

			List<SchuleAPIModel> fehlendeLehrer = schulenOfLehrer.stream().filter(s -> !schulenAusKatalg.contains(s))
				.collect(Collectors.toList());

			fehlendeLehrer.forEach(s -> {

				s.markKatalogeintragUnknown();
				nurLehrer.add(s);
			});
		}

		return nurLehrer;
	}

	SchuleAPIModel getSchuleAusWettbewerbAPIResponse(final Response response) {

		ResponsePayload responsePayload = response.readEntity(ResponsePayload.class);

		MessagePayload messagePayload = responsePayload.getMessage();

		if (!messagePayload.isOk()) {

			return null;
		}

		try {

			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>) responsePayload.getData();

			return SchuleAPIModel.withAttributes(data);
		} catch (ClassCastException e) {

			LOG.error(e.getMessage(), e);
			throw new MkGatewayRuntimeException("Konnte ResponsePayload von mk-wettbewerbe nicht verarbeiten");

		}
	}

	List<SchuleAPIModel> getSchulenFromWettbewerbAPI(final Response response) {

		ResponsePayload responsePayload = response.readEntity(ResponsePayload.class);

		MessagePayload messagePayload = responsePayload.getMessage();

		List<SchuleAPIModel> result = new ArrayList<>();

		if (!messagePayload.isOk()) {

			return result;
		}

		try {

			@SuppressWarnings("unchecked")
			List<Map<String, Object>> data = (List<Map<String, Object>>) responsePayload.getData();

			for (Map<String, Object> keyValueMap : data) {

				result.add(SchuleAPIModel.withAttributes(keyValueMap));
			}

			return result;
		} catch (ClassCastException e) {

			LOG.error(e.getMessage(), e);
			throw new MkGatewayRuntimeException("Konnte ResponsePayload von mk-wettbewerbe nicht verarbeiten");

		}
	}

	List<SchuleAPIModel> getSchulenFromKatalogeAPI(final Response response) {

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

	DataInconsistencyRegistered getDataInconsistencyRegistered() {

		return dataInconsistencyRegistered;
	}

}
