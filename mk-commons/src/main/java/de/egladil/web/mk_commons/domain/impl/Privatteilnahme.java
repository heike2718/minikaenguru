// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.mk_commons.domain.IMkEntity;
import de.egladil.web.mk_commons.domain.ITeilnahmeIdentifierProvider;

/**
 * Privatteilnahme
 */
@Entity
@Table(name = "privatteilnahmen")
public class Privatteilnahme implements IMkEntity, ITeilnahmeIdentifierProvider {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Version
	@Column(name = "VERSION")
	private int version;

	@NotBlank
	@Column(name = "JAHR", length = 4)
	private String jahr;

	@NotNull
	@Kuerzel
	@Size(min = 8, max = 8)
	@Column(name = "KUERZEL", length = 8)
	private String kuerzel;

	/**
	 * Erzeugt eine Instanz von Privatteilnahme
	 */
	public Privatteilnahme() {

	}

	/**
	 * Erzeugt eine neue Privatteilnahme mit kuerzel und Jahr. Das kuerzel wird neu generiert.
	 *
	 * @param jahr
	 *                String das Jahr
	 * @param kuerzel
	 *                String das kuerzel
	 */
	public Privatteilnahme(final String jahr, final String kuerzel) {

		this.kuerzel = kuerzel;
		this.jahr = jahr;
	}

	@Override
	public TeilnahmeIdentifier provideTeilnahmeIdentifier() {

		return TeilnahmeIdentifier.createPrivatteilnahmeIdentifier(kuerzel, jahr);
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

	public void setKuerzel(final String kuerzel) {

		this.kuerzel = kuerzel;
	}

	@Override
	public String toString() {

		final StringBuilder builder = new StringBuilder();
		builder.append("Privatteilnahme [kuerzel=");
		builder.append(kuerzel);
		builder.append(", jahr=");
		builder.append(jahr);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((jahr == null) ? 0 : jahr.hashCode());
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
		final Privatteilnahme other = (Privatteilnahme) obj;

		if (jahr == null) {

			if (other.jahr != null) {

				return false;
			}
		} else if (!jahr.equals(other.jahr)) {

			return false;
		}

		if (kuerzel == null) {

			if (other.kuerzel != null) {

				return false;
			}
		} else if (!kuerzel.equals(other.kuerzel)) {

			return false;
		}
		return true;
	}
}
