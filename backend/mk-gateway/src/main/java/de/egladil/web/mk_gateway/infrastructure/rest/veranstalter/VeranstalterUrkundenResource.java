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
import de.egladil.web.mk_gateway.domain.urkunden.api.UrkundenauftragEinzelkind;

/**
 * VeranstalterUrkundenResource
 */
@RequestScoped
@Path("veranstalter/urkunden")
@Consumes(MediaType.APPLICATION_JSON)
public class VeranstalterUrkundenResource {

	@Context
	SecurityContext securityContext;

	@Inject
	EinzelkindUrkundenservice einzelkindUrkundenservice;

	/**
	 * Generiert eine einzelne Teilnehmerurkunde für das gegebene Kind, sofern es einen Lösungszettel hat.
	 *
	 * @param  kindUuid
	 *                        String UUID eines Kindes.
	 * @param  farbschemaname
	 *                        String Name eines bekannten Farbschemas.
	 * @return                Response mit einem DownloadData-Payload
	 */
	@POST
	@Path("urkunde")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response generiereEinzelurkunde(final UrkundenauftragEinzelkind urkundenauftrag) {

		Identifier veranstalterID = new Identifier(securityContext.getUserPrincipal().getName());

		DownloadData file = einzelkindUrkundenservice.generiereUrkunde(urkundenauftrag, veranstalterID);

		return MkGatewayFileUtils.createDownloadResponse(file);
	}

}
