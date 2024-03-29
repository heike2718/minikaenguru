// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.error;

import java.util.Locale;
import java.util.ResourceBundle;

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
import de.egladil.web.mk_gateway.domain.error.ActionNotAuthorizedException;
import de.egladil.web.mk_gateway.domain.error.AuthException;
import de.egladil.web.mk_gateway.domain.error.ClientAuthException;
import de.egladil.web.mk_gateway.domain.error.InaccessableEndpointException;
import de.egladil.web.mk_gateway.domain.error.MessagingAuthException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayWebApplicationException;
import de.egladil.web.mk_gateway.domain.error.StatistikKeineDatenException;
import de.egladil.web.mk_gateway.domain.error.UnterlagenNichtVerfuegbarException;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.GeneralErrorEvent;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.NoContentException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * MkGatewayExceptionMapper.<br>
 * <br>
 * <strong>Achtung: </strong> Die Serialisierung des ResponsePayloads muss man selbst vornehmen, da der Response als
 * Payload einen String ewartet. Wenn man ein
 * ResponsePayload-Objekt zurückgibt, kommt beim
 * Client ein OK-Response an.
 */
@Provider
public class MkGatewayExceptionMapper implements ExceptionMapper<Throwable> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MkGatewayExceptionMapper.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Context
	SecurityContext securityContext;

	@Inject
	DomainEventHandler domainEventHandler;

	@Override
	public Response toResponse(final Throwable exception) {

		if (exception instanceof NoContentException) {

			return Response.status(204).build();
		}

		if (exception instanceof InvalidInputException) {

			InvalidInputException e = (InvalidInputException) exception;

			int statusUnprocessableEntity = 422;
			return Response.status(statusUnprocessableEntity).entity(serializeAsJson(e.getResponsePayload())).build();
		}

		if (exception instanceof AuthException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(applicationMessages.getString("general.notAuthorized")));

			return Response.status(401)
				.cookie(CommonHttpUtils.createSessionInvalidatedCookie(MkGatewayApp.CLIENT_COOKIE_PREFIX))
				.entity(serializeAsJson(payload))
				.build();
		}

		if (exception instanceof ActionNotAuthorizedException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(exception.getMessage()));

			return Response.status(200)
				.entity(serializeAsJson(payload))
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
				.entity(serializeAsJson(payload))
				.build();
		}

		if (exception instanceof SessionExpiredException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(applicationMessages.getString("general.sessionTimeout")));

			return Response.status(908)
				.cookie(CommonHttpUtils.createSessionInvalidatedCookie(MkGatewayApp.CLIENT_COOKIE_PREFIX))
				.entity(serializeAsJson(payload))
				.build();
		}

		if (exception instanceof InaccessableEndpointException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(exception.getMessage() + applicationMessages.getString("sendMail")));

			return Response.status(503).entity(serializeAsJson(payload)).build();
		}

		if (exception instanceof StatistikKeineDatenException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(applicationMessages.getString("statistik.keineDaten")));

			return Response.status(404).entity(serializeAsJson(payload)).build();
		}

		if (exception instanceof UnterlagenNichtVerfuegbarException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(applicationMessages.getString("unterlagen.nichtVerfuegbar")));

			return Response.status(404).entity(serializeAsJson(payload)).build();
		}

		if (exception instanceof NotFoundException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(applicationMessages.getString("general.notFound")));

			return Response.status(404).entity(serializeAsJson(payload)).build();
		}

		if (exception instanceof UploadFormatException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(exception.getMessage()));

			return Response.status(Status.BAD_REQUEST).entity(payload).build();
		}

		if (exception instanceof BadRequestException) {

			BadRequestException brException = (BadRequestException) exception;

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(applicationMessages.getString("general.badRequest")));

			return Response.status(brException.getResponse().getStatus()).entity(payload).build();
		}

		if (exception instanceof MkGatewayWebApplicationException) {

			MkGatewayWebApplicationException waException = (MkGatewayWebApplicationException) exception;
			return waException.getResponse();
		}

		if (exception instanceof WebApplicationException) {

			WebApplicationException waException = (WebApplicationException) exception;

			String msg = waException.getMessage();
			int status = waException.getResponse().getStatus();

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(msg));

			LOGGER.error(msg);

			GeneralErrorEvent errorEvent = new GeneralErrorEvent(exception.getClass().getSimpleName(), msg);
			domainEventHandler.handleEvent(errorEvent);

			return Response.status(status).entity(serializeAsJson(payload)).build();
		}

		if (exception instanceof MkGatewayRuntimeException || exception instanceof ClientAuthException) {

			GeneralErrorEvent errorEvent = new GeneralErrorEvent(exception.getClass().getSimpleName(), exception.getMessage());
			domainEventHandler.handleEvent(errorEvent);

			// nicht loggen, wurde schon
		} else {

			if (securityContext != null && securityContext.getUserPrincipal() instanceof LoggedInUser) {

				// TODO: MDC stattdessen verwenden und konfigurieren
				LoggedInUser sessionUser = (LoggedInUser) securityContext.getUserPrincipal();
				LOGGER.error("idRef={} - uuid={}: {}", sessionUser.idReference(),
					StringUtils.abbreviate(sessionUser.uuid(), 11), exception.getMessage(), exception);
			} else {

				LOGGER.error(exception.getMessage(), exception);
			}
		}

		ResponsePayload payload = ResponsePayload
			.messageOnly(MessagePayload.error(applicationMessages.getString("general.internalServerError")));

		GeneralErrorEvent errorEvent = new GeneralErrorEvent(exception.getClass().getSimpleName(), exception.getMessage());
		domainEventHandler.handleEvent(errorEvent);

		return Response.status(500).entity(serializeAsJson(payload)).build();
	}

	private String serializeAsJson(final ResponsePayload rp) {

		try {

			return new ObjectMapper().writeValueAsString(rp);
		} catch (JsonProcessingException e) {

			MessagePayload mp = rp.getMessage();
			return mp.getLevel() + " " + mp.getMessage();

		}
	}

}
