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
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.auth.s2s.MkGatewayAuthConfig;
import de.egladil.web.mk_gateway.domain.error.MkGatewayWebApplicationException;
import de.egladil.web.mk_gateway.domain.feedback.ActivateFeedbackDelegate;
import de.egladil.web.mk_gateway.domain.feedback.scores.dto.AufgabenvorschauDto;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.infrastructure.restclient.MjaApiRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

/**
 * AufgabenVorschauService
 */
@ApplicationScoped
public class AufgabenVorschauService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AufgabenVorschauService.class);

	private final ActivateFeedbackDelegate activateFeedbackDelegate = new ActivateFeedbackDelegate();

	@Inject
	MkGatewayAuthConfig authConfig;

	@Context
	SecurityContext securityContext;

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	VeranstalterRepository veranstalterRepository;

	@RestClient
	@Inject
	MjaApiRestClient mjaApiRestClient;

	public AufgabenvorschauDto getAufgabenvorschauAktuellerWettbewerb(final Klassenstufe klassenstufe) {

		String veranstalterID = securityContext.getUserPrincipal().getName();

		Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(new Identifier(veranstalterID));

		if (optVeranstalter.isEmpty()) {

			LOGGER.warn("Unbekannter Veranstalter mit UUID={} greift auf Aufgabenvorschau zu.", veranstalterID);
			MessagePayload messagePayload = MessagePayload.error("keine Berechtigung!");
			Response response = Response.status(401).entity(messagePayload).build();
			throw new MkGatewayWebApplicationException(response);
		}

		Optional<Wettbewerb> optAktuellerWettbewerb = wettbewerbService.aktuellerWettbewerb();

		if (optAktuellerWettbewerb.isEmpty()) {

			LOGGER.error("Es gibt keinen aktuellen Wettbewerb");
			MessagePayload messagePayload = MessagePayload.error("aktueller Wettbewerb wurde nicht gefunden");
			Response response = Response.status(404).entity(messagePayload).build();
			throw new MkGatewayWebApplicationException(response);
		}

		Wettbewerb wettbewerb = optAktuellerWettbewerb.get();
		Veranstalter veranstalter = optVeranstalter.get();

		boolean canActivateFeedback = activateFeedbackDelegate.canActivateFeedback(wettbewerb.status(),
			veranstalter.zugangUnterlagen());

		if (!canActivateFeedback) {

			LOGGER.error("Bewertung nicht freigeschaltet: wettbewerb.status={}, veranstalter.zugangUnterlagen", wettbewerb.status(),
				veranstalter.zugangUnterlagen());
			MessagePayload messagePayload = MessagePayload.error("Bewertung nicht freigeschaltet");
			Response response = Response.status(400).entity(messagePayload).build();
			throw new MkGatewayWebApplicationException(response);
		}

		try {

			Response response = mjaApiRestClient.getAufgabenMinikaenguruwettbewerb(authConfig.client(),
				new String(Base64.getEncoder().encode(authConfig.header().getBytes())),
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
