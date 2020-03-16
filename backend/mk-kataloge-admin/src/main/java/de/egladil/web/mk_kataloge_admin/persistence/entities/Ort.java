// =====================================================
// Project: mk-kataloge-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge_admin.persistence.entities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.egladil.web.commons_validation.annotations.Kuerzel;

/**
 * Ort
 */
@Entity
@Table(name = "kat_orte")
public class Ort implements KatalogeAdminEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Version
	@Column(name = "VERSION")
	private int version;

	@NotNull
	@Size(min = 1, max = 100)
	@Column(name = "NAME")
	private String name;

	@Kuerzel
	@Size(min = 8, max = 8)
	@Column(name = "KUERZEL")
	private String kuerzel;

	@OneToMany(cascade = { CascadeType.ALL })
	private Set<Schule> schulen = new HashSet<>();

	public boolean addSchule(final Schule schule) {

		return this.schulen.add(schule);

	}

	public boolean removeSchule(final Schule schule) {

		return this.schulen.remove(schule);
	}

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
		Ort other = (Ort) obj;
		return Objects.equals(kuerzel, other.kuerzel);
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

	@Override
	public Long getId() {

		return id;
	}

	public void setId(final Long id) {

		this.id = id;
	}

	public Set<Schule> getSchulen() {

		return schulen;
	}
}
