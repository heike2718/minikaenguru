// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import de.egladil.web.mk_gateway.domain.apimodel.FileResource;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;

/**
 * AdminUploadResource
 */
@RequestScoped
@Path("/wb-admin/upload")
public class AdminUploadResource {

	@Inject
	MkKatalogeResourceAdapter resourceAdapter;

	@ConfigProperty(name = "admin.secret")
	String katalogAdminSecret;

	@POST
	@Path("/schulen")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFile(@MultipartForm final FileResource input) {

		return resourceAdapter.uploadSchulkatalog(katalogAdminSecret, input);
	}

}
