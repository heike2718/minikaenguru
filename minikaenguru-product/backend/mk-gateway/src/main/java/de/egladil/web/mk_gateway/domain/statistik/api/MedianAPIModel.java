// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * MedianAPIModel
 */
public class MedianAPIModel {

	@JsonProperty
	private Klassenstufe klassenstufe;

	@JsonProperty
	private String median;

	@JsonProperty
	private int anzahlLoesungszettel;

	MedianAPIModel() {

	}

	public MedianAPIModel(final Klassenstufe klassenstufe, final String median, final int anzahlLoesungszettel) {

		this.klassenstufe = klassenstufe;
		this.median = median;
		this.anzahlLoesungszettel = anzahlLoesungszettel;
	}

	@Override
	public String toString() {

		return "MedianAPIModel [klassenstufe=" + klassenstufe + ", anzahlLoesungszettel=" + anzahlLoesungszettel + ", median="
			+ median + "]";
	}

	public Klassenstufe getKlassenstufe() {

		return klassenstufe;
	}

	public String getMedian() {

		return median;
	}

	public int getAnzahlLoesungszettel() {

		return anzahlLoesungszettel;
	}

}
