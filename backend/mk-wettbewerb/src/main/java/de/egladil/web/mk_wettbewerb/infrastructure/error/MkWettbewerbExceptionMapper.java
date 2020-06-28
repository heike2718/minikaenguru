// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.error;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_wettbewerb.domain.error.AccessDeniedException;
import de.egladil.web.mk_wettbewerb.domain.error.MkWettbewerbRuntimeException;

/**
 * MkWettbewerbExceptionMapper<br>
 * <br>
 * <strong>Achtung: </strong> Die Serialisierung des ResponsePayloads muss man selbst vornehmen, da der Response als
 * Payload einen String ewartet. Wenn man ein
 * ResponsePayload-Objekt zurückgibt, kommt beim
 * Client ein OK-Response an.
 */
@Provider
public class MkWettbewerbExceptionMapper implements ExceptionMapper<Throwable> {

	private static final Logger LOG = LoggerFactory.getLogger(MkWettbewerbExceptionMapper.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Context
	SecurityContext securityContext;

	@Override
	public Response toResponse(final Throwable exception) {

		if (exception instanceof NoContentException) {

			return Response.status(204).build();
		}

		if (exception instanceof InvalidInputException) {

			InvalidInputException e = (InvalidInputException) exception;
			return Response.status(400).entity(serialize(e.getResponsePayload())).build();
		}

		if (exception instanceof NotFoundException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(applicationMessages.getString("general.notFound")));

			return Response.status(404).entity(serialize(payload)).build();
		}

		if (exception instanceof IllegalStateException) {

			String msg = exception.getMessage();

			if (msg == null) {

				msg = applicationMessages.getString("general.illegalState");
			}

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(msg));

			return Response.status(412).entity(serialize(payload)).build();
		}

		if (exception instanceof WebApplicationException) {

			WebApplicationException waException = (WebApplicationException) exception;

			String msg = waException.getMessage();
			int status = waException.getResponse().getStatus();

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(msg));

			LOG.error(msg);

			return Response.status(status).entity(serialize(payload)).build();
		}

		if (exception instanceof AccessDeniedException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error("keine Berechtigung"));

			return Response.status(Status.FORBIDDEN).entity(serialize(payload)).build();
		}

		if (exception instanceof MkWettbewerbRuntimeException) {

			// nicht loggen, wurde schon
		} else {

			// if (securityContext != null && securityContext.getUserPrincipal() instanceof SessionUser) {

			// TODO: MDC stattdessen verwenden und konfigurieren
			// SessionUser sessionUser = (SessionUser) securityContext.getUserPrincipal();
			// LOG.error("idRef={} - uuid={}: {}", sessionUser.getIdReference(),
			// StringUtils.abbreviate(sessionUser.getUuid(), 11), exception.getMessage(), exception);
			// } else {

			LOG.error(exception.getMessage(), exception);
			// }
		}

		ResponsePayload payload = ResponsePayload
			.messageOnly(MessagePayload.error(applicationMessages.getString("general.internalServerError")));

		return Response.status(500).entity(serialize(payload)).build();
	}

	private String serialize(final ResponsePayload rp) {

		try {

			return new ObjectMapper().writeValueAsString(rp);
		} catch (JsonProcessingException e) {

			MessagePayload mp = rp.getMessage();
			return mp.getLevel() + " " + mp.getMessage();

		}
	}

}
