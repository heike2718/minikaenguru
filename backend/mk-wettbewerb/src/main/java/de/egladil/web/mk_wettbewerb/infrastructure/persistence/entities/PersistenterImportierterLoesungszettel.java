// =====================================================
// Projekt: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Size;

import de.egladil.web.mk_wettbewerb.domain.auswertungen.Auswertungsquelle;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Sprache;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahmeart;

/**
 * PersistenterImportierterLoesungszettel
 */
@Entity
@Table(name = "TEMP_LOESUNGSZETTEL")
public class PersistenterImportierterLoesungszettel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Version
	@Column(name = "VERSION")
	private int version;

	@Column(name = "KUERZEL")
	private String kuerzel;

	@Column(name = "NUMMER")
	private int nummer;

	@Column(name = "JAHR")
	private String jahr;

	@Column(name = "TEILNAHMEART")
	@Enumerated(EnumType.STRING)
	private Teilnahmeart teilnahmeart;

	@Column(name = "SPRACHE")
	@Enumerated(EnumType.STRING)
	private Sprache sprache;

	@Column(name = "TEILNAHMEKUERZEL")
	private String teilnahmekuerzel;

	@Column(name = "TEILNAHMENUMMER_NEU")
	private String teilnahmenummerNeu;

	@Column(name = "LANDKUERZEL")
	private String landkuerzel;

	@Column(name = "KLASSENSTUFE")
	@Enumerated(EnumType.STRING)
	private Klassenstufe klassenstufe;

	@Column(name = "QUELLE")
	@Enumerated(EnumType.STRING)
	private Auswertungsquelle auswertungsquelle;

	@Column(name = "TYPO")
	private boolean typo;

	@Column(name = "KAENGURUSPRUNG")
	private int kaengurusprung;

	@Column(name = "PUNKTE")
	private int punkte;

	/**
	 * Die Nutzereingabe ist ein kommaseparierter String, entweder von 'f,r,n' oder
	 * 'A,B,C,D,E,N'
	 */
	@Size(max = 100)
	@Column(name = "ORIGINALWERTUNG")
	private String nutzereingabe;

	/**
	 * Aneinanderreihung von 12 bzw. 15 Lösungsbuchstaben oder N (Beispiel
	 * 'EDNADECCCCNNABD')
	 */
	@Size(max = 15)
	@Column(name = "ANTWORTCODE")
	private String antwortcode;

	/**
	 * Aneinanderreihung von 12 bzw. 15 Bewertungen f,r,n (Beispiel 'fnnfffrrffrn')
	 */
	@Size(max = 15)
	@Column(name = "WERTUNGSCODE")
	private String wertungscode;

	/**
	 * Erzeugt eine Instanz von PersistenterImportierterLoesungszettel
	 */
	public PersistenterImportierterLoesungszettel() {

	}

	public Long getId() {

		return id;
	}

	public String getKuerzel() {

		return kuerzel;
	}

	public int getNummer() {

		return nummer;
	}

	public String getJahr() {

		return jahr;
	}

	public Teilnahmeart getTeilnahmeart() {

		return teilnahmeart;
	}

	public String getLandkuerzel() {

		return landkuerzel;
	}

	public Sprache getSprache() {

		return sprache;
	}

	public String getTeilnahmekuerzel() {

		return teilnahmekuerzel;
	}

	public Klassenstufe getKlassenstufe() {

		return klassenstufe;
	}

	public Auswertungsquelle getAuswertungsquelle() {

		return auswertungsquelle;
	}

	public boolean isTypo() {

		return typo;
	}

	public int getKaengurusprung() {

		return kaengurusprung;
	}

	public int getPunkte() {

		return punkte;
	}

	public String getNutzereingabe() {

		return nutzereingabe;
	}

	public String getAntwortcode() {

		return antwortcode;
	}

	public String getWertungscode() {

		return wertungscode;
	}

	public PersistenterImportierterLoesungszettel withId(final Long id) {

		this.id = id;
		return this;
	}

	public PersistenterImportierterLoesungszettel withKuerzel(final String kuerzel) {

		this.kuerzel = kuerzel;
		return this;
	}

	public PersistenterImportierterLoesungszettel withNummer(final int nummer) {

		this.nummer = nummer;
		return this;
	}

	public PersistenterImportierterLoesungszettel withJahr(final String jahr) {

		this.jahr = jahr;
		return this;
	}

	public PersistenterImportierterLoesungszettel withTeilnahmeart(final Teilnahmeart teilnahmeart) {

		this.teilnahmeart = teilnahmeart;
		return this;
	}

	public PersistenterImportierterLoesungszettel withSprache(final Sprache sprache) {

		this.sprache = sprache;
		return this;
	}

	public PersistenterImportierterLoesungszettel withTeilnahmekuerzel(final String teilnahmekuerzel) {

		this.teilnahmekuerzel = teilnahmekuerzel;
		return this;
	}

	public PersistenterImportierterLoesungszettel withLandkuerzel(final String landkuerzel) {

		this.landkuerzel = landkuerzel;
		return this;
	}

	public PersistenterImportierterLoesungszettel withKlassenstufe(final Klassenstufe klassenstufe) {

		this.klassenstufe = klassenstufe;
		return this;
	}

	public PersistenterImportierterLoesungszettel withAuswertungsquelle(final Auswertungsquelle auswertungsquelle) {

		this.auswertungsquelle = auswertungsquelle;
		return this;
	}

	public PersistenterImportierterLoesungszettel withTypo(final boolean typo) {

		this.typo = typo;
		return this;
	}

	public PersistenterImportierterLoesungszettel withKaengurusprung(final int kaengurusprung) {

		this.kaengurusprung = kaengurusprung;
		return this;
	}

	public PersistenterImportierterLoesungszettel withPunkte(final int punkte) {

		this.punkte = punkte;
		return this;
	}

	public PersistenterImportierterLoesungszettel withNutzereingabe(final String nutzereingabe) {

		this.nutzereingabe = nutzereingabe;
		return this;
	}

	public PersistenterImportierterLoesungszettel withAntwortcode(final String antwortcode) {

		this.antwortcode = antwortcode;
		return this;
	}

	public PersistenterImportierterLoesungszettel withWertungscode(final String wertungscode) {

		this.wertungscode = wertungscode;
		return this;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((kuerzel == null) ? 0 : kuerzel.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final PersistenterImportierterLoesungszettel other = (PersistenterImportierterLoesungszettel) obj;

		if (kuerzel == null) {

			if (other.kuerzel != null)
				return false;
		} else if (!kuerzel.equals(other.kuerzel))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "PersistenterImportierterLoesungszettel [id=" + id + ", jahr=" + jahr + ", teilnahmeart=" + teilnahmeart
			+ ", teilnahmekuerzel=" + teilnahmekuerzel + "]";
	}

	public void setTeilnahmenummerNeu(final String teilnahmenummerNeu) {

		this.teilnahmenummerNeu = teilnahmenummerNeu;
	}

	public String getTeilnahmenummerNeu() {

		return teilnahmenummerNeu;
	}

}
