// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel.teilnahmen;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * TeilnahmeIdentifier
 */
public class TeilnahmeIdentifier {

	@JsonProperty
	private int jahr;

	@JsonProperty
	private String teilnahmenummer;

	@JsonProperty
	private String teilnahmeart;

	public static TeilnahmeIdentifier createFromTeilnahme(final Teilnahme teilnahme) {

		TeilnahmeIdentifier result = new TeilnahmeIdentifier();
		result.jahr = teilnahme.wettbewerbID().jahr();
		result.teilnahmeart = teilnahme.teilnahmeart().toString();
		result.teilnahmenummer = teilnahme.teilnahmenummer().identifier();
		return result;
	}

	public int jahr() {

		return jahr;
	}

	public String teilnahmenummer() {

		return teilnahmenummer;
	}

	public String teilnahmeart() {

		return teilnahmeart;
	}

	public String wettbewerbID() {

		return "" + jahr;
	}

	public TeilnahmeIdentifier withWettbewerbID(final WettbewerbID wettbewerbID) {

		if (this.jahr != 0) {

			throw new IllegalStateException("TeilnahmeIdentifier ist immutable: jahr darf nicht geändert werden.");
		}
		this.jahr = wettbewerbID.jahr();
		return this;
	}

	public TeilnahmeIdentifier withTeilnahmenummer(final String teilnahmenummer) {

		if (this.teilnahmenummer != null) {

			throw new IllegalStateException("TeilnahmeIdentifier ist immutable: teilnahmenummer darf nicht geändert werden.");
		}
		this.teilnahmenummer = teilnahmenummer;
		return this;
	}

	public TeilnahmeIdentifier withTeilnahmeart(final String teilnahmeart) {

		if (this.teilnahmeart != null) {

			throw new IllegalStateException("TeilnahmeIdentifier ist immutable: teilnahmeart darf nicht geändert werden.");
		}
		this.teilnahmeart = teilnahmeart;
		return this;
	}

	public TeilnahmeIdentifier withTeilnahmeart(final Teilnahmeart teilnahmeart) {

		if (this.teilnahmeart != null) {

			throw new IllegalStateException("TeilnahmeIdentifier ist immutable: teilnahmeart darf nicht geändert werden.");
		}
		this.teilnahmeart = teilnahmeart.toString();
		return this;
	}

	@Override
	public String toString() {

		return "TeilnahmeIdentifier [teilnahmenummer=" + teilnahmenummer + ", teilnahmeart=" + teilnahmeart + ", jahr=" + jahr
			+ "]";
	}

	@Override
	public int hashCode() {

		return Objects.hash(jahr, teilnahmeart, teilnahmenummer);
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
		TeilnahmeIdentifier other = (TeilnahmeIdentifier) obj;
		return jahr == other.jahr && Objects.equals(teilnahmeart, other.teilnahmeart)
			&& Objects.equals(teilnahmenummer, other.teilnahmenummer);
	}

}
