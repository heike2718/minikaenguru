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
 * OrtInverse
 */
@Entity
@Table(name = "mkverwaltung.VW_ORTE")
public class OrtInverse {

	@Id
	@Column(name = "ID")
	private Long id;

	@Column(name = "ORT_NAME")
	private String name;

	@Column(name = "ORT_KUERZEL")
	private String kuerzel;

	@Column(name = "LAND_NAME")
	private String landName;

	@Column(name = "LAND_KUERZEL")
	private String landKuerzel;

	@Column(name = "ANZAHL_SCHULEN")
	private int anzahlSchulen;

	/**
	 *
	 */
	public OrtInverse() {

	}

	public Long getId() {

		return id;
	}

	public void setId(final Long id) {

		this.id = id;
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
