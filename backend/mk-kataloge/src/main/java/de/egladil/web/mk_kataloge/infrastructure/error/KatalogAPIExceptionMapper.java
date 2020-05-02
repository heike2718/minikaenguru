// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.error;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.DuplicateEntityException;

/**
 * KatalogAPIExceptionMapper<br>
 * <br>
 * <strong>Achtung: </strong> Die Serialisierung des ResponsePayloads muss man selbst vornehmen, da der Response als
 * Payload einen String ewartet. Wenn man ein
 * ResponsePayload-Objekt zurückgibt, kommt beim
 * Client ein OK-Response an.
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

			return Response.status(404).entity(serialize(payload)).build();
		}

		if (exception instanceof IllegalArgumentException) {

			LOG.error(exception.getMessage());
			ResponsePayload responsePayload = ResponsePayload
				.messageOnly(MessagePayload.error(exception.getMessage()));

			return Response.status(400)
				.entity(serialize(responsePayload))
				.build();
		}

		if (exception instanceof DuplicateEntityException) {

			LOG.warn(exception.getMessage());
			ResponsePayload responsePayload = ResponsePayload
				.messageOnly(MessagePayload.warn(exception.getMessage()));

			return Response.status(409)
				.entity(serialize(responsePayload))
				.build();
		}

		if (exception instanceof WebApplicationException) {

			WebApplicationException waException = (WebApplicationException) exception;
			int status = waException.getResponse().getStatus();

			ResponsePayload payload = ResponsePayload
				.messageOnly(MessagePayload.error(waException.getLocalizedMessage()));

			return Response.status(status).entity(serialize(payload)).build();
		}

		LOG.error(exception.getMessage(), exception);

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
