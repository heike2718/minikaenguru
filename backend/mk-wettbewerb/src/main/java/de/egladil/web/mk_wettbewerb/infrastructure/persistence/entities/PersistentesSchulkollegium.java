// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * PersistentesSchulkollegium
 */
@Entity
@Table(name = "SCHULKOLLEGIEN")
@NamedQueries(@NamedQuery(
	name = "FIND_SCHULKOLLEGIUM_BY_UUID", query = "select sk from PersistentesSchulkollegium sk where sk.uuid = :uuid"))
public class PersistentesSchulkollegium extends ConcurrencySafeEntity {

	public static final String FIND_BY_UUID_QUERY = "FIND_SCHULKOLLEGIUM_BY_UUID";

	@Column(name = "KOLLEGIUM")
	private String kollegium;

	public String getKollegium() {

		return kollegium;
	}

	public void setKollegium(final String kollegium) {

		this.kollegium = kollegium;
	}

}