// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.unterlagen.UnterlagenService;

/**
 * UnterlagenResource
 */
@RequestScoped
@Path("unterlagen")
@Consumes(MediaType.APPLICATION_JSON)
public class UnterlagenResource {

	private static final Logger LOG = LoggerFactory.getLogger(UnterlagenResource.class);

	@Context
	SecurityContext securityContext;

	@Inject
	UnterlagenService unterlagenService;

	/**
	 * Läd die Unterlagen für Schulen herunter.
	 *
	 * @param  sprache
	 *                 String
	 *                 der Teil aus der enum Sprache, der festlegt, in welcher Sprache die Unterlagen heruntergeladen werden sollen.
	 * @return         Response mit einem DownloadData-Payload
	 */
	@GET
	@Path("schulen/{sprache}")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response getUnterlagenSchule(@PathParam(value = "sprache") final String sprache) {

		Sprache theSprache = null;

		try {

			theSprache = Sprache.valueOf(sprache);
		} catch (IllegalArgumentException e) {

			LOG.error(e.getMessage());

			throw new NotFoundException();
		}

		Identifier veranstalterID = new Identifier(securityContext.getUserPrincipal().getName());

		DownloadData file = unterlagenService.getUnterlagenFuerSchule(veranstalterID, theSprache);

		return MkGatewayFileUtils.createDownloadResponse(file);
	}

	/**
	 * Läd die Unterlagen für Schulen herunter.
	 *
	 * @param  sprache
	 *                 String
	 *                 der Teil aus der enum Sprache, der festlegt, in welcher Sprache die Unterlagen heruntergeladen werden sollen.
	 * @return         Response mit einem DownloadData-Payload
	 */
	@GET
	@Path("privat/{sprache}")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response getUnterlagenPrivat(@PathParam(value = "sprache") final String sprache) {

		Sprache theSprache = null;

		try {

			theSprache = Sprache.valueOf(sprache);
		} catch (IllegalArgumentException e) {

			LOG.error(e.getMessage());

			throw new NotFoundException();
		}

		Identifier veranstalterID = new Identifier(securityContext.getUserPrincipal().getName());

		DownloadData file = unterlagenService.getUnterlagenFuerPrivatanmeldung(veranstalterID, theSprache);

		return MkGatewayFileUtils.createDownloadResponse(file);
	}

}
