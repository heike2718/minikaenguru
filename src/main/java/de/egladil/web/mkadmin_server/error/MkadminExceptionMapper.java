// =====================================================
// Project: mkadmin-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkadmin_server.error;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.exception.SessionExpiredException;
import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;

/**
 * MkadminExceptionMapper
 */
@Provider
public class MkadminExceptionMapper implements ExceptionMapper<Exception> {

	private static final Logger LOG = LoggerFactory.getLogger(MkadminExceptionMapper.class);

	@Override
	public Response toResponse(final Exception exception) {

		if (exception instanceof NoContentException) {

			return Response.status(204).build();
		}

		if (exception instanceof InvalidInputException) {

			InvalidInputException e = (InvalidInputException) exception;
			return Response.status(400).entity(e.getResponsePayload()).build();
		}

		if (exception instanceof AuthException) {

			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error("Du kommst nicht vorbei!"));
			return Response.status(401).entity(payload).build();
		}

		if (exception instanceof SessionExpiredException) {

			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error("Deine Session ist abgelaufen."));
			return Response.status(901).entity(payload).build();
		}

		if (exception instanceof NotFoundException) {

			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error("Hamwer nich"));
			return Response.status(404).entity(payload).build();
		}

		if (exception instanceof ConcurrentUpdateException) {

			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.warn(exception.getMessage()));
			return Response.status(409).entity(payload).build();
		}

		if (exception instanceof MkadminRuntimeException) {

			// wurde schon geloggt
		} else {

			LOG.error(exception.getMessage(), exception);
		}

		ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error(
			"OMG +++ Divide By Cucumber Error. Please Reinstall Universe And Reboot +++ (und schau besser auch mal ins Server-Log...)"));

		return Response.status(Status.INTERNAL_SERVER_ERROR).header("X-Checklisten-Error", payload.getMessage().getMessage())
			.entity(payload).build();
	}

}
