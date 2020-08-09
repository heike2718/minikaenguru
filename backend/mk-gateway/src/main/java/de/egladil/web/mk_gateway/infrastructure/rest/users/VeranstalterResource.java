// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.users;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.signup.AuthResult;
import de.egladil.web.mk_gateway.domain.signup.AuthResultToResourceOwnerMapper;
import de.egladil.web.mk_gateway.domain.signup.SignUpResourceOwner;
import de.egladil.web.mk_gateway.domain.signup.SignUpService;

/**
 * VeranstalterResource ist die Resource zu den Minikänguru-Veranstaltern.
 */
@RequestScoped
@Path("/wettbewerb")
@Produces(MediaType.APPLICATION_JSON)
public class VeranstalterResource {

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	SignUpService signUpService;

	@Inject
	AuthResultToResourceOwnerMapper authResultMapper;

	@POST
	@Path("/veranstalter")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createUser(final AuthResult authResult) {

		SignUpResourceOwner signUpResourceOwner = authResultMapper.apply(authResult);

		signUpService.createUser(signUpResourceOwner, false);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info(applicationMessages.getString("createUser.success"))))
			.build();
	}
}
