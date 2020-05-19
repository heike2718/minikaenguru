// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.messaging;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.egladil.web.commons_validation.annotations.Kuerzel;

/**
 * MkKatalogeRestClient
 */
@RegisterRestClient
@Path("/mk-kataloge-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MkKatalogeRestClient {

	/**
	 * Response.entity.data hat die Signatur von List<KatalogItem>
	 *
	 * @param  commaseparatedKuerzel
	 * @return
	 */
	@GET
	@Path("/schulen/{commaseparatedKuerzel}")
	Response findSchulenMitKuerzeln(@PathParam(
		value = "commaseparatedKuerzel") @Kuerzel final String commaseparatedKuerzel);

}
