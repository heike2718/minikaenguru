// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.wettbewerbe;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mkbiza_api.domain.dto.Gruppierungsitem;

/**
 * WettbewerbDetails
 */
@Schema(description = "Stellt die statistischen Details eine Minikänguru-Wettbewerbs zur Verfügung")
public class WettbewerbDetails {

	@JsonProperty
	private int jahr;

	@JsonProperty
	private int anzahlKinderGesamt;

	@JsonProperty
	private List<Gruppierungsitem> schulteilnahmenJeLand;

	@JsonProperty
	private List<Gruppierungsitem> teilnahmenJeTeilnahmeart;

	@JsonProperty
	private List<Gruppierungsitem> kinderJeTeilnahmeart;

	@JsonProperty
	private List<Gruppierungsitem> kinderJeKlassenstufe;

	@JsonProperty
	private List<Gruppierungsitem> kinderJeSprache;

	public int getJahr() {

		return jahr;
	}

	public WettbewerbDetails withJahr(final int jahr) {

		this.jahr = jahr;
		return this;
	}

	public List<Gruppierungsitem> getSchulteilnahmenJeLand() {

		return schulteilnahmenJeLand;
	}

	public WettbewerbDetails withSchulteilnahmenJeLand(final List<Gruppierungsitem> schulteilnahmenJeLand) {

		this.schulteilnahmenJeLand = schulteilnahmenJeLand;
		return this;
	}

	public List<Gruppierungsitem> getTeilnahmenJeTeilnahmeart() {

		return teilnahmenJeTeilnahmeart;
	}

	public WettbewerbDetails withTeilnahmenJeTeilnahmeart(final List<Gruppierungsitem> teilnahmenJeTeilnahmeart) {

		this.teilnahmenJeTeilnahmeart = teilnahmenJeTeilnahmeart;
		return this;
	}

	public List<Gruppierungsitem> getKinderJeKlassenstufe() {

		return kinderJeKlassenstufe;
	}

	public WettbewerbDetails withKinderJeKlassenstufe(final List<Gruppierungsitem> kinderJeKlassenstufe) {

		this.kinderJeKlassenstufe = kinderJeKlassenstufe;
		return this;
	}

	public List<Gruppierungsitem> getKinderJeSprache() {

		return kinderJeSprache;
	}

	public WettbewerbDetails withKinderJeSprache(final List<Gruppierungsitem> kinderJeSprache) {

		this.kinderJeSprache = kinderJeSprache;
		return this;
	}

	public int getAnzahlKinderGesamt() {

		return anzahlKinderGesamt;
	}

	public WettbewerbDetails withAnzahlKinderGesamt(final int anzahlKinderGesamt) {

		this.anzahlKinderGesamt = anzahlKinderGesamt;
		return this;
	}

	public List<Gruppierungsitem> getKinderJeTeilnahmeart() {

		return kinderJeTeilnahmeart;
	}

	public WettbewerbDetails withKinderJeTeilnahmeart(final List<Gruppierungsitem> kinderJeTeilnahmeart) {

		this.kinderJeTeilnahmeart = kinderJeTeilnahmeart;
		return this;
	}

}
