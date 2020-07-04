// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_kataloge.domain.SchuleMessage;
import de.egladil.web.mk_kataloge.domain.SchuleRepository;
import de.egladil.web.mk_kataloge.domain.error.DuplicateEntityException;
import de.egladil.web.mk_kataloge.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_kataloge.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * UpdateSchulenResource
 */
@ApplicationScoped
@Path("/schulen")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.APPLICATION_JSON)
public class UpdateSchulenResource {

	private static final Logger LOG = LoggerFactory.getLogger(UpdateSchulenResource.class);

	@ConfigProperty(name = "admin.secret")
	String expectedSecret;

	@Inject
	SchuleRepository schuleRepository;

	@Inject
	Event<SecurityIncidentRegistered> securityEvent;

	@PUT
	public Response schuleChanged(final SchuleMessage schuleMessage) {

		if (!expectedSecret.equals(schuleMessage.getSecret())) {

			String msg = "Unautorisierter Versuch, eine Schule zu ändern: " + schuleMessage.toString();

			LOG.warn(msg);

			new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);

			return Response.status(401).build();
		}

		try {

			Schule result = schuleRepository.updateSchule(schuleMessage.getSchule());

			LOG.info("Schule aktualisiert: {}", result.printForLog());

			return Response.ok("Alles fein! Schule " + schuleMessage.getSchule().getKuerzel() + " geändert.").build();

		} catch (IllegalArgumentException e) {

			return Response.status(400)
				.entity(e.getMessage())
				.build();

		} catch (Exception e) {

			LOG.error(e.getMessage(), e);

			return Response.status(500)
				.entity(e.getMessage())
				.build();
		}

	}

	@POST
	public Response schuleInserted(final SchuleMessage schuleMessage) {

		LOG.info(schuleMessage.getSchule().printForLog());

		if (!expectedSecret.equals(schuleMessage.getSecret())) {

			String msg = "Unautorisierter Versuch, eine Schule anzulegen: " + schuleMessage.toString();

			LOG.warn(msg);

			new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);
			return Response.status(401).build();
		}

		LOG.info("importiere {}", schuleMessage.getSchule().printForLog());

		try {

			schuleRepository.addSchule(schuleMessage.getSchule());

			LOG.info("Schule importiert: {}", schuleMessage.getSchule().printForLog());

			String responsePayload = "Alles fein! Schule " + schuleMessage.getSchule().getKuerzel() + " importiert";

			return Response.status(201).entity(responsePayload).build();
		} catch (IllegalArgumentException e) {

			return Response.status(400)
				.entity(e.getMessage())
				.build();

		} catch (DuplicateEntityException e) {

			return Response.status(409)
				.entity(e.getMessage())
				.build();

		} catch (Exception e) {

			LOG.error(e.getMessage(), e);

			return Response.status(500)
				.entity(e.getMessage())
				.build();
		}
	}
}
