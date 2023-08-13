// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general.kataloge;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.LandKuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;
import de.egladil.web.mk_gateway.domain.kataloge.api.SchulkatalogAntrag;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * PublicKatalogsucheResource
 */
@RequestScoped
@Path("schulkatalog")
@Produces(MediaType.APPLICATION_JSON)
public class PublicKatalogsucheResource {

	@Inject
	MkKatalogeResourceAdapter katalogResourceAdapter;

	@Inject
	DevDelayService delayService;

	@GET
	@Path("suche/{typ}")
	public Response findItems(@PathParam(
		value = "typ") final String typ, @NotBlank @StringLatin @QueryParam("search") final String searchTerm) {

		this.delayService.pause();

		return katalogResourceAdapter.findItems(typ, searchTerm);
	}

	@GET
	@Path("suche/laender/{land}/orte")
	public Response findOrteInLand(@LandKuerzel @PathParam(
		value = "land") final String landKuerzel, @NotBlank @StringLatin @QueryParam("search") final String searchTerm) {

		this.delayService.pause();

		return katalogResourceAdapter.findOrteInLand(landKuerzel, searchTerm);
	}

	@GET
	@Path("suche/orte/{ort}/schulen")
	public Response findSchulenInOrt(@Kuerzel @PathParam(
		value = "ort") final String ortKuerzel, @NotBlank @StringLatin @QueryParam("search") final String searchTerm) {

		this.delayService.pause();

		return katalogResourceAdapter.findSchulenInOrt(ortKuerzel, searchTerm);

	}

	@GET
	@Path("orte/{ort}/schulen")
	public Response loadSchulenInOrt(@Kuerzel @PathParam(
		value = "ort") final String ortKuerzel) {

		this.delayService.pause();

		return katalogResourceAdapter.loadSchulenInOrt(ortKuerzel);
	}

	@GET
	@Path("laender/{land}/orte")
	public Response loadOrteInLand(@LandKuerzel @PathParam(
		value = "land") final String landKuerzel) {

		this.delayService.pause();

		return katalogResourceAdapter.loadOrteInLand(landKuerzel);

	}

	@POST
	@Path("katalogantrag")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendeKatalogantrag(final SchulkatalogAntrag antrag) {

		this.delayService.pause();

		return this.katalogResourceAdapter.sendeSchulkatalogAntrag(antrag);
	}
}
