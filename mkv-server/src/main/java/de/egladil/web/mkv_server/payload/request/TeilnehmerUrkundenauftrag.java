// =====================================================
// Projekt: mkv-server
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mkv_server.payload.request;

import java.util.Arrays;

import javax.validation.constraints.NotNull;

import de.egladil.web.mkv_server.validation.annotations.ValidTeilnehmerUrkundenauftrag;

/**
 * TeilnehmerUrkundenauftrag
 */
@ValidTeilnehmerUrkundenauftrag
public class TeilnehmerUrkundenauftrag {

	/* serialVersionUID */
	private static final long serialVersionUID = 1L;

	@NotNull
	private String farbschemaName;

	@NotNull
	private String[] teilnehmerKuerzel;

	private String datum;

	@Override
	public String toString() {

		final StringBuilder builder = new StringBuilder();
		builder.append("TeilnehmerUrkundenauftrag [farbschemaName=");
		builder.append(farbschemaName);
		builder.append(", anzahlTeilnehmer=");
		builder.append(teilnehmerKuerzel != null ? teilnehmerKuerzel.length : 0);
		builder.append("]");
		return builder.toString();
	}

	public final String getFarbschemaName() {

		return farbschemaName;
	}

	public final void setFarbschemaName(final String farbschema) {

		this.farbschemaName = farbschema;
	}

	public final String getDatum() {

		return datum;
	}

	public final void setDatum(final String datum) {

		this.datum = datum;
	}

	public final String[] getTeilnehmerKuerzel() {

		return teilnehmerKuerzel;
	}

	public final void setTeilnehmerKuerzel(final String[] teilnehmerKuerzel) {

		this.teilnehmerKuerzel = teilnehmerKuerzel;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((datum == null) ? 0 : datum.hashCode());
		result = prime * result + ((farbschemaName == null) ? 0 : farbschemaName.hashCode());
		result = prime * result + Arrays.hashCode(teilnehmerKuerzel);
		return result;
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
		final TeilnehmerUrkundenauftrag other = (TeilnehmerUrkundenauftrag) obj;

		if (datum == null) {

			if (other.datum != null) {

				return false;
			}
		} else if (!datum.equals(other.datum)) {

			return false;
		}

		if (farbschemaName == null) {

			if (other.farbschemaName != null) {

				return false;
			}
		} else if (!farbschemaName.equals(other.farbschemaName)) {

			return false;
		}

		if (!Arrays.equals(teilnehmerKuerzel, other.teilnehmerKuerzel)) {

			return false;
		}
		return true;
	}

}
