// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.infrastructure.error;

import java.util.Locale;
import java.util.ResourceBundle;

import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mkbiza_api.domain.dto.MessagePayload;
import de.egladil.web.mkbiza_api.domain.exeptions.MkBiZaCommunicationExcepion;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * MkBiZaExceptionMapper
 */
@Provider
public class MkBiZaExceptionMapper implements ExceptionMapper<Throwable> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MkBiZaExceptionMapper.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Override
	public Response toResponse(final Throwable exception) {

		LOGGER.debug(exception.getMessage(), exception);

		if (exception instanceof MkBiZaCommunicationExcepion) {

			LOGGER.debug(exception.getMessage());

			return this.handleWrappedException(exception.getCause());
		}

		LOGGER.error(exception.getMessage(), exception);

		return Response.status(500).entity(MessagePayload.error(applicationMessages.getString("general.internalServerError")))
			.build();
	}

	private Response handleWrappedException(final Throwable exception) {

		if (exception instanceof ProcessingException) {

			ProcessingException e = (ProcessingException) exception;
			LOGGER.error("ProcessingException bei Kommunikation mit mja-api: {}", e.getMessage());
			MessagePayload messagePayload = MessagePayload
				.error(applicationMessages.getString("general.internalServerError"));
			return Response.status(500).entity(messagePayload).build();
		}

		if (exception instanceof ClientWebApplicationException) {

			ClientWebApplicationException waException = (ClientWebApplicationException) exception;

			if (waException.getCause() != null) {

				if (waException.getCause() instanceof WebApplicationException) {

					return mapWebApplicationException(waException.getCause());
				} else {

					LOGGER.error("ClientWebApplicationException mit status {} bei Kommunikation mit mja-api: {}",
						waException.getResponse().getStatus(), exception.getMessage(), exception);
					MessagePayload messagePayload = MessagePayload
						.error(applicationMessages.getString("general.internalServerError"));
					return Response.status(500).entity(messagePayload).build();
				}

			}

			LOGGER.error("ClientWebApplicationException mit status {} bei Kommunikation mit mja-api: {}",
				waException.getResponse().getStatus(), exception.getMessage(), exception);
			MessagePayload messagePayload = MessagePayload
				.error(applicationMessages.getString("general.internalServerError"));
			return Response.status(500).entity(messagePayload).build();
		}

		if (exception instanceof WebApplicationException) {

			return mapWebApplicationException(exception);
		}

		LOGGER.error(exception.getMessage(), exception);

		return Response.status(500).entity(MessagePayload.error(applicationMessages.getString("general.internalServerError")))
			.build();
	}

	/**
	 * @param  exception
	 * @return
	 */
	private Response mapWebApplicationException(final Throwable exception) {

		WebApplicationException waException = (WebApplicationException) exception;

		if (waException.getResponse().getStatus() != 404) {

			LOGGER.error("WebApplicationException mit status {} bei Kommunikation mit mja-api: {}",
				waException.getResponse().getStatus(), exception.getMessage(), exception);
		}
		return waException.getResponse();
	}

}
