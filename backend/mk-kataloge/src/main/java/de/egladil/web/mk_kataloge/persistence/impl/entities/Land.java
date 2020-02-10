// =====================================================
// Projekt: Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_kataloge.persistence.impl.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
public class Land {

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

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name = "LAND", referencedColumnName = "ID", nullable = false)
	private List<Ort> orte = new ArrayList<>();

	public void addOrt(final Ort ort) {

		if (orte == null) {

			orte = new ArrayList<>();
		}

		if (!orte.contains(ort)) {

			orte.add(ort);
		}
	}

	@Override
	public String toString() {

		return "Land [kuerzel=" + kuerzel + ", bezeichnung=" + name + "]";
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
		final Land other = (Land) obj;

		if (kuerzel == null) {

			if (other.kuerzel != null)
				return false;
		} else if (!kuerzel.equals(other.kuerzel))
			return false;
		return true;
	}

	public Optional<Ort> findOrt(final String ortkuerzel) {

		return orte.stream().filter(o -> ortkuerzel.equals(o.getKuerzel())).findFirst();
	}

	public String getKuerzel() {

		return kuerzel;
	}

	public void setKuerzel(final String kuerzel) {

		this.kuerzel = kuerzel;
	}

	public String getName() {

		return name;
	}

	public void setName(final String bezeichnung) {

		this.name = bezeichnung;
	}

	public Long getId() {

		return id;
	}

	public void setId(final Long id) {

		this.id = id;
	}

	/**
	 * Gibt nur bekannte orte Zurück.
	 *
	 * @return List
	 */
	public List<Ort> getOrte() {

		if (orte == null) {

			return Collections.emptyList();
		}
		final List<Ort> result = new ArrayList<>(orte.size());

		for (final Ort ort : orte) {

			if (!"unbekannt".equalsIgnoreCase(ort.getName())) {

				result.add(ort);
			}
		}
		return result;
	}

	/**
	 * Gibt die ungefilterte Ortliste zurück.
	 *
	 * @return List
	 */
	public List<Ort> getAllOrte() {

		return this.orte;
	}

	public void setOrte(final List<Ort> orte) {

		this.orte = orte;
	}

	public boolean isFreigeschaltet() {

		return freigeschaltet;
	}

	public void setFreigeschaltet(final boolean freigeschaltet) {

		this.freigeschaltet = freigeschaltet;
	}

}
