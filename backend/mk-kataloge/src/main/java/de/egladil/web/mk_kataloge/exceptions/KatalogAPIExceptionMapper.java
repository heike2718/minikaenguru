// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.exceptions;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;

/**
 * KatalogAPIExceptionMapper
 */
@Provider
public class KatalogAPIExceptionMapper implements ExceptionMapper<Exception> {

	private static final Logger LOG = LoggerFactory.getLogger(KatalogAPIExceptionMapper.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Override
	public Response toResponse(final Exception exception) {

		if (exception instanceof NoContentException) {

			return Response.status(204).build();
		}

		if (exception instanceof NotFoundException) {

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(applicationMessages.getString("general.notFound")));

			return Response.status(404).entity(payload).build();
		}

		if (exception instanceof IllegalArgumentException) {

			LOG.error(exception.getMessage());
			ResponsePayload responsePayload = ResponsePayload
				.messageOnly(MessagePayload.error(exception.getMessage()));
			return Response.status(400)
				.entity(responsePayload)
				.build();
		}

		if (exception instanceof DuplicateEntityException) {

			LOG.warn(exception.getMessage());
			ResponsePayload responsePayload = ResponsePayload
				.messageOnly(MessagePayload.warn(exception.getMessage()));
			return Response.status(409)
				.entity(responsePayload)
				.build();
		}

		if (exception instanceof WebApplicationException) {

			WebApplicationException waException = (WebApplicationException) exception;
			int status = waException.getResponse().getStatus();

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(waException.getLocalizedMessage()));

			return Response.status(status).entity(payload).build();
		}

		LOG.error(exception.getMessage(), exception);

		ResponsePayload payload = ResponsePayload
			.messageOnly(MessagePayload.error(applicationMessages.getString("general.internalServerError")));

		return Response.status(500).entity(payload).build();
	}
}
