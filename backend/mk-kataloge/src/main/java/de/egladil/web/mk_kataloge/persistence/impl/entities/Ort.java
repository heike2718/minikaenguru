// =====================================================
// Projekt: Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_kataloge.persistence.impl.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.egladil.web.commons_validation.annotations.Kuerzel;

/**
 * Ort
 */
@Entity
@Table(name = "kat_orte")
public class Ort {

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

	@Transient
	private Long landId;

	@Transient
	private Land land;

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL }, mappedBy = "ort")
	private List<Schule> schulen = new ArrayList<>();

	public void addSchule(final Schule schule) {

		if (schulen == null) {

			schulen = new ArrayList<>();
		}

		if (!schulen.contains(schule)) {

			schulen.add(schule);
			schule.setOrt(this);
		}
	}

	public void removeSchule(final Schule schule) {

		if (schulen != null && schulen.remove(schule)) {

			schule.setOrt(null);
		}
	}

	public String getName() {

		return name;
	}

	public void setName(final String name) {

		this.name = name;
	}

	public Long getId() {

		return id;
	}

	public void setId(final Long id) {

		this.id = id;
	}

	/**
	 * Gibt nur beekannte Schulen zurück.
	 *
	 * @return List
	 */
	public List<Schule> getSchulen() {

		if (schulen == null) {

			return Collections.emptyList();
		}
		final List<Schule> result = new ArrayList<>(schulen.size());

		for (final Schule schule : schulen) {

			if (!"unbekannt".equalsIgnoreCase(schule.getName())) {

				result.add(schule);
			}
		}
		return result;
	}

	/**
	 * Gibt die ungefilterte Ortliste zurück.
	 *
	 * @return List
	 */
	public List<Schule> getAllSchulen() {

		return this.schulen;
	}

	public void setSchulen(final List<Schule> schulen) {

		this.schulen = schulen;
	}

	@Override
	public String toString() {

		return "Ort [name=" + name + ", kuerzel=" + kuerzel + "]";
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
		final Ort other = (Ort) obj;

		if (kuerzel == null) {

			if (other.kuerzel != null)
				return false;
		} else if (!kuerzel.equals(other.kuerzel))
			return false;
		return true;
	}

	public String getKuerzel() {

		return kuerzel;
	}

	public void setKuerzel(final String kuerzel) {

		this.kuerzel = kuerzel;
	}

	public final Long getLandId() {

		return landId;
	}

	public final void setLandId(final Long landId) {

		this.landId = landId;
	}

	public final Land getLand() {

		return land;
	}

	public final void setLand(final Land land) {

		this.land = land;
	}
}
