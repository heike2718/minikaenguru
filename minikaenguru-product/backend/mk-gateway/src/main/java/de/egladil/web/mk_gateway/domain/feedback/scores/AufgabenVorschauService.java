// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.feedback.scores;

import java.util.Base64;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
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
import de.egladil.web.mk_gateway.infrastructure.restclient.RaetselbaukastenRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
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

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

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
	RaetselbaukastenRestClient raetselbaukastenRestClient;

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
			MessagePayload messagePayload = MessagePayload.error(applicationMessages.getString("aufgabenvorschau.error"));
			Response response = Response.status(404).entity(ResponsePayload.messageOnly(messagePayload)).build();
			throw new MkGatewayWebApplicationException(response);
		}

		Wettbewerb wettbewerb = optAktuellerWettbewerb.get();
		Veranstalter veranstalter = optVeranstalter.get();

		LOGGER.debug("Veranstalter {} ruft Feedbackbogen {} ab.", StringUtils.abbreviate(veranstalterID, 11), klassenstufe);

		boolean canActivateFeedback = activateFeedbackDelegate.canActivateFeedback(wettbewerb.status(),
			veranstalter.zugangUnterlagen());

		if (!canActivateFeedback) {

			LOGGER.error("Bewertung nicht freigeschaltet: wettbewerb.status={}, veranstalter.zugangUnterlagen", wettbewerb.status(),
				veranstalter.zugangUnterlagen());
			MessagePayload messagePayload = MessagePayload.error(applicationMessages.getString("aufgabenvorschau.error"));
			Response response = Response.status(400).entity(ResponsePayload.messageOnly(messagePayload)).build();
			throw new MkGatewayWebApplicationException(response);
		}

		try {

			String authHeader = new String(Base64.getEncoder().encode(authConfig.header().getBytes()));

			LOGGER.debug("about to call raetselbaukasten/api with params X-CLIENT-ID={}, auth-header={}, jahr={}, klasse={}",
				authConfig.client(),
				StringUtils.abbreviate(authHeader, 20), wettbewerb.id().toString(), klassenstufe);

			Response response = raetselbaukastenRestClient.getAufgabenMinikaenguruwettbewerb(authConfig.client(),
				authHeader,
				wettbewerb.id().toString(), klassenstufe);

			AufgabenvorschauDto result = response.readEntity(new GenericType<AufgabenvorschauDto>() {
			});

			LOGGER.debug("response status={}, anzahl aufgaben: ", response.getStatus(), result.getAufgaben().size());

			return result;

		} catch (WebApplicationException | ProcessingException e) {

			LOGGER.error("{} bei Kommunikation mit raetselbaukasten/api: {}", e.getClass().getSimpleName(), e.getMessage());
			MessagePayload messagePayload = MessagePayload
				.error(applicationMessages.getString("aufgabenvorschau.error"));
			Response response = Response.status(500).entity(ResponsePayload.messageOnly(messagePayload)).build();
			throw new MkGatewayWebApplicationException(response);
		}
	}
}
