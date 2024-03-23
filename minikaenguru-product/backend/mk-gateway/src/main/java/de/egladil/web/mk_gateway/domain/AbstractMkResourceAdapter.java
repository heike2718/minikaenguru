// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain;

import java.util.Locale;
import java.util.ResourceBundle;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import org.slf4j.Logger;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.error.InaccessableEndpointException;

/**
 * AbstractMkResourceAdapter
 */
public abstract class AbstractMkResourceAdapter {

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	protected Response handleException(final Exception e, final Logger log, final String context) {

		if (e instanceof WebApplicationException) {

			WebApplicationException exception = (WebApplicationException) e;
			return exception.getResponse();
		}

		if (e instanceof ProcessingException) {

			log.error("endpoint " + endpointName() + " ist nicht erreichbar: {}", e.getMessage(), e);

			throw new InaccessableEndpointException("Der Endpoint " + endpointName() + " ist nicht erreichbar. ");

		}

		log.error(context + ": unerwartete Exception - " + e.getMessage(), e);

		return Response.serverError()
			.entity(ResponsePayload.messageOnly(MessagePayload.error(applicationMessages.getString("general.internalServerError"))))
			.build();
	}

	protected abstract String endpointName();
}
