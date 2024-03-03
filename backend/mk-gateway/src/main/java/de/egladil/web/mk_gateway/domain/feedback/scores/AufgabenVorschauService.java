// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.feedback.scores;

import java.util.Base64;
import java.util.Optional;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.mk_gateway.domain.auth.s2s.MkGatewayAuthConfig;
import de.egladil.web.mk_gateway.domain.error.MkGatewayWebApplicationException;
import de.egladil.web.mk_gateway.domain.feedback.scores.dto.AufgabenvorschauDto;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.infrastructure.restclient.MjaApiRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

/**
 * AufgabenVorschauService
 */
@ApplicationScoped
public class AufgabenVorschauService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AufgabenVorschauService.class);

	@Inject
	MkGatewayAuthConfig authConfig;

	@Inject
	WettbewerbService wettbewerbService;

	@RestClient
	@Inject
	MjaApiRestClient mjaApiRestClient;

	public AufgabenvorschauDto getAufgabenvorschauAktuellerWettbewerb(final Klassenstufe klassenstufe) {

		Optional<Wettbewerb> optAktuellerWettbewerb = wettbewerbService.aktuellerWettbewerb();

		if (optAktuellerWettbewerb.isEmpty()) {

			LOGGER.error("Es gibt keinen aktuellen Wettbewerb");
			MessagePayload messagePayload = MessagePayload.error("aktueller Wettbewerb wurde nicht gefunden");
			Response response = Response.status(404).entity(messagePayload).build();
			throw new MkGatewayWebApplicationException(response);
		}

		Wettbewerb wettbewerb = optAktuellerWettbewerb.get();

		if (!wettbewerb.status().isAllowedForScoring()) {

			LOGGER.error("Der Wettbewerb kann noch nicht bewertet werden: Status={}", wettbewerb.status());
			MessagePayload messagePayload = MessagePayload.error("Der Wettbewerb kann noch nicht bewertet werden");
			Response response = Response.status(400).entity(messagePayload).build();
			throw new MkGatewayWebApplicationException(response);
		}

		try {

			Response response = mjaApiRestClient.getAufgabenMinikaenguruwettbewerb(authConfig.client(),
				new String(Base64.getEncoder().encode(authConfig.header().getBytes())),
				wettbewerb.status(),
				wettbewerb.id().toString(), klassenstufe);

			AufgabenvorschauDto result = response.readEntity(new GenericType<AufgabenvorschauDto>() {
			});

			return result;

		} catch (ProcessingException e) {

			LOGGER.error("ProcessingException bei Kommunikation mit mja-api: {}", e.getMessage());
			MessagePayload messagePayload = MessagePayload.error("Aufgabenvorschau konnte nicht geladen werden");
			Response response = Response.status(500).entity(messagePayload).build();
			throw new MkGatewayWebApplicationException(response);

		}

	}
}
