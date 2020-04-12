// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_api_gateway.application.rest;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mkv_api_gateway.application.AuthResult;

/**
 * MkvAppUserResource ist die Resource für die Lehrer und Privatmenschen der mkv-app.
 */
@RequestScoped
@Path("/mkv-app/users")
@Produces(MediaType.APPLICATION_JSON)
public class MkvAppUserResource {

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@POST
	public Response createUser(final AuthResult authResult) {

		// Erstmal nur faken
		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info(applicationMessages.getString("createUser.success"))))
			.build();
	}

}
