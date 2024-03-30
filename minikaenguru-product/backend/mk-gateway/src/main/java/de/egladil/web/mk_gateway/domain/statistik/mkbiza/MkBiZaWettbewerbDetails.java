// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.mkbiza;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * MkBiZaWettbewerbDetails
 */
public class MkBiZaWettbewerbDetails {

	@JsonProperty
	private int jahr;

	@JsonProperty
	private int anzahlKinderGesamt;

	@JsonProperty
	private List<Klassenstufe> klassenstufen = new ArrayList<>();

	@JsonProperty
	private List<MkBiZaGruppierungsitem> schulteilnahmenJeLand = new ArrayList<>();

	@JsonProperty
	private List<MkBiZaGruppierungsitem> kinderJeTeilnahmeart = new ArrayList<>();;

	@JsonProperty
	private List<MkBiZaGruppierungsitem> kinderJeKlassenstufe = new ArrayList<>();;

	@JsonProperty
	private List<MkBiZaGruppierungsitem> kinderJeSprache = new ArrayList<>();;

	public MkBiZaWettbewerbDetails addKlassenstufe(final Klassenstufe klassenstufe) {

		this.klassenstufen.add(klassenstufe);
		return this;
	}

	public int getJahr() {

		return jahr;
	}

	public void setJahr(final int jahr) {

		this.jahr = jahr;
	}

	public int getAnzahlKinderGesamt() {

		return anzahlKinderGesamt;
	}

	public void setAnzahlKinderGesamt(final int anzahlKinderGesamt) {

		this.anzahlKinderGesamt = anzahlKinderGesamt;
	}

	public List<MkBiZaGruppierungsitem> getSchulteilnahmenJeLand() {

		return schulteilnahmenJeLand;
	}

	public List<MkBiZaGruppierungsitem> getKinderJeTeilnahmeart() {

		return kinderJeTeilnahmeart;
	}

	public List<MkBiZaGruppierungsitem> getKinderJeKlassenstufe() {

		return kinderJeKlassenstufe;
	}

	public List<MkBiZaGruppierungsitem> getKinderJeSprache() {

		return kinderJeSprache;
	}

	public List<Klassenstufe> getKlassenstufen() {

		return klassenstufen;
	}

	public void addKinderJeKlassenstufe(final MkBiZaGruppierungsitem kinderJeKlassenstufe, final Klassenstufe klassenstufe) {

		this.kinderJeKlassenstufe.add(kinderJeKlassenstufe);
		this.klassenstufen.add(klassenstufe);

	}

	public void addKinderJeSprache(final MkBiZaGruppierungsitem kinderJeSprache) {

		this.kinderJeSprache.add(kinderJeSprache);

	}

	public void addKinderJeTeilnahmeart(final MkBiZaGruppierungsitem kinderJeTeilnahmeart) {

		this.kinderJeTeilnahmeart.add(kinderJeTeilnahmeart);

	}

	public void addSchulteilnahmenJeLand(final MkBiZaGruppierungsitem schulteilnahme) {

		this.schulteilnahmenJeLand.add(schulteilnahme);
	}
}
