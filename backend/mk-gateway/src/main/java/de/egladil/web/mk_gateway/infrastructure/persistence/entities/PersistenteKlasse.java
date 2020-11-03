// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * PersistenteKlasse
 */
@Entity
@Table(name = "KLASSEN")
public class PersistenteKlasse extends ConcurrencySafeEntity {

	private static final long serialVersionUID = 6030913019732246442L;

	@Column(name = "SCHULKUERZEL")
	private String schulkuerzel;

	@Column(name = "NAME")
	private String name;

	public String getSchulkuerzel() {

		return schulkuerzel;
	}

	public void setSchulkuerzel(final String schulkuerzel) {

		this.schulkuerzel = schulkuerzel;
	}

	public String getName() {

		return name;
	}

	public void setName(final String name) {

		this.name = name;
	}

}
