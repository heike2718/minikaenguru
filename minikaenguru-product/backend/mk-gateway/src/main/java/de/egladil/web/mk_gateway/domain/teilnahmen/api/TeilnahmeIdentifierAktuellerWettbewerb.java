// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen.api;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.semantik.ValueObject;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;

/**
 * TeilnahmeIdentifierAktuellerWettbewerb
 */
@ValueObject
public class TeilnahmeIdentifierAktuellerWettbewerb {

	@JsonProperty
	private String teilnahmenummer;

	@JsonProperty
	private Teilnahmeart teilnahmeart;

	public static TeilnahmeIdentifierAktuellerWettbewerb createForPrivatteilnahme(String teilnahmenummer) {
		return new TeilnahmeIdentifierAktuellerWettbewerb(teilnahmenummer, Teilnahmeart.PRIVAT);
	}

	public static TeilnahmeIdentifierAktuellerWettbewerb createForSchulteilnahme(String teilnahmenummer) {
		return new TeilnahmeIdentifierAktuellerWettbewerb(teilnahmenummer, Teilnahmeart.SCHULE);
	}

	public static TeilnahmeIdentifierAktuellerWettbewerb createFromTeilnahme(final Teilnahme teilnahme) {

		TeilnahmeIdentifierAktuellerWettbewerb result = new TeilnahmeIdentifierAktuellerWettbewerb();
		result.teilnahmenummer = teilnahme.teilnahmenummer().identifier();
		result.teilnahmeart = teilnahme.teilnahmeart();
		return result;
	}

	TeilnahmeIdentifierAktuellerWettbewerb() {

	}

	public TeilnahmeIdentifierAktuellerWettbewerb(final String teilnahmenummer, final Teilnahmeart teilnahmeart) {

		this.teilnahmenummer = teilnahmenummer;
		this.teilnahmeart = teilnahmeart;
	}

	public String teilnahmenummer() {

		return teilnahmenummer;
	}

	public Teilnahmeart teilnahmeart() {

		return teilnahmeart;
	}

	@Override
	public int hashCode() {

		return Objects.hash(teilnahmeart, teilnahmenummer);
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
		TeilnahmeIdentifierAktuellerWettbewerb other = (TeilnahmeIdentifierAktuellerWettbewerb) obj;
		return teilnahmeart == other.teilnahmeart && Objects.equals(teilnahmenummer, other.teilnahmenummer);
	}

	@Override
	public String toString() {

		return "TeilnahmeIdentifierAktuellerWettbewerb [teilnahmenummer=" + teilnahmenummer + ", teilnahmeart=" + teilnahmeart
			+ "]";
	}

}
