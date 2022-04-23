// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kataloge;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.StringsAPIModel;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.kataloge.api.SchulePayload;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.veranstalter.SchuleKatalogResponseMapper;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;

/**
 * SchulkatalogService
 */
@ApplicationScoped
public class SchulkatalogService {

	private static final Logger LOG = LoggerFactory.getLogger(SchulkatalogService.class);

	@Inject
	MkKatalogeResourceAdapter katalogeResourceAdapter;

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	public static final SchulkatalogService createForTest(final MkKatalogeResourceAdapter katalogeResourceAdapter) {

		SchulkatalogService result = new SchulkatalogService();
		result.katalogeResourceAdapter = katalogeResourceAdapter;
		return result;
	}

	/**
	 * Fragt beim mk-kataloge-Microservice nach Daten der Schule mit gegebenem schulkuerzel. Exceptions weren nur geloggt.
	 *
	 * @param  schulkuerzel
	 * @return              Optional
	 */
	public Optional<SchuleAPIModel> findSchuleQuietly(final String schulkuerzel) {

		Response katalogeResponse = null;

		try {

			katalogeResponse = katalogeResourceAdapter.findSchulen(schulkuerzel);

			List<SchuleAPIModel> trefferliste = new SchuleKatalogResponseMapper().getSchulenFromKatalogeAPI(katalogeResponse);

			return trefferliste.isEmpty() ? Optional.empty() : Optional.of(trefferliste.get(0));

		} catch (MkGatewayRuntimeException e) {

			LOG.warn("Können Schule nicht ermitteln: {}", e.getMessage());
			return Optional.empty();
		} finally {

			if (katalogeResponse != null) {

				katalogeResponse.close();
			}
		}
	}

	/**
	 * Fragt beim mk-kataloge-Microservice nach Daten der Schulen mit den gegebenen kuerzeln. Exceptions weren nur geloggt.
	 *
	 * @param  schulkuerzel
	 * @return
	 */
	public List<SchuleAPIModel> loadSchulenQuietly(final StringsAPIModel schulkuerzel) {

		Response katalogeResponse = null;

		try {

			katalogeResponse = katalogeResourceAdapter.loadSchulen(schulkuerzel);

			katalogeResponse.bufferEntity();

			List<SchuleAPIModel> trefferliste = new SchuleKatalogResponseMapper().getSchulenFromKatalogeAPI(katalogeResponse);

			return trefferliste;

		} catch (MkGatewayRuntimeException e) {

			LOG.warn("Können Schule nicht ermitteln: {}", e.getMessage());
			return new ArrayList<>();
		} finally {

			if (katalogeResponse != null) {

				katalogeResponse.close();
			}
		}
	}

	public Response renameSchule(final String uuid, final String secret, final SchulePayload payload) {

		Response response = katalogeResourceAdapter.renameSchule(uuid, secret, payload);

		if (response.getStatus() == 200) {

			boolean umbenannt = changeSchulteilnahmeQuietly(uuid, payload);

			if (!umbenannt) {

				ResponsePayload responsePayload = ResponsePayload
					.messageOnly(MessagePayload.warn("Umbenennung im Schulkatalog erfolgreich, aber in Schulteilnahme nicht"));
				return Response.ok(responsePayload).build();
			}

		}

		return response;

	}

	@Transactional
	boolean changeSchulteilnahmeQuietly(final String uuid, final SchulePayload payload) {

		try {

			Optional<Wettbewerb> optWettbewerb = wettbewerbService.aktuellerWettbewerb();

			if (optWettbewerb.isPresent()) {

				Wettbewerb wettbewerb = optWettbewerb.get();

				if (WettbewerbStatus.ANMELDUNG == wettbewerb.status() || WettbewerbStatus.DOWNLOAD_LEHRER == wettbewerb.status()
					|| WettbewerbStatus.DOWNLOAD_PRIVAT == wettbewerb.status()) {

					TeilnahmeIdentifier eilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
						.withTeilnahmenummer(payload.kuerzel()).withWettbewerbID(wettbewerb.id());

					Optional<Teilnahme> optTeilnahme = teilnahmenRepository.ofTeilnahmeIdentifier(eilnahmeIdentifier);

					if (optTeilnahme.isPresent()) {

						Schulteilnahme geaenderte = new Schulteilnahme(wettbewerb.id(), new Identifier(payload.kuerzel()),
							payload.name(), new Identifier(uuid));

						teilnahmenRepository.changeTeilnahme(geaenderte);
					}

				}
			}

			return true;

		} catch (Exception e) {

			LOG.error("Schulteilnahme mit Teilnahmenummer " + payload.kuerzel() + ": Schulname wurde nicht umbenannt");
			return false;

		}

	}

}
