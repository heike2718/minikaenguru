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
 * Land
 */
@Entity
@Table(name = "VW_LAENDER")
public class Land {

	@Id
	@Column(name = "KUERZEL")
	private String kuerzel;

	@Column(name = "NAME")
	private String name;

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
}
