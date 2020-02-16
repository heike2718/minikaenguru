// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.persistence.impl.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * LandInverse
 */
@Entity
@Table(name = "mkverwaltung.VW_LAENDER")
public class LandInverse {

	@Id
	@Column(name = "ID")
	private Long id;

	@Column(name = "LAND_NAME")
	private String name;

	@Column(name = "LAND_KUERZEL")
	private String kuerzel;

	@Column(name = "ANZAHL_ORTE")
	private int anzahlOrte;

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

	public int getAnzahlOrte() {

		return anzahlOrte;
	}

	public void setAnzahlOrte(final int anzahlOrte) {

		this.anzahlOrte = anzahlOrte;
	}

	public Long getId() {

		return id;
	}

}
