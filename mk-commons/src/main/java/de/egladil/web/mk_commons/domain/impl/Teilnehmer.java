// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.mk_commons.domain.IMkEntity;
import de.egladil.web.mk_commons.domain.ITeilnahmeIdentifierProvider;
import de.egladil.web.mk_commons.domain.enums.Antworteingabemodus;
import de.egladil.web.mk_commons.domain.enums.Klassenstufe;
import de.egladil.web.mk_commons.domain.enums.Sprache;
import de.egladil.web.mk_commons.domain.enums.Teilnahmeart;

/**
 * Teilnehmer kapselt die Attribute eines Teilnehmers, die für die AuswertungDownload relevant sind. Eine Referenz auf eine
 * Klassenstufe ist nicht erforderlich
 */
@Entity
@Table(name = "teilnehmer")
public class Teilnehmer implements IMkEntity, ITeilnahmeIdentifierProvider {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Version
	@Column(name = "VERSION")
	private int version;

	@NotNull
	@Kuerzel
	@Size(min = 22, max = 22)
	@Column(name = "KUERZEL")
	private String kuerzel;

	@NotNull
	@Column(name = "TEILNAHMEART")
	@Enumerated(EnumType.STRING)
	private Teilnahmeart teilnahmeart;

	@NotNull
	@Kuerzel
	@Size(min = 8, max = 8)
	@Column(name = "TEILNAHMEKUERZEL")
	private String teilnahmekuerzel;

	@NotNull
	@Size(min = 4, max = 4)
	@Column(name = "JAHR")
	private String jahr;

	@NotNull
	@Column(name = "KLASSENSTUFE")
	@Enumerated(EnumType.STRING)
	private Klassenstufe klassenstufe;

	@Column(name = "NUMMER")
	private int nummer;

	@StringLatin
	@NotBlank
	@Size(max = 55)
	@Column(name = "VORNAME")
	private String vorname;

	@StringLatin
	@Size(max = 55)
	@Column(name = "NACHNAME")
	private String nachname;

	@StringLatin
	@Size(max = 55)
	@Column(name = "ZUSATZ")
	private String zusatz;

	@NotNull
	@Column(name = "SPRACHE")
	@Enumerated(EnumType.STRING)
	private Sprache sprache = Sprache.de;

	@Kuerzel
	@Size(max = 22)
	@Column(name = "LOESUNGSZETTELKUERZEL")
	private String loesungszettelkuerzel;

	@Transient
	private String antwortcode;

	@Transient
	private Integer kaengurusprung;

	@Transient
	private String punkte;

	@ManyToOne()
	@JoinColumn(name = "AUSWERTUNGSGRUPPE")
	private Auswertungsgruppe auswertungsgruppe;

	@Column(name = "EINGABEMODUS")
	@Enumerated(EnumType.STRING)
	private Antworteingabemodus eingabemodus;

	@UuidString
	@Size(max = 40)
	@Column(name = "GEAENDERT_DURCH")
	@JsonProperty
	private String geaendertDurch;

	/**
	 * Teilnehmer
	 */
	public Teilnehmer() {

	}

	@Override
	public TeilnahmeIdentifier provideTeilnahmeIdentifier() {

		return TeilnahmeIdentifier.create(teilnahmeart, teilnahmekuerzel, jahr);
	}

	public String getVorname() {

		return vorname;
	}

	public String getNachname() {

		return nachname;
	}

	public void setVorname(final String vorname) {

		// TODO: durch mkv-server muss dafür gesorgt werden, dass name = new TrimShrinker().apply(name) vorher erfolgt.
		this.vorname = vorname;
	}

	public void setNachname(final String nachname) {

		// TODO: durch mkv-server muss dafür gesorgt werden, dass name = new TrimShrinker().apply(name) vorher erfolgt.
		this.nachname = nachname;
	}

	public Long getId() {

		return id;
	}

	public void setId(final Long id) {

		this.id = id;
	}

