// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.impl;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.LandKuerzel;
import de.egladil.web.mk_commons.domain.ILoesungszettel;
import de.egladil.web.mk_commons.domain.IMkEntity;
import de.egladil.web.mk_commons.domain.ITeilnahmeIdentifierProvider;
import de.egladil.web.mk_commons.domain.enums.Sprache;
import de.egladil.web.mk_commons.domain.enums.Teilnahmeart;
import de.egladil.web.mk_commons.utils.KuerzelGenerator;

/**
 * Loesungszettel ist der Lösungszettel eines einzelnen Teilnehmers in einem Wettbewerb. teilnahmekuerzel, jahr und
 * teilnahmeart identifizieren die zugehörige Teilnahme.
 */
@Entity
@Table(name = "loesungszettel")
@SqlResultSetMappings({
	@SqlResultSetMapping(name = "loesungszettelResult", entities = @EntityResult(entityClass = Loesungszettel.class)) })
public class Loesungszettel implements IMkEntity, ILoesungszettel, ITeilnahmeIdentifierProvider {

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

	@Column(name = "NUMMER")
	private int nummer;

	@Column(name = "JAHR")
	private String jahr;

	@NotNull
	@Column(name = "TEILNAHMEART")
	@Enumerated(EnumType.STRING)
	private Teilnahmeart teilnahmeart;

	@NotNull
	@Column(name = "SPRACHE")
	@Enumerated(EnumType.STRING)
	private Sprache sprache;

	@Kuerzel
	@Column(name = "TEILNAHMEKUERZEL")
	private String teilnahmekuerzel;

	@LandKuerzel
	@Column(name = "LANDKUERZEL")
	private String landkuerzel;

	@NotNull
	@Embedded
	private LoesungszettelRohdaten loesungszettelRohdaten;

	public static class Builder {
		// Pflichtattribute
		private final String jahr;

		private final Teilnahmeart teilnahmeart;

		private final String teilnahmekuerzel;

		private final int nummer;

		private final String kuerzel;

		private final LoesungszettelRohdaten daten;

		private Sprache sprache;

		// optionale Attribute

		/**
		 * Erzeugt eine Instanz von Builder
		 */
		public Builder(final String jahr, final Teilnahmeart teilnahmeart, final String teilnahmekuerzel, final int nummer, final LoesungszettelRohdaten daten) {

			if (jahr == null) {

				throw new NullPointerException("Parameter jahr");
			}

			if (teilnahmeart == null) {

				throw new NullPointerException("Parameter teilnahmeart");
			}

			if (teilnahmekuerzel == null) {

				throw new NullPointerException("Parameter teilnahmekuerzel");
			}

			if (daten == null) {

				throw new NullPointerException("Parameter daten");
			}
			this.jahr = jahr;
			this.teilnahmeart = teilnahmeart;
			this.teilnahmekuerzel = teilnahmekuerzel;
			this.nummer = nummer;
			this.daten = daten;
			this.kuerzel = KuerzelGenerator.generateDefaultKuerzelWithTimestamp();
			this.sprache = Sprache.de;
		}

		public Builder withSprache(final Sprache sprache) {

			this.sprache = sprache;
			return this;
		}

		public Loesungszettel build() {

			return new Loesungszettel(this);
		}
	}

	/**
	 * Erzeugt eine Instanz von Loesungszettel
	 */
	public Loesungszettel() {

	}

	private Loesungszettel(final Builder builder) {

		loesungszettelRohdaten = builder.daten;
		jahr = builder.jahr;
		kuerzel = builder.kuerzel;
		nummer = builder.nummer;
		teilnahmeart = builder.teilnahmeart;
		teilnahmekuerzel = builder.teilnahmekuerzel;
		sprache = builder.sprache;
	}

	@Override
	public TeilnahmeIdentifier provideTeilnahmeIdentifier() {

		return TeilnahmeIdentifier.create(teilnahmeart, teilnahmekuerzel, jahr);
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
		final Loesungszettel other = (Loesungszettel) obj;

		if (kuerzel == null) {

			if (other.kuerzel != null)
				return false;
		} else if (!kuerzel.equals(other.kuerzel))
			return false;
		return true;
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

	public LoesungszettelRohdaten getLoesungszettelRohdaten() {

		return loesungszettelRohdaten;
	}

	public String getTeilnahmekuerzel() {

		return teilnahmekuerzel;
	}

	@Override
	public Integer getPunkte() {

		return loesungszettelRohdaten == null ? null : loesungszettelRohdaten.getPunkte();
	}

	@Override
	public Integer getKaengurusprung() {

		return loesungszettelRohdaten == null ? null : loesungszettelRohdaten.getKaengurusprung();
	}

	@Override
	public String getWertungscode() {

		return loesungszettelRohdaten == null ? null : loesungszettelRohdaten.getWertungscode();
	}

	@Override
	public String toString() {

		return "Loesungszettel [kuerzel=" + kuerzel + ", nummer=" + nummer + ", jahr=" + jahr + ", teilnahmeart=" + teilnahmeart
			+ ", teilnahmekuerzel=" + teilnahmekuerzel + ", landkuerzel=" + landkuerzel + ", loesungszettelRohdaten="
			+ loesungszettelRohdaten + "]";
	}

	public void setLoesungszettelRohdaten(final LoesungszettelRohdaten loesungszettelRohdaten) {

		this.loesungszettelRohdaten = loesungszettelRohdaten;
	}

	public Sprache getSprache() {

		return sprache;
	}

	public void setSprache(final Sprache sprache) {

		this.sprache = sprache;
	}
}
