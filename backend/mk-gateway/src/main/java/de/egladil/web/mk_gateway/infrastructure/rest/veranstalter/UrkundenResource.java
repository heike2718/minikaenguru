// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.urkunden.EinzelkindUrkundenservice;
import de.egladil.web.mk_gateway.domain.urkunden.SchuleUrkundenservice;
import de.egladil.web.mk_gateway.domain.urkunden.api.UrkundenauftragEinzelkind;
import de.egladil.web.mk_gateway.domain.urkunden.api.UrkundenauftragSchule;

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

		Identifier veranstalterID = new Identifier(securityContext.getUserPrincipal().getName());

		DownloadData file = schulurkundenservice.generiereSchulauswertung(urkundenauftrag, veranstalterID);

		return MkGatewayFileUtils.createDownloadResponse(file);
	}
}
