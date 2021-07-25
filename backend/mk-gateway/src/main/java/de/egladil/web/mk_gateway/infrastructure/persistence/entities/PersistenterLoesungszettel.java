// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;

/**
 * PersistenterLoesungszettel
 */
@Entity
@Table(name = "LOESUNGSZETTEL")
@NamedQueries({
	@NamedQuery(
		name = "PersistenterLoesungszettel.LOAD_ALL_WITH_TEILNAHME_IDENTIFIER",
		query = "select l from PersistenterLoesungszettel l where l.teilnahmenummer = :teilnahmenummer and l.wettbewerbUuid = :wettbewerbUuid and l.teilnahmeart = :teilnahmeart order by l.uuid"),
	@NamedQuery(
		name = "PersistenterLoesungszettel.LOAD_ALL_WITH_TEILNAHMENUMMER_AND_JAHR",
		query = "select l from PersistenterLoesungszettel l where l.teilnahmenummer = :teilnahmenummer and l.wettbewerbUuid = :wettbewerbUuid order by l.uuid"),
	@NamedQuery(
		name = "PersistenterLoesungszettel.LOAD_ALL_WITH_WETTBEWERBID",
		query = "select l from PersistenterLoesungszettel l where l.wettbewerbUuid = :wettbewerbUuid order by l.uuid"),
	@NamedQuery(
		name = "PersistenterLoesungszettel.LOAD_ALL_WITH_WETTBEWERBID_KLASSENSTUFE",
		query = "select l from PersistenterLoesungszettel l where l.wettbewerbUuid = :wettbewerbUuid and l.klassenstufe = :klassenstufe order by l.uuid"),
	@NamedQuery(
		name = "PersistenterLoesungszettel.FIND_LOESUNGSZETTEL_WITH_KIND",
		query = "select l from PersistenterLoesungszettel l where l.kindID = :kindID order by l.uuid"),

})
public class PersistenterLoesungszettel extends ConcurrencySafeEntity {

	private static final long serialVersionUID = 6846868705303269825L;

	public static final String LOAD_ALL_WITH_TEILNAHME_IDENTIFIER = "PersistenterLoesungszettel.LOAD_ALL_WITH_TEILNAHME_IDENTIFIER";

	public static final String LOAD_ALL_WITH_TEILNAHMENUMMER_AND_JAHR = "PersistenterLoesungszettel.LOAD_ALL_WITH_TEILNAHMENUMMER_AND_JAHR";

	public static final String LOAD_ALL_WITH_WETTBEWERBID = "PersistenterLoesungszettel.LOAD_ALL_WITH_WETTBEWERBID";

	public static final String LOAD_ALL_WITH_WETTBEWERBID_KLASSENSTUFE = "PersistenterLoesungszettel.LOAD_ALL_WITH_WETTBEWERBID_KLASSENSTUFE";

	public static final String FIND_LOESUNGSZETTEL_WITH_KIND = "PersistenterLoesungszettel.FIND_LOESUNGSZETTEL_WITH_KIND";

	@Column(name = "KIND_ID")
	private String kindID;

	@Column(name = "KLASSENSTUFE")
	@Enumerated(EnumType.STRING)
	private Klassenstufe klassenstufe;

	@Column(name = "TEILNAHMENUMMER")
	private String teilnahmenummer;

	@Column(name = "QUELLE")
	@Enumerated(EnumType.STRING)
	private Auswertungsquelle auswertungsquelle;

	@Column(name = "TEILNAHMEART")
	@Enumerated(EnumType.STRING)
	private Teilnahmeart teilnahmeart;

	@Column(name = "WETTBEWERB_UUID")
	private String wettbewerbUuid;

	@Column(name = "LANDKUERZEL")
	private String landkuerzel;

	@Column(name = "KAENGURUSPRUNG")
	private int kaengurusprung;

	@Column(name = "TYPO")
	private boolean typo;

	/**
	 * Die Nutzereingabe ist ein kommaseparierter String, entweder von 'f,r,n' oder
	 * 'A,B,C,D,E,N'
	 */
	@Column(name = "NUTZEREINGABE")
	private String nutzereingabe;

	@Column(name = "PUNKTE")
	private int punkte;

	/**
	 * Aneinanderreihung von 6, 12 bzw. 15 Lösungsbuchstaben oder N (Beispiel
	 * 'EDNADECCCCNNABD')
	 */
	@Column(name = "ANTWORTCODE")
	private String antwortcode;

	/**
	 * Aneinanderreihung von 6, 12 bzw. 15 Bewertungen f,r,n (Beispiel 'fnnfffrrffrn')
	 */
	@Column(name = "WERTUNGSCODE")
	private String wertungscode;

	@Column(name = "SPRACHE")
	@Enumerated(EnumType.STRING)
	private Sprache sprache;

	public String getKindID() {

		return kindID;
	}

	public Klassenstufe getKlassenstufe() {

		return klassenstufe;
	}

	public String getTeilnahmenummer() {

		return teilnahmenummer;
	}

	public Auswertungsquelle getAuswertungsquelle() {

		return auswertungsquelle;
	}

	public Teilnahmeart getTeilnahmeart() {

		return teilnahmeart;
	}

	public String getWettbewerbUuid() {

		return wettbewerbUuid;
	}

	public String getLandkuerzel() {

		return landkuerzel;
	}

	public int getKaengurusprung() {

		return kaengurusprung;
	}

	public boolean isTypo() {

		return typo;
	}

	public String getNutzereingabe() {

		return nutzereingabe;
	}

	public int getPunkte() {

		return punkte;
	}

	public String getAntwortcode() {

		return antwortcode;
	}

	public String getWertungscode() {

		return wertungscode;
	}

	public Sprache getSprache() {

		return sprache;
	}

	@Override
	public String toString() {

		return "PersistenterLoesungszettel [uuid=" + getUuid() + ", wettbewerbUuid=" + wettbewerbUuid + ", teilnahmeart="
			+ teilnahmeart
			+ ", teilnahmenummer=" + teilnahmenummer + ", kindID=" + kindID + "]";
	}

	public void setKindID(final String kindID) {

		this.kindID = kindID;
	}

	public void setKlassenstufe(final Klassenstufe klassenstufe) {

		this.klassenstufe = klassenstufe;
	}

	public void setTeilnahmenummer(final String teilnahmenummer) {

		this.teilnahmenummer = teilnahmenummer;
	}

	public void setAuswertungsquelle(final Auswertungsquelle auswertungsquelle) {

		this.auswertungsquelle = auswertungsquelle;
	}

	public void setTeilnahmeart(final Teilnahmeart teilnahmeart) {

		this.teilnahmeart = teilnahmeart;
	}

	public void setWettbewerbUuid(final String wettbewerbUuid) {

		this.wettbewerbUuid = wettbewerbUuid;
	}

	public void setLandkuerzel(final String landkuerzel) {

		this.landkuerzel = landkuerzel;
	}

	public void setKaengurusprung(final int kaengurusprung) {

		this.kaengurusprung = kaengurusprung;
	}

	public void setTypo(final boolean typo) {

		this.typo = typo;
	}

	public void setNutzereingabe(final String nutzereingabe) {

		this.nutzereingabe = nutzereingabe;
	}

	public void setPunkte(final int punkte) {

		this.punkte = punkte;
	}

	public void setAntwortcode(final String antwortcode) {

		this.antwortcode = antwortcode;
	}

	public void setWertungscode(final String wertungscode) {

		this.wertungscode = wertungscode;
	}

	public void setSprache(final Sprache sprache) {

		this.sprache = sprache;
	}

}
