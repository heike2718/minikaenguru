// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.unterlagen.UnterlagenService;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

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

	@Inject
	DevDelayService delayService;

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

		this.delayService.pause();

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

		this.delayService.pause();

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
