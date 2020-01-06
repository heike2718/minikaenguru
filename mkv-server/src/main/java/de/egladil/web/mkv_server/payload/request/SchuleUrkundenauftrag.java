// =====================================================
// Projekt: mkv-server
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mkv_server.payload.request;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.mkv_server.validation.annotations.ValidSchuleUrkundenauftrag;

/**
 * SchuleUrkundenauftrag
 */
@ValidSchuleUrkundenauftrag
public class SchuleUrkundenauftrag {

	/* serialVersionUID */
	private static final long serialVersionUID = 1L;

	@NotNull
	private String farbschemaName;

	@NotNull
	@Kuerzel
	private String kuerzelRootgruppe;

	@JsonIgnore
	private String datum;

	/**
	 * SchuleUrkundenauftrag
	 */
	public SchuleUrkundenauftrag() {

	}

	public final String getFarbschemaName() {

		return farbschemaName;
	}

	public final void setFarbschemaName(final String farbschemaName) {

		this.farbschemaName = farbschemaName;
	}

	public final String getKuerzelRootgruppe() {

		return kuerzelRootgruppe;
	}

	public final void setKuerzelRootgruppe(final String kuerzelRootgruppe) {

		this.kuerzelRootgruppe = kuerzelRootgruppe;
	}

	@Override
	public String toString() {

		final StringBuilder builder = new StringBuilder();
		builder.append("SchuleUrkundenauftrag [kuerzelRootgruppe=");
		builder.append(kuerzelRootgruppe);
		builder.append(", farbschemaName=");
		builder.append(farbschemaName);
		builder.append("]");
		return builder.toString();
	}

	public final String getDatum() {

		return datum;
	}

	public final void setDatum(final String datum) {

		this.datum = datum;
	}

}
