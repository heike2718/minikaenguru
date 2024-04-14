// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.mkbiza;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * MkBiZaWettbewerbDetails
 */
public class MkBiZaWettbewerbDetails {

	@JsonProperty
	private int jahr;

	@JsonProperty
	private boolean beendet;

	@JsonProperty
	private int anzahlKinderGesamt;

	@JsonProperty
	private long anzahlPrivatanmeldungen;

	@JsonProperty
	private long anzahlSchulanmeldungen;

	@JsonProperty
	private long teilnehmendeSchulenGesamt;

	@JsonProperty
	private List<Klassenstufe> klassenstufen = new ArrayList<>();

	@JsonProperty
	private List<MkBiZaGruppierungsitem> schulenJeLand = new ArrayList<>();

	@JsonProperty
	@Schema(description = "Histogramm der Kinder je Land, wobei PRIVAT als ein Land interpretiert wird.")
	private List<MkBiZaGruppierungsitem> kinderJeLand = new ArrayList<>();

	@JsonProperty
	private List<MkBiZaGruppierungsitem> kinderJeTeilnahmeart = new ArrayList<>();

	@JsonProperty
	private List<MkBiZaGruppierungsitem> kinderJeKlassenstufe = new ArrayList<>();

	@JsonProperty
	private List<MkBiZaGruppierungsitem> kinderJeSprache = new ArrayList<>();

	@JsonProperty
	private List<MkBiZaGruppierungsitem> medianeJeKlassenstufe = new ArrayList<>();

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

	public List<MkBiZaGruppierungsitem> getKinderJeLand() {

		return this.kinderJeLand;
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

	public void addKinderJeLand(final MkBiZaGruppierungsitem gruppierungsitem) {

		this.kinderJeLand.add(gruppierungsitem);
	}

	public long getAnzahlPrivatanmeldungen() {

		return anzahlPrivatanmeldungen;
	}

	public void setAnzahlPrivatanmeldungen(final long anzahlPrivatteilnahmen) {

		this.anzahlPrivatanmeldungen = anzahlPrivatteilnahmen;
	}

	public long getAnzahlSchulanmeldungen() {

		return anzahlSchulanmeldungen;
	}

	public void setAnzahlSchulanmeldungen(final long anzahlSchulteilnahmen) {

		this.anzahlSchulanmeldungen = anzahlSchulteilnahmen;
	}

	public List<MkBiZaGruppierungsitem> getSchulenJeLand() {

		return schulenJeLand;
	}

	public void addSchulenJeLand(final MkBiZaGruppierungsitem schulenJeLand) {

		this.schulenJeLand.add(schulenJeLand);
	}

	public long getTeilnehmendeSchulenGesamt() {

		return teilnehmendeSchulenGesamt;
	}

	public void setTeilnehmendeSchulenGesamt(final long teilnehmendeSchulenGesamt) {

		this.teilnehmendeSchulenGesamt = teilnehmendeSchulenGesamt;
	}

	public List<MkBiZaGruppierungsitem> getMedianeJeKlassenstufe() {

		return medianeJeKlassenstufe;
	}

	public void addMedianeJeKlassenstufe(final MkBiZaGruppierungsitem medianJeKlassenstufe) {

		this.medianeJeKlassenstufe.add(medianJeKlassenstufe);
	}

	public void setBeendet(final boolean beendet) {

		this.beendet = beendet;
	}
}
