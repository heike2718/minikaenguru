// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.migration;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.apimodel.migration.ZuMigrierenderBenutzer;
import de.egladil.web.mk_gateway.domain.signup.MigrationService;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.User;

/**
 * MigrationResource
 */
@RequestScoped
@Path("/migration")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MigrationResource {

	private static final Logger LOG = LoggerFactory.getLogger(MigrationResource.class);

	@ConfigProperty(name = "mk.migration.secret")
	String expectedSecret;

	@Inject
	MigrationService migrationService;

	@POST
	@Path("/veranstalter")
	public Response veranstalterAnlegen(final ZuMigrierenderBenutzer requestPayload) {

		if (!expectedSecret.equals(requestPayload.getSecret())) {

			throw new SecurityException("veranstalter importieren, ohne das secret zu kennen, ist verboten");
		}

		User user = migrationService.veranstalterImportieren(requestPayload);

		if (user != null) {

			LOG.info("{} migriert", user);
		}

		return Response.ok("mk-gateway: success").build();
	}

}
