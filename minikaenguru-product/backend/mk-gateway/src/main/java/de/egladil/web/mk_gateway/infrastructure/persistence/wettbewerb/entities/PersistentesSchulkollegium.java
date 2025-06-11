// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * PersistentesSchulkollegium
 */
@Entity
@Table(name = "SCHULKOLLEGIEN")
@NamedQueries(@NamedQuery(
	name = PersistentesSchulkollegium.FIND_BY_UUID, query = "select sk from PersistentesSchulkollegium sk where sk.uuid = :uuid"))
public class PersistentesSchulkollegium extends ConcurrencySafeEntity {

	private static final long serialVersionUID = 7765477655501550508L;

	public static final String FIND_BY_UUID = "PersistentesSchulkollegium.FIND_BY_UUID";

	@Column(name = "KOLLEGIUM")
	private String kollegium;

	public String getKollegium() {

		return kollegium;
	}

	public void setKollegium(final String kollegium) {

		this.kollegium = kollegium;
	}

}
