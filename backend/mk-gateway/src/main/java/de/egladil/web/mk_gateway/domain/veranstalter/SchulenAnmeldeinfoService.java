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
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DataInconsistencyRegistered;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;
import de.egladil.web.mk_gateway.domain.semantik.DomainService;
import de.egladil.web.mk_gateway.domain.teilnahmen.SchuleDetailsService;
import de.egladil.web.mk_gateway.domain.teilnahmen.SchulenOverviewService;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleDetails;

/**
 * SchulenAnmeldeinfoService
 */
@ApplicationScoped
@DomainService
public class SchulenAnmeldeinfoService {

	private static final Logger LOG = LoggerFactory.getLogger(SchulenAnmeldeinfoService.class);

	@Inject
	DomainEventHandler domainEventHandler;

	@Inject
	SchulenOverviewService schulenOverviewService;

	@Inject
	SchuleDetailsService schuleDetailsService;

	@Inject
	MkKatalogeResourceAdapter katalogeAdapter;

	private DataInconsistencyRegistered dataInconsistencyRegistered;

	static SchulenAnmeldeinfoService createForTest(final MkKatalogeResourceAdapter katalogeAdapter, final SchulenOverviewService schulenOverviewService, final SchuleDetailsService schuleDetailsService) {

		SchulenAnmeldeinfoService result = new SchulenAnmeldeinfoService();
		result.katalogeAdapter = katalogeAdapter;
		result.schulenOverviewService = schulenOverviewService;
		result.schuleDetailsService = schuleDetailsService;
		return result;

	}

	public List<SchuleAPIModel> findSchulenMitAnmeldeinfo(final String lehrerUUID) {

		List<SchuleAPIModel> schulenOfLehrer = this.schulenOverviewService
			.ermittleAnmeldedatenFuerSchulen(new Identifier(lehrerUUID));

		if (schulenOfLehrer == null || schulenOfLehrer.isEmpty()) {

			return new ArrayList<>();
		}

		List<String> kuerzel = schulenOfLehrer.stream().map(s -> s.kuerzel()).collect(Collectors.toList());

		final String kommaseparierteSchulkuerzel = StringUtils.join(kuerzel, ",");

		Response katalogItemsResponse = katalogeAdapter.findSchulen(kommaseparierteSchulkuerzel);

		if (katalogItemsResponse.getStatus() >= 400) {

			LOG.error("mk-kataloge: Status={}, beim Laden der Schulen - Lehrer-UUID={}",
				katalogItemsResponse.getStatus(), StringUtils.abbreviate(lehrerUUID, 11));

			throw new MkGatewayRuntimeException("Fehler beim Laden der Schulen aus dem Katalog");
		}

		final List<SchuleAPIModel> schulenAusKatalg = new SchuleKatalogResponseMapper()
			.getSchulenFromKatalogeAPI(katalogItemsResponse);

		return mergeDataFromSchulenOfLehrer(schulenAusKatalg, schulenOfLehrer);
	}

	/**
	 * Läd die Details für die Schule des gegebenen Lehrers aus den Katalogen und aus der Wettbewerbe-API.
	 *
	 * @param  schulkuerzel
	 * @param  lehrerId
	 *                      String UUID eines Lehrers.
	 * @return              SchuleAPIModel
	 */
	public SchuleAPIModel getSchuleWithWettbewerbsdetails(final String schulkuerzel, final String lehrerId) {

		Response katalogItemsResponse = katalogeAdapter.findSchulen(schulkuerzel);

		if (katalogItemsResponse.getStatus() >= 400) {

			LOG.error("mk-kataloge: Status={}, beim Laden der Schule - kuerzel={}, Lehrer-UUID={}",
				katalogItemsResponse.getStatus(), schulkuerzel, StringUtils.abbreviate(lehrerId, 11));

			throw new MkGatewayRuntimeException("Fehler beim Laden der Schulen aus dem Katalog");
		}

		final List<SchuleAPIModel> schulenAusKatalg = new SchuleKatalogResponseMapper()
			.getSchulenFromKatalogeAPI(katalogItemsResponse);

		SchuleAPIModel schuleAusKatalog = null;

		if (schulenAusKatalg.isEmpty()) {

			LOG.error("mk-kataloge: Status={}, Kein Katalogeintrag für Schule - kuerzel={}, Lehrer-UUID={}",
				katalogItemsResponse.getStatus(), schulkuerzel, StringUtils.abbreviate(lehrerId, 11));

			schuleAusKatalog = SchuleAPIModel.withKuerzel(schulkuerzel).markKatalogeintragUnknown();
		} else {

			schuleAusKatalog = schulenAusKatalg.get(0);
		}

		SchuleDetails schuleDetails = schuleDetailsService.ermittleSchuldetails(new Identifier(schulkuerzel),
			new Identifier(lehrerId));

		SchuleAPIModel result = SchuleAPIModel.merge(schuleAusKatalog, schuleDetails);

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

			this.dataInconsistencyRegistered = new LoggableEventDelegate().fireDataInconsistencyEvent(msg, domainEventHandler);
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

	DataInconsistencyRegistered getDataInconsistencyRegistered() {

		return dataInconsistencyRegistered;
	}

}
