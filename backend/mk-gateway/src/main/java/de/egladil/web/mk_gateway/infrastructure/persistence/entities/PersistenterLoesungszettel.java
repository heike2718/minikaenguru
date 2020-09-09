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

import de.egladil.web.mk_gateway.domain.auswertungen.Auswertungsquelle;
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
		name = "PersistenterLoesungszettel.LOAD_ALL_WITH_IDENTIFIER",
		query = "select l from PersistenterLoesungszettel l where l.teilnahmenummer = :teilnahmenummer and l.wettbewerbUuid = :wettbewerbUuid and l.teilnahmeart = :teilnahmeart")

})
public class PersistenterLoesungszettel extends ConcurrencySafeEntity {

	public static final String LOAD_ALL_WITH_IDENTIFIER = "PersistenterLoesungszettel.LOAD_ALL_WITH_IDENTIFIER";

	@Column(name = "NUMMER")
	private int nummer;

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

	public int getNummer() {

		return nummer;
	}

	public PersistenterLoesungszettel withNummer(final int nummer) {

		this.nummer = nummer;
		return this;
	}

	public Klassenstufe getKlassenstufe() {

		return klassenstufe;
	}

	public PersistenterLoesungszettel withKlassenstufe(final Klassenstufe klassenstufe) {

		this.klassenstufe = klassenstufe;
		return this;
	}

	public String getTeilnahmenummer() {

		return teilnahmenummer;
	}

	public PersistenterLoesungszettel withTeilnahmenummer(final String teilnahmenummer) {

		this.teilnahmenummer = teilnahmenummer;
		return this;
	}

	public Auswertungsquelle getAuswertungsquelle() {

		return auswertungsquelle;
	}

	public PersistenterLoesungszettel withAuswertungsquelle(final Auswertungsquelle auswertungsquelle) {

		this.auswertungsquelle = auswertungsquelle;
		return this;
	}

	public Teilnahmeart getTeilnahmeart() {

		return teilnahmeart;
	}

	public PersistenterLoesungszettel withTeilnahmeart(final Teilnahmeart teilnahmeart) {

		this.teilnahmeart = teilnahmeart;
		return this;
	}

	public String getWettbewerbUuid() {

		return wettbewerbUuid;
	}

	public PersistenterLoesungszettel withWettbewerbUuid(final String wettbewerbUuid) {

		this.wettbewerbUuid = wettbewerbUuid;
		return this;
	}

	public String getLandkuerzel() {

		return landkuerzel;
	}

	public PersistenterLoesungszettel withLandkuerzel(final String landkuerzel) {

		this.landkuerzel = landkuerzel;
		return this;
	}

	public int getKaengurusprung() {

		return kaengurusprung;
	}

	public PersistenterLoesungszettel withKaengurusprung(final int kaengurusprung) {

		this.kaengurusprung = kaengurusprung;
		return this;
	}

	public boolean isTypo() {

		return typo;
	}

	public PersistenterLoesungszettel withTypo(final boolean typo) {

		this.typo = typo;
		return this;
	}

	public String getNutzereingabe() {

		return nutzereingabe;
	}

	public PersistenterLoesungszettel withNutzereingabe(final String originalwerbung) {

		this.nutzereingabe = originalwerbung;
		return this;
	}

	public int getPunkte() {

		return punkte;
	}

	public PersistenterLoesungszettel withPunkte(final int punkte) {

		this.punkte = punkte;
		return this;
	}

	public String getAntwortcode() {

		return antwortcode;
	}

	public PersistenterLoesungszettel withAntwortcode(final String antwortcode) {

		this.antwortcode = antwortcode;
		return this;
	}

	public String getWertungscode() {

		return wertungscode;
	}

	public PersistenterLoesungszettel withWertungscode(final String wertungscode) {

		this.wertungscode = wertungscode;
		return this;
	}

	public Sprache getSprache() {

		return sprache;
	}

	public PersistenterLoesungszettel withSprache(final Sprache sprache) {

		this.sprache = sprache;
		return this;
	}

	@Override
	public String toString() {

		return "PersistenterLoesungszettel [uuid=" + getUuid() + ", wettbewerbUuid=" + wettbewerbUuid + ", teilnahmeart="
			+ teilnahmeart
			+ ", teilnahmenummer=" + teilnahmenummer + ", nummer=" + nummer + "]";
	}

}
