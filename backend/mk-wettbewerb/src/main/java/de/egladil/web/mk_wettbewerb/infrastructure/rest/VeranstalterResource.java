// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.rest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_wettbewerb.MkvServerApp;
import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.personen.Veranstalter;
import de.egladil.web.mk_wettbewerb.domain.personen.VeranstalterRepository;

/**
 * VeranstalterResource
 */
@Path("/veranstalter")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VeranstalterResource {

	private static final Logger LOG = LoggerFactory.getLogger(VeranstalterResource.class);

	@Inject
	VeranstalterRepository veranstalterRepository;

	@GET
	@Path("/teilnahmenummern")
	public Response getTeilnahmenummern(@HeaderParam(value = MkvServerApp.UUID_HEADER_NAME) final String uuid) {

		LOG.info("Holen teilnahmenummern für " + uuid);

		Optional<Veranstalter> opt = veranstalterRepository.ofId(new Identifier(uuid));

		if (opt.isPresent()) {

			Veranstalter veranstalter = opt.get();

			List<String> teilnahmenummern = veranstalter.teilnahmeIdentifier().stream().map(id -> id.identifier())
				.collect(Collectors.toList());

			ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), StringUtils.join(teilnahmenummern, ","));
			return Response.ok(responsePayload).build();
		}

		LOG.warn("kein Veranstalter mit UUID={} vorhanden", uuid);
		return Response.status(404)
			.entity(ResponsePayload.messageOnly(MessagePayload.error("Konnte die Teilnahmenummern nicht ermitteln."))).build();
	}

}
