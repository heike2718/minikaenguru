// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * Ort
 */
@Entity
@Table(name = "VW_ORTE")
@NamedQueries({
	@NamedQuery(
		name = "ORT_QUERY_LOAD_ORTE_IN_LAND",
		query = "select o from Ort o where o.landKuerzel = :landKuerzel and o.name != :excluded"),
	@NamedQuery(
		name = "ORT_QUERY_LOAD_ORTE_WITH_LANDKUERZEL",
		query = "select o from Ort o where o.landKuerzel = :landKuerzel"),
	@NamedQuery(
		name = "ORT_QUERY_FIND_ORTE_IN_LAND",
		query = "select o from Ort o where o.landKuerzel = :landKuerzel and lower(o.name) like :name and o.name != :excluded"),
	@NamedQuery(
		name = "ORT_QUERY_FIND_ORTE_MIT_NAME",
		query = "select o from Ort o where lower(o.name) like :name and o.name != :excluded"),
	@NamedQuery(
		name = "ORT_FIND_BY_KUERZEL", query = "select o from Ort o where o.kuerzel = :kuerzel"),
	@NamedQuery(
		name = "ORT_COUNT_WITH_KUERZEL", query = "select count(o) from Ort o where o.kuerzel = :kuerzel"),
	@NamedQuery(
		name = "ORT_COUNT_IN_LAND", query = "select count(o) from Ort o where o.landKuerzel = :kuerzel and o.name != :excluded")
})
public class Ort {

	public static final String QUERY_LOAD_ORTE_IN_LAND = "ORT_QUERY_LOAD_ORTE_IN_LAND";

	public static final String QUERY_LOAD_ORTE_WITH_LANDKUERZEL = "ORT_QUERY_LOAD_ORTE_WITH_LANDKUERZEL";

	public static final String QUERY_FIND_ORTE_IN_LAND = "ORT_QUERY_FIND_ORTE_IN_LAND";

	public static final String QUERY_FIND_ORTE_MIT_NAME = "ORT_QUERY_FIND_ORTE_MIT_NAME";

	public static final String QUERY_FIND_ORT_BY_KUERZEL = "ORT_FIND_BY_KUERZEL";

	public static final String QUERY_COUNT_IN_LAND = "ORT_COUNT_IN_LAND";

	public static final String QUERY_COUNT_WITH_KUERZEL = "ORT_COUNT_WITH_KUERZEL";

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

	public String printForLog() {

		return "Ort [kuerzel=" + kuerzel + ", name=" + name + ", anzahlSchulen=" + anzahlSchulen + ", landKuerzel=" + landKuerzel
			+ ", landName=" + landName + "]";
	}

	@Override
	public String toString() {

		return "Ort [kuerzel=" + kuerzel + ", name=" + name + "]";
	}
}
