// =====================================================
// Projekt: Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_kataloge.persistence.impl.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.egladil.web.commons_validation.annotations.Kuerzel;

/**
 * Schule
 */
@Entity
@Table(name = "kat_schulen")
public class Schule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Version
	@Column(name = "VERSION")
	private int version;

	@NotNull
	@Size(min = 1, max = 150)
	@Column(name = "NAME", length = 150)
	private String name;

	@NotNull
	@Kuerzel
	@Size(min = 8, max = 8)
	@Column(name = "KUERZEL", length = 8)
	private String kuerzel;

	@Column(name = "STRASSE", length = 100)
	@Size(min = 0, max = 100)
	private String strasse;

	@Column(name = "URL", length = 2000)
	@Size(min = 0, max = 2000)
	private String url;

	@Column(name = "SCHULTYP", length = 100)
	@Size(min = 0, max = 100)
	private String schultyp;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORT", referencedColumnName = "ID")
	private Ort ort;

	public String getName() {

		return name;
	}

	public void setName(final String name) {

		this.name = name;
	}

	public String getKuerzel() {

		return kuerzel;
	}

	public void setKuerzel(final String kuerzel) {

		this.kuerzel = kuerzel;
	}

	public Long getId() {

		return id;
	}

	public void setId(final Long id) {

		this.id = id;
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
		final Schule other = (Schule) obj;

		if (kuerzel == null) {

			if (other.kuerzel != null)
				return false;
		} else if (!kuerzel.equals(other.kuerzel))
			return false;
		return true;
	}

	public String getStrasse() {

		return strasse;
	}

	public void setStrasse(final String strasse) {

		this.strasse = strasse;
	}

	public String getUrl() {

		return url;
	}

	public void setUrl(final String url) {

		this.url = url;
	}

	public String getSchultyp() {

		return schultyp;
	}

	public void setSchultyp(final String schultyp) {

		this.schultyp = schultyp;
	}

	@Override
	public String toString() {

		final StringBuilder builder = new StringBuilder();
		builder.append("Schule [kuerzel=");
		builder.append(kuerzel);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @return Ort
	 */
	public Ort getOrt() {

		return ort;
	}

	/**
	 * @param ort
	 *            Ort
	 */
	public void setOrt(final Ort ort) {

		this.ort = ort;
	}
}
