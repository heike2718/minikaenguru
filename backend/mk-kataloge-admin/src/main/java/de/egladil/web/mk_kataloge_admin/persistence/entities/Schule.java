// =====================================================
// Project: mk-kataloge-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge_admin.persistence.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class Schule implements KatalogeAdminEntity {

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

	@Override
	public int hashCode() {

		return Objects.hash(kuerzel);
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
		Schule other = (Schule) obj;
		return Objects.equals(kuerzel, other.kuerzel);
	}

	@Override
	public String toString() {

		return "Schule [name=" + name + ", kuerzel=" + kuerzel + "]";
	}

	@Override
	public Long getId() {

		return id;
	}

	public void setId(final Long id) {

		this.id = id;
	}

	@Override
	public String getName() {

		return name;
	}

	public void setName(final String name) {

		this.name = name;
	}

	@Override
	public String getKuerzel() {

		return kuerzel;
	}

	public void setKuerzel(final String kuerzel) {

		this.kuerzel = kuerzel;
	}

	public String getStrasse() {

		return strasse;
	}

	public void setStrasse(final String strasse) {

		this.strasse = strasse;
	}

}
