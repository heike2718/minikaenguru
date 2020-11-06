// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general.kataloge;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.LandKuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;
import de.egladil.web.mk_gateway.domain.kataloge.api.SchulkatalogAntrag;

/**
 * PublicKatalogsucheResource
 */
@RequestScoped
@Path("/schulkatalog")
@Produces(MediaType.APPLICATION_JSON)
public class PublicKatalogsucheResource {

	@Inject
	MkKatalogeResourceAdapter katalogResourceAdapter;

	@GET
	@Path("/suche/{typ}")
	public Response findItems(@PathParam(
		value = "typ") final String typ, @NotBlank @StringLatin @QueryParam("search") final String searchTerm) {

		return katalogResourceAdapter.findItems(typ, searchTerm);
	}

	@GET
	@Path("/suche/laender/{land}/orte")
	public Response findOrteInLand(@LandKuerzel @PathParam(
		value = "land") final String landKuerzel, @NotBlank @StringLatin @QueryParam("search") final String searchTerm) {

		return katalogResourceAdapter.findOrteInLand(landKuerzel, searchTerm);
	}

	@GET
	@Path("/suche/orte/{ort}/schulen")
	public Response findSchulenInOrt(@Kuerzel @PathParam(
		value = "ort") final String ortKuerzel, @NotBlank @StringLatin @QueryParam("search") final String searchTerm) {

		return katalogResourceAdapter.findSchulenInOrt(ortKuerzel, searchTerm);

	}

	@GET
	@Path("/orte/{ort}/schulen")
	public Response loadSchulenInOrt(@Kuerzel @PathParam(
		value = "ort") final String ortKuerzel) {

		return katalogResourceAdapter.loadSchulenInOrt(ortKuerzel);
	}

	@GET
	@Path("/laender/{land}/orte")
	public Response loadOrteInLand(@LandKuerzel @PathParam(
		value = "land") final String landKuerzel) {

		return katalogResourceAdapter.loadOrteInLand(landKuerzel);

	}

	@POST
	@Path("/katalogantrag")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendeKatalogantrag(final SchulkatalogAntrag antrag) {

		return this.katalogResourceAdapter.sendeSchulkatalogAntrag(antrag);
	}
}
