// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.api;

/**
 * ProzentrangAPIModel
 */
public class ProzentrangAPIModel {

	private String wettbewerbsjahr;

	private String klassenstufe;

	private String punkteText;

	private int anzahlLoesungszettel;

	private int anzahlKinderBesser;

	private String prozentrang;

	private String text;

	public String getWettbewerbsjahr() {

		return wettbewerbsjahr;
	}

	public ProzentrangAPIModel withWettbewerbsjahr(final String wettbewerbsjahr) {

		this.wettbewerbsjahr = wettbewerbsjahr;
		return this;
	}

	public String getKlassenstufe() {

		return klassenstufe;
	}

	public ProzentrangAPIModel withKlassenstufe(final String klassenstufe) {

		this.klassenstufe = klassenstufe;
		return this;
	}

	public int getAnzahlLoesungszettel() {

		return anzahlLoesungszettel;
	}

	public ProzentrangAPIModel withAnzahlLoesungszettel(final int anzahlLoesungszettel) {

		this.anzahlLoesungszettel = anzahlLoesungszettel;
		return this;
	}

	public String getProzentrang() {

		return prozentrang;
	}

	public ProzentrangAPIModel withProzentrang(final String prozentrang) {

		this.prozentrang = prozentrang;
		return this;
	}

	public String getText() {

		return text;
	}

	public ProzentrangAPIModel withText(final String text) {

		this.text = text;
		return this;
	}

	public int getAnzahlKinderBesser() {

		return anzahlKinderBesser;
	}

	public ProzentrangAPIModel withAnzahlKinderBesser(final int anzahlKinderBesser) {

		this.anzahlKinderBesser = anzahlKinderBesser;
		return this;
	}

	public String getPunkteText() {

		return punkteText;
	}

	public ProzentrangAPIModel withPunkteText(final String punkteText) {

		this.punkteText = punkteText;
		return this;
	}

}
