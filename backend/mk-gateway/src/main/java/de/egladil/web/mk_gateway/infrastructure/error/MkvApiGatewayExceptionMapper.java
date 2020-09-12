// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.error;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.commons_net.exception.SessionExpiredException;
import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.MkGatewayApp;
import de.egladil.web.mk_gateway.domain.auth.session.LoggedInUser;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.error.AuthException;
import de.egladil.web.mk_gateway.domain.error.ClientAuthException;
import de.egladil.web.mk_gateway.domain.error.InaccessableEndpointException;
import de.egladil.web.mk_gateway.domain.error.MessagingAuthException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;

/**
 * MkvApiGatewayExceptionMapper.<br>
 * <br>
 * <strong>Achtung: </strong> Die Serialisierung des ResponsePayloads muss man selbst vornehmen, da der Response als
 * Payload einen String ewartet. Wenn man ein
 * ResponsePayload-Objekt zurückgibt, kommt beim
 * Client ein OK-Response an.
 */
@Provider
public class MkvApiGatewayExceptionMapper implements ExceptionMapper<Throwable> {

	private static final Logger LOG = LoggerFactory.getLogger(MkvApiGatewayExceptionMapper.class);

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

		if (exception instanceof AuthException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(applicationMessages.getString("general.notAuthorized")));

			return Response.status(401)
				.cookie(CommonHttpUtils.createSessionInvalidatedCookie(MkGatewayApp.CLIENT_COOKIE_PREFIX))
				.entity(serialize(payload))
				.build();
		}

		if (exception instanceof MessagingAuthException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(applicationMessages.getString("messaging.notAuthorized")));
			return Response.status(401).entity(payload).build();

		}

		if (exception instanceof AccessDeniedException || exception instanceof SecurityException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(applicationMessages.getString("general.forbidden")));

			return Response.status(Status.FORBIDDEN)
				.entity(serialize(payload))
				.build();
		}

		if (exception instanceof SessionExpiredException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(applicationMessages.getString("general.sessionTimeout")));

			return Response.status(908)
				.cookie(CommonHttpUtils.createSessionInvalidatedCookie(MkGatewayApp.CLIENT_COOKIE_PREFIX))
				.entity(serialize(payload))
				.build();
		}

		if (exception instanceof InaccessableEndpointException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(exception.getMessage() + applicationMessages.getString("sendMail")));

			return Response.status(909).entity(serialize(payload)).build();
		}

		if (exception instanceof NotFoundException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(applicationMessages.getString("general.notFound")));

			return Response.status(404).entity(serialize(payload)).build();
		}

		if (exception instanceof BadRequestException) {

			BadRequestException brException = (BadRequestException) exception;

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(applicationMessages.getString("general.badRequest")));

			return Response.status(brException.getResponse().getStatus()).entity(payload).build();
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

		if (exception instanceof MkGatewayRuntimeException || exception instanceof ClientAuthException) {

			// nicht loggen, wurde schon
		} else {

			if (securityContext != null && securityContext.getUserPrincipal() instanceof LoggedInUser) {

				// TODO: MDC stattdessen verwenden und konfigurieren
				LoggedInUser sessionUser = (LoggedInUser) securityContext.getUserPrincipal();
				LOG.error("idRef={} - uuid={}: {}", sessionUser.idReference(),
					StringUtils.abbreviate(sessionUser.uuid(), 11), exception.getMessage(), exception);
			} else {

				LOG.error(exception.getMessage(), exception);
			}
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
