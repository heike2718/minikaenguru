// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.mk_commons.domain.IMkEntity;
import de.egladil.web.mk_commons.domain.ITeilnahmeIdentifierProvider;
import de.egladil.web.mk_commons.domain.enums.Farbschema;
import de.egladil.web.mk_commons.domain.enums.Klassenstufe;
import de.egladil.web.mk_commons.domain.enums.Teilnahmeart;

/**
 * Auswertungsgruppe
 */
@Entity
@Table(name = "auswertungsgruppen")
public class Auswertungsgruppe implements IMkEntity, ITeilnahmeIdentifierProvider {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	@JsonIgnore
	private Long id;

	@Version
	@Column(name = "VERSION")
	@JsonIgnore
	private int version;

	@NotNull
	@Kuerzel
	@Size(min = 22, max = 22)
	@Column(name = "KUERZEL")
	@JsonProperty
	private String kuerzel;

	@NotNull
	@Column(name = "TEILNAHMEART")
	@Enumerated(EnumType.STRING)
	@JsonIgnore
	private Teilnahmeart teilnahmeart;

	@NotNull
	@Kuerzel
	@Size(min = 8, max = 8)
	@Column(name = "TEILNAHMEKUERZEL", length = 8)
	@JsonIgnore
	private String teilnahmekuerzel;

	@NotBlank
	@Column(name = "JAHR", length = 4)
	@JsonIgnore
	private String jahr;

	@StringLatin
	@Size(max = 110)
	@Column(name = "NAME")
	@JsonProperty
	private String name;

	@Column(name = "KLASSENSTUFE")
	@Enumerated(EnumType.STRING)
	@JsonIgnore
	private Klassenstufe klassenstufe;

	@Column(name = "FARBSCHEMA")
	@Enumerated(EnumType.STRING)
	@JsonIgnore
	private Farbschema farbschema;

	@UuidString
	@Size(max = 40)
	@Column(name = "GEAENDERT_DURCH")
	@JsonProperty
	private String geaendertDurch;

	@Embedded
	@JsonIgnore
	private IndividuellesUrkundenmotiv individuellesUrkundenmotiv;

	@ManyToOne
	@JoinColumn(name = "PARENT_ID")
	@JsonIgnore
	private Auswertungsgruppe parent;

