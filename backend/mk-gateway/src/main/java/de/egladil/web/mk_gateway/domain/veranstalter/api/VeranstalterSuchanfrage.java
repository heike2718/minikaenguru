// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.api;

import java.util.Objects;

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

	@Override
	public int hashCode() {

		return Objects.hash(suchkriterium, suchstring);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null) {

			return false;
		}

		if (getClass() != obj.getClass()) {

			return false;
		}
		VeranstalterSuchanfrage other = (VeranstalterSuchanfrage) obj;
		return suchkriterium == other.suchkriterium && Objects.equals(suchstring, other.suchstring);
	}

	@Override
	public String toString() {

		return "VeranstalterSuchanfrage [suchkriterium=" + suchkriterium + ", suchstring=" + suchstring + "]";
	}

	public VeranstalterSuchkriterium getSuchkriterium() {

		return suchkriterium;
	}

	public String getSuchstring() {

		return suchstring;
	}

	public VeranstalterSuchanfrage withSuchkriterium(final VeranstalterSuchkriterium suchkriterium) {

		this.suchkriterium = suchkriterium;
		return this;
	}

	public VeranstalterSuchanfrage withSuchstring(final String suchstring) {

		this.suchstring = suchstring;
		return this;
	}

}
