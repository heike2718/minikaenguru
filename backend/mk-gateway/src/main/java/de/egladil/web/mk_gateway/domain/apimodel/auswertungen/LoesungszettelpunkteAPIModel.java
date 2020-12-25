// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel.auswertungen;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.UuidString;

/**
 * LoesungszettelpunkteAPIModel
 */
public class LoesungszettelpunkteAPIModel {

	@JsonProperty
	@UuidString
	private String loesungszettelId;

	@JsonProperty
	private String punkte;

	@JsonProperty
	private int laengeKaengurusprung;

	public String loesungszettelId() {

		return loesungszettelId;
	}

	public LoesungszettelpunkteAPIModel withLoesungszettelId(final String loesungszettelId) {

		this.loesungszettelId = loesungszettelId;
		return this;
	}

	public String punkte() {

		return punkte;
	}

	public LoesungszettelpunkteAPIModel withPunkte(final String punkte) {

		this.punkte = punkte;
		return this;
	}

	public int laengeKaengurusprung() {

		return laengeKaengurusprung;
	}

	public LoesungszettelpunkteAPIModel withLaengeKaengurusprung(final int laengeKaengurusprung) {

		this.laengeKaengurusprung = laengeKaengurusprung;
		return this;
	}

	@Override
	public int hashCode() {

		return Objects.hash(loesungszettelId);
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
		LoesungszettelpunkteAPIModel other = (LoesungszettelpunkteAPIModel) obj;
		return Objects.equals(loesungszettelId, other.loesungszettelId);
	}

	@Override
	public String toString() {

		return "LoesungszettelpunkteAPIModel [loesungszettelId=" + loesungszettelId + ", punkte=" + punkte
			+ ", laengeKaengurusprung=" + laengeKaengurusprung + "]";
	}

}
