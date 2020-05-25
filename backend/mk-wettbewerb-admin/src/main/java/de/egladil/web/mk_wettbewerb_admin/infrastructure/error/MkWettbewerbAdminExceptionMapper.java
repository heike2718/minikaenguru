// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.infrastructure.error;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.commons_validation.InvalidProperty;
import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_wettbewerb_admin.domain.error.AccessDeniedException;
import de.egladil.web.mk_wettbewerb_admin.domain.error.MkWettbewerbAdminRuntimeException;

/**
 * MkWettbewerbAdminExceptionMapper
 */
@Provider
public class MkWettbewerbAdminExceptionMapper implements ExceptionMapper<Throwable> {

	private static final Logger LOG = LoggerFactory.getLogger(MkWettbewerbAdminExceptionMapper.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Override
	public Response toResponse(final Throwable exception) {

		if (exception instanceof NoContentException) {

			return Response.status(204).build();
		}

		if (exception instanceof InvalidInputException) {

			InvalidInputException e = (InvalidInputException) exception;
			ResponsePayload responsePayload = e.getResponsePayload();
			Object data = responsePayload.getData();

			String msg = "";

			if (data != null && data instanceof InvalidProperty[]) {

				List<String> invalidProperties = Arrays.stream(((InvalidProperty[]) data)).map(p -> p.toString())
					.collect(Collectors.toList());
				msg = responsePayload.getMessage().getMessage() + ": " + StringUtils.join(invalidProperties, ",");
			} else {

				msg = responsePayload.getMessage().getMessage();
			}
			LOG.error(msg);
			return Response.status(400).entity(serialize(responsePayload)).build();
		}

		if (exception instanceof NotFoundException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(applicationMessages.getString("general.notFound")));

			return Response.status(404).entity(serialize(payload)).build();
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
				.messageOnly(MessagePayload.error(applicationMessages.getString("general.forbidden")));

			return Response.status(Status.FORBIDDEN).entity(serialize(payload)).build();
		}

		if (exception instanceof MkWettbewerbAdminRuntimeException) {

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
