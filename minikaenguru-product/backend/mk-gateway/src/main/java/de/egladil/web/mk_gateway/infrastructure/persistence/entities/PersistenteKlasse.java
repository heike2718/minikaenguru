// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * PersistenteKlasse
 */
@Entity
@Table(name = "KLASSEN")
@NamedQueries({
	@NamedQuery(
		name = "PersistenteKlasse.FIND_KLASSEN_WITH_SCHULE",
		query = "select k from PersistenteKlasse k where k.schulkuerzel = :schulkuerzel order by k.uuid")
})
public class PersistenteKlasse extends ConcurrencySafeEntity {

	private static final long serialVersionUID = 6030913019732246442L;

	public static final String FIND_KLASSEN_WITH_SCHULE = "PersistenteKlasse.FIND_KLASSEN_WITH_SCHULE";

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