	@Column(name = "TEILNEHMERUEBERSICHT")
	@Lob
	@JsonIgnore
	private String teilnehmeruebersicht;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = { CascadeType.ALL })
	@JsonIgnore
	private List<Auswertungsgruppe> auswertungsgruppen = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "auswertungsgruppe", cascade = { CascadeType.ALL })
	@JsonIgnore
	private List<Teilnehmer> alleTeilnehmer = new ArrayList<>();

	@Transient
	@JsonProperty
	private List<String> teilnahmejahre = new ArrayList<>();

	/**
	 * Auswertungsgruppe
	 */
	public Auswertungsgruppe() {

		super();
	}

	public void addAuswertungsgruppe(final Auswertungsgruppe auswertungsgruppe) {

		if (auswertungsgruppe != null && !auswertungsgruppen.contains(auswertungsgruppe)) {

			auswertungsgruppen.add(auswertungsgruppe);
			auswertungsgruppe.setParent(this);
		}
	}

	public void removeAuswertungsgruppe(final Auswertungsgruppe auswertungsgruppe) {

		if (auswertungsgruppe != null && auswertungsgruppen.remove(auswertungsgruppe)) {

			auswertungsgruppe.setParent(null);
		}
	}

	private void setParent(final Auswertungsgruppe parent) {

		this.parent = parent;
	}

	/**
	 * @param teilnehmer
	 *                   Teilnehmer
	 */
	public void addTeilnehmer(final Teilnehmer teilnehmer) {

		if (teilnehmer != null && !alleTeilnehmer.contains(teilnehmer)) {

			alleTeilnehmer.add(teilnehmer);
			teilnehmer.setAuswertungsgruppe(this);
		}
	}

	/**
	 * @param teilnehmer
	 *                   Teilnehmer
	 */
	public void removeTeilnehmer(final Teilnehmer teilnehmer) {

		if (teilnehmer != null && alleTeilnehmer.remove(teilnehmer)) {

			teilnehmer.setAuswertungsgruppe(null);
		}
	}

	@Override
	public TeilnahmeIdentifier provideTeilnahmeIdentifier() {

		return TeilnahmeIdentifier.create(teilnahmeart, teilnahmekuerzel, jahr);
	}

	@Override
	public String toString() {

		final StringBuilder builder = new StringBuilder();
		builder.append("Auswertungsgruppe [kuerzel=");
		builder.append(kuerzel);
		builder.append(", teilnahmeart=");
		builder.append(teilnahmeart);
		builder.append(", teilnahmekuerzel=");
		builder.append(teilnahmekuerzel);
		builder.append(", jahr=");
		builder.append(jahr);
		builder.append(", klassenstufe=");
		builder.append(klassenstufe);
		builder.append(", name=");
		builder.append(name);
		builder.append(", parent_id=");
		builder.append(parent != null ? parent.getId() : "null");
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
		final Auswertungsgruppe other = (Auswertungsgruppe) obj;

		if (kuerzel == null) {

			if (other.kuerzel != null) {

				return false;
			}
		} else if (!kuerzel.equals(other.kuerzel)) {

			return false;
		}
		return true;
	}

	public Long getId() {

		return id;
	}

	public void setId(final Long id) {

		this.id = id;
	}

	public String getJahr() {

		return jahr;
	}

	public void setJahr(final String jahr) {

		this.jahr = jahr;
	}

	public String getKuerzel() {

		return kuerzel;
	}

	public Teilnahmeart getTeilnahmeart() {

		return teilnahmeart;
	}

	public String getTeilnahmekuerzel() {

		return teilnahmekuerzel;
	}

	public String getName() {

		if (isRoot()) {

			return name;
		}
		return StringUtils.isBlank(name) ? ("Klassenstufe " + klassenstufe.getNummer()) : name;
	}

	/**
	 * @return
	 */
	public boolean isRoot() {

		return this.parent == null;
	}

	public Klassenstufe getKlassenstufe() {

		return klassenstufe;
	}

	public Farbschema getFarbschema() {

		return farbschema;
	}

	public IndividuellesUrkundenmotiv getIndividuellesUrkundenmotiv() {

		return individuellesUrkundenmotiv;
	}

	public Auswertungsgruppe getParent() {

		return parent;
	}

	public String getTeilnehmeruebersicht() {

		return teilnehmeruebersicht;
	}

	public List<Auswertungsgruppe> getAuswertungsgruppen() {

		return auswertungsgruppen;
	}

	public List<Teilnehmer> getAlleTeilnehmer() {

		return alleTeilnehmer;
	}

	public void setFarbschema(final Farbschema farbschema) {

		this.farbschema = farbschema;
	}

	public void setIndividuellesUrkundenmotiv(final IndividuellesUrkundenmotiv individuellesUrkundenmotiv) {

		this.individuellesUrkundenmotiv = individuellesUrkundenmotiv;
	}

	public void setTeilnehmeruebersicht(final String teilnehmeruebersicht) {

		this.teilnehmeruebersicht = teilnehmeruebersicht;
	}

	public void setKuerzel(final String kuerzel) {

		this.kuerzel = kuerzel;
	}

	public void setTeilnahmeart(final Teilnahmeart teilnahmeart) {

		this.teilnahmeart = teilnahmeart;
	}

	public void setTeilnahmekuerzel(final String teilnahmekuerzel) {

		this.teilnahmekuerzel = teilnahmekuerzel;
	}

	public void setName(final String name) {

		// TODO: durch mkv-server muss dafür gesorgt werden, dass name = new TrimShrinker().apply(name) vorher erfolgt.
		this.name = name;
	}

	public void setKlassenstufe(final Klassenstufe klassenstufe) {

		this.klassenstufe = klassenstufe;
	}

	public void setGeaendertDurch(final String geaendertDurch) {

		this.geaendertDurch = geaendertDurch;
	}

	public String getGeaendertDurch() {

		return geaendertDurch;
	}

	/**
	 * Sucht die Auswertungsgruppe mit dem gegebenen kuerzel
	 *
	 * @param  kuerzelGruppe
	 * @return               Auswertungsgruppe oder null
	 */
	public Auswertungsgruppe findAuswertungsgruppe(final String kuerzelGruppe) {

		for (final Auswertungsgruppe gruppe : auswertungsgruppen) {

			if (kuerzelGruppe.equals(gruppe.getKuerzel())) {

				return gruppe;
			}
		}
		return null;
	}

	public final List<String> getTeilnahmejahre() {

		return teilnahmejahre;
	}

	public final void setTeilnahmejahre(final List<String> teilnahmejahre) {

		this.teilnahmejahre = teilnahmejahre;
	}
}
