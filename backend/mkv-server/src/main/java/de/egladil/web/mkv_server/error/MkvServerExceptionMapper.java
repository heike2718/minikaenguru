// =====================================================
// Project: mkv-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server.error;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.exception.SessionExpiredException;
import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_commons.exception.AuthException;
import de.egladil.web.mk_commons.exception.ClientAuthException;
import de.egladil.web.mk_commons.exception.MkRuntimeException;
import de.egladil.web.mk_commons.session.SessionUser;
import de.egladil.web.mkv_server.MkvServerApp;

/**
 * MkvServerExceptionMapper
 */
@Provider
public class MkvServerExceptionMapper implements ExceptionMapper<Throwable> {

	private static final Logger LOG = LoggerFactory.getLogger(MkvServerExceptionMapper.class);

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
			return Response.status(400).entity(e.getResponsePayload()).build();
		}

		if (exception instanceof AuthException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(applicationMessages.getString("general.notAuthorized")));

			return Response.status(401)
				.cookie(CommonHttpUtils.createSessionInvalidatedCookie(MkvServerApp.CLIENT_COOKIE_PREFIX)).entity(payload)
				.build();
		}

		if (exception instanceof SessionExpiredException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(applicationMessages.getString("general.sessionTimeout")));

			return Response.status(908)
				.cookie(CommonHttpUtils.createSessionInvalidatedCookie(MkvServerApp.CLIENT_COOKIE_PREFIX)).entity(payload)
				.build();
		}

		if (exception instanceof NotFoundException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(applicationMessages.getString("general.notFound")));

			return Response.status(404).entity(payload).build();
		}

		if (exception instanceof MkRuntimeException || exception instanceof ClientAuthException) {

			// nicht loggen, wurde schon
		} else {

			if (securityContext != null && securityContext.getUserPrincipal() instanceof SessionUser) {

				// TODO: MDC stattdessen verwenden und konfigurieren
				SessionUser sessionUser = (SessionUser) securityContext.getUserPrincipal();
				LOG.error("idRef={} - uuid={}: {}", sessionUser.getIdReference(),
					StringUtils.abbreviate(sessionUser.getUuid(), 11), exception.getMessage(), exception);
			} else {

				LOG.error(exception.getMessage(), exception);
			}
		}

		ResponsePayload payload = ResponsePayload
			.messageOnly(MessagePayload.error(applicationMessages.getString("general.internalServerError")));

		return Response.status(500).entity(payload).build();
	}

}