	public Teilnahmeart getTeilnahmeart() {

		return teilnahmeart;
	}

	public void setTeilnahmeart(final Teilnahmeart teilnahmeart) {

		this.teilnahmeart = teilnahmeart;
	}

	public String getTeilnahmekuerzel() {

		return teilnahmekuerzel;
	}

	public void setTeilnahmekuerzel(final String teilnahmekuerzel) {

		this.teilnahmekuerzel = teilnahmekuerzel;
	}

	public String getJahr() {

		return jahr;
	}

	public void setJahr(final String jahr) {

		this.jahr = jahr;
	}

	public String getLoesungszettelkuerzel() {

		return loesungszettelkuerzel;
	}

	public void setLoesungszettelkuerzel(final String loesungszettelKuerzel) {

		this.loesungszettelkuerzel = loesungszettelKuerzel;
	}

	public String getZusatz() {

		return zusatz;
	}

	public void setZusatz(final String bemerkung) {

		// TODO: durch mkv-server muss dafür gesorgt werden, dass name = new TrimShrinker().apply(name) vorher erfolgt.
		this.zusatz = bemerkung;
	}

	@Override
	public String toString() {

		final StringBuilder builder = new StringBuilder();
		builder.append("Teilnehmer [kuerzel=");
		builder.append(kuerzel);
		builder.append(", ");
		builder.append(provideTeilnahmeIdentifier().toString());
		builder.append(", vorname=");
		builder.append(vorname);
		builder.append(", nachname=");
		builder.append(nachname);
		builder.append(", loesungszettelkuerzel=");
		builder.append(loesungszettelkuerzel);
		builder.append("]");
		return builder.toString();
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

		if (this == obj) {

			return true;
		}

		if (obj == null) {

			return false;
		}

		if (getClass() != obj.getClass()) {

			return false;
		}
		final Teilnehmer other = (Teilnehmer) obj;

		if (kuerzel == null) {

			if (other.kuerzel != null) {

				return false;
			}
		} else if (!kuerzel.equals(other.kuerzel)) {

			return false;
		}
		return true;
	}

	public String getKuerzel() {

		return kuerzel;
	}

	public void setKuerzel(final String kuerzel) {

		this.kuerzel = kuerzel;
	}

	public void setAuswertungsgruppe(final Auswertungsgruppe auswertungsgruppe) {

		this.auswertungsgruppe = auswertungsgruppe;
	}

	public Auswertungsgruppe getAuswertungsgruppe() {

		return auswertungsgruppe;
	}

	public Klassenstufe getKlassenstufe() {

		return klassenstufe;
	}

	public void setKlassenstufe(final Klassenstufe klassenstufe) {

		this.klassenstufe = klassenstufe;
	}

	public int getNummer() {

		return nummer;
	}

	public void setNummer(final int nummer) {

		this.nummer = nummer;
	}

	public String getAntwortcode() {

		return antwortcode;
	}

	public void setAntwortcode(final String antwortcode) {

		this.antwortcode = antwortcode;
	}

	public Integer getKaengurusprung() {

		return kaengurusprung;
	}

	public void setKaengurusprung(final Integer kaengurusprung) {

		this.kaengurusprung = kaengurusprung;
	}

	public String getPunkte() {

		return punkte;
	}

	public void setPunkte(final String punkte) {

		this.punkte = punkte;
	}

	public Antworteingabemodus getEingabemodus() {

		return eingabemodus;
	}

	public void setEingabemodus(final Antworteingabemodus eingabemodus) {

		this.eingabemodus = eingabemodus;
	}

	public String getGeaendertDurch() {

		return geaendertDurch;
	}

	public void setGeaendertDurch(final String geaendertDurch) {

		this.geaendertDurch = geaendertDurch;
	}

	public Sprache getSprache() {

		return sprache;
	}

	public void setSprache(final Sprache sprache) {

		this.sprache = sprache;
	}
}
