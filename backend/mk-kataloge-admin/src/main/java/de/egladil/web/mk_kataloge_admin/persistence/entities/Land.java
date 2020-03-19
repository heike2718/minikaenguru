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

/**
 * Land
 */
@Entity
@Table(name = "kat_laender")
public class Land implements KatalogeAdminEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Version
	@Column(name = "VERSION")
	private int version;

	@NotNull
	@Size(min = 1, max = 5)
	@Column(name = "KUERZEL")
	private String kuerzel;

	@NotNull
	@Size(min = 1, max = 100)
	@Column(name = "NAME")
	private String name;

	@Column(name = "FREIGESCHALTET")
	private boolean freigeschaltet;

	@OneToMany(cascade = { CascadeType.ALL })
	private Set<Ort> orte = new HashSet<>();

	public boolean addOrt(final Ort ort) {

		return this.orte.add(ort);
	}

	public boolean removeOrt(final Ort ort) {

		return this.orte.remove(ort);
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
		Land other = (Land) obj;
		return Objects.equals(kuerzel, other.kuerzel);
	}

	@Override
	public Long getId() {

		return id;
	}

	public void setId(final Long id) {

		this.id = id;
	}

	@Override
	public String getKuerzel() {

		return kuerzel;
	}

	public void setKuerzel(final String kuerzel) {

		this.kuerzel = kuerzel;
	}

	@Override
	public String getName() {

		return name;
	}

	public void setName(final String name) {

		this.name = name;
	}

	public boolean isFreigeschaltet() {

		return freigeschaltet;
	}

	public void setFreigeschaltet(final boolean freigeschaltet) {

		this.freigeschaltet = freigeschaltet;
	}

	public Set<Ort> getOrte() {

		return orte;
	}

}
