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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;
import de.egladil.web.mk_gateway.domain.semantik.DomainService;
import de.egladil.web.mk_gateway.domain.statistik.AuswertungsmodusInfoService;
import de.egladil.web.mk_gateway.domain.teilnahmen.AktuelleTeilnahmeService;
import de.egladil.web.mk_gateway.domain.teilnahmen.SchuleDetailsService;
import de.egladil.web.mk_gateway.domain.teilnahmen.SchulenOverviewService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.veranstalter.api.Auswertungsmodus;
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
	AuthorizationService authorizationService;

	@Inject
	DomainEventHandler domainEventHandler;

	@Inject
	LoggableEventDelegate eventDelegate;

	@Inject
	SchulenOverviewService schulenOverviewService;

	@Inject
	SchuleDetailsService schuleDetailsService;

	@Inject
	MkKatalogeResourceAdapter katalogeAdapter;

	@Inject
	AuswertungsmodusInfoService auswertungsmodusInfoService;

	@Inject
	AktuelleTeilnahmeService aktuelleTeilnahmeService;

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

		String kontext = "[getSchuleDetails - " + schulkuerzel + "]";
		authorizationService.checkPermissionForTeilnahmenummerAndReturnRolle(new Identifier(lehrerId),
			new Identifier(schulkuerzel),
			kontext);

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

		Optional<Teilnahme> optAktuelleTeilnahme = aktuelleTeilnahmeService.aktuelleTeilnahme(schulkuerzel);

		if (optAktuelleTeilnahme.isPresent()) {

			Teilnahme teilnahme = optAktuelleTeilnahme.get();
			Auswertungsmodus auswertungsmodus = auswertungsmodusInfoService
				.ermittleAuswertungsmodusFuerTeilnahme(teilnahme.teilnahmeIdentifier());

			return result.withAngemeldet(true).withAuswertungsmodus(auswertungsmodus);
		}

		return result.withAuswertungsmodus(Auswertungsmodus.INDIFFERENT);

	}

	List<SchuleAPIModel> mergeDataFromSchulenOfLehrer(final List<SchuleAPIModel> schulenAusKatalg, final List<SchuleAPIModel> schulenOfLehrer) {

		final List<SchuleAPIModel> nurLehrer = schulenAusKatalg.stream().filter(s -> schulenOfLehrer.contains(s))
			.collect(Collectors.toList());

		nurLehrer.stream().forEach(schule -> {

			Optional<SchuleAPIModel> opt = schulenOfLehrer.stream()
				.filter(ks -> ks.kuerzel().equals(schule.kuerzel())).findFirst();

			if (opt.isPresent()) {

				SchuleAPIModel schuleAPIModel = opt.get();
				schule.withAngemeldet(schuleAPIModel.aktuellAngemeldet())
					.withAuswertungsmodus(schuleAPIModel.getAuswertungsmodus());
			}
		});

		if (nurLehrer.size() != schulenOfLehrer.size()) {

			String msg = "Nicht alle Schulen auf beiden Seiten gefunden: Kataloge: " + schulenAusKatalg.toString() + ", Lehrer: "
				+ schulenOfLehrer.toString();

			LOG.warn(msg);

			eventDelegate.fireDataInconsistencyEvent(msg, domainEventHandler);
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
}
