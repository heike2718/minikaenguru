// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Land
 */
@Entity
@Table(name = "VW_LAENDER")
@NamedQueries({
	@NamedQuery(name = "LAND_QUERY_LOAD_LAENDER", query = "select l from Land l where l.name != :excluded"),
	@NamedQuery(name = "LAENDER_COUNT_WITH_KUERZEL", query = "select count(l) from Land l where l.kuerzel = :kuerzel"),
	@NamedQuery(
		name = "LAND_QUERY_FIND_LAENDER_MIT_NAME",
		query = "select l from Land l where lower(l.name) like :name and l.name != :excluded"),
	@NamedQuery(name = "LAND_FIND_BY_KURZEL", query = "select l from Land l where l.kuerzel = :kuerzel")
})
public class Land {

	public static final String QUERY_FIND_BY_KUEZEL = "LAND_FIND_BY_KURZEL";

	public static final String QUERY_LOAD_LAENDER = "LAND_QUERY_LOAD_LAENDER";

	public static final String QUERY_FIND_LAENDER_MIT_NAME = "LAND_QUERY_FIND_LAENDER_MIT_NAME";

	public static final String QUERY_COUNT_WITH_KUERZEL = "LAENDER_COUNT_WITH_KUERZEL";

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
