// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.urkunden.EinzelkindUrkundenservice;
import de.egladil.web.mk_gateway.domain.urkunden.SchuleUrkundenservice;
import de.egladil.web.mk_gateway.domain.urkunden.api.UrkundenauftragEinzelkind;
import de.egladil.web.mk_gateway.domain.urkunden.api.UrkundenauftragSchule;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * UrkundenResource
 */
@RequestScoped
@Path("urkunden")
@Consumes(MediaType.APPLICATION_JSON)
public class UrkundenResource {

	@Context
	SecurityContext securityContext;

	@Inject
	EinzelkindUrkundenservice einzelkindUrkundenservice;

	@Inject
	SchuleUrkundenservice schulurkundenservice;

	@Inject
	DevDelayService delayService;

	/**
	 * Generiert eine einzelne Teilnehmerurkunde für das gegebene Kind, sofern es einen Lösungszettel hat.
	 *
	 * @param  urkundenauftrag
	 *                         UrkundenauftragEinzelkind
	 * @return                 Response mit einem DownloadData-Payload
	 */
	@POST
	@Path("urkunde")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response generiereEinzelurkunde(final UrkundenauftragEinzelkind urkundenauftrag) {

		this.delayService.pause();

		Identifier veranstalterID = new Identifier(securityContext.getUserPrincipal().getName());

		DownloadData file = einzelkindUrkundenservice.generiereUrkunde(urkundenauftrag, veranstalterID);

		return MkGatewayFileUtils.createDownloadResponse(file);
	}

	/**
	 * Generiert Schulauswertung mit Urkunden und allem.
	 *
	 * @param  urkundenauftrag
	 *                         UrkundenauftragSchule
	 * @return                 Response mit einem DownloadData-Payload
	 */
	@POST
	@Path("schule")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response generiereSchulauswertung(final UrkundenauftragSchule urkundenauftrag) {

		this.delayService.pause();

		Identifier veranstalterID = new Identifier(securityContext.getUserPrincipal().getName());

		DownloadData file = schulurkundenservice.generiereSchulauswertung(urkundenauftrag, veranstalterID);

		return MkGatewayFileUtils.createDownloadResponse(file);
	}
}
