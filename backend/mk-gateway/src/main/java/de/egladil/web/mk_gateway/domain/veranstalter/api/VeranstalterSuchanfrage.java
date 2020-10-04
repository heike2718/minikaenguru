// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.veranstalter.admin.VeranstalterSuchkriterium;

/**
 * VeranstalterSuchanfrage
 */
public class VeranstalterSuchanfrage {

	@JsonProperty
	private VeranstalterSuchkriterium suchkriterium;

	@JsonProperty
	private String suchstring;

	public VeranstalterSuchanfrage() {

	}

	public VeranstalterSuchanfrage(final VeranstalterSuchkriterium suchkriterium, final String suchstring) {

		this.suchkriterium = suchkriterium;
		this.suchstring = suchstring;
	}

	public VeranstalterSuchkriterium getSuchkriterium() {

		return suchkriterium;
	}

	public String getSuchstring() {

		return suchstring;
	}

}
