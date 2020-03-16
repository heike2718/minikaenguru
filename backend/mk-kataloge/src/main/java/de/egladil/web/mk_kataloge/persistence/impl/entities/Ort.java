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
 * Ort
 */
@Entity
@Table(name = "VW_ORTE")
public class Ort {

	@Id
	@Column(name = "KUERZEL")
	private String kuerzel;

	@Column(name = "NAME")
	private String name;

	@Column(name = "ANZAHL_SCHULEN")
	private int anzahlSchulen;

	@Column(name = "LAND_KUERZEL")
	private String landKuerzel;

	@Column(name = "LAND_NAME")
	private String landName;

	/**
	 *
	 */
	public Ort() {

	}

	public String getName() {

		return name;
	}

	public void setName(final String ortName) {

		this.name = ortName;
	}

	public String getKuerzel() {

		return kuerzel;
	}

	public void setKuerzel(final String ortKuerzel) {

		this.kuerzel = ortKuerzel;
	}

	public String getLandName() {

		return landName;
	}

	public void setLandName(final String landName) {

		this.landName = landName;
	}

	public String getLandKuerzel() {

		return landKuerzel;
	}

	public void setLandKuerzel(final String landKuerzel) {

		this.landKuerzel = landKuerzel;
	}

	public int getAnzahlSchulen() {

		return anzahlSchulen;
	}

	public void setAnzahlSchulen(final int anzahlSchulen) {

		this.anzahlSchulen = anzahlSchulen;
	}
}
