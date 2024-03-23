// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import de.egladil.web.mk_gateway.domain.mustertexte.Mustertextkategorie;

/**
 * PersistenterMustertextShort
 */
@Entity
@Table(name = "VW_MUSTERTEXTE_SHORTLIST")
@NamedQueries({
	@NamedQuery(
		name = "PersistenterMustertextShort.FIND_BY_KATEGORIE",
		query = "select m from PersistenterMustertextShort m where m.kategorie = :kategorie order by m.name")
})
public class PersistenterMustertextShort implements Serializable {

	private static final long serialVersionUID = 5816648549056930393L;

	public static final String FIND_BY_KATEGORIE = "PersistenterMustertextShort.FIND_BY_KATEGORIE";

	@Id
	@Column(name = "UUID")
	private String uuid;

	@Column(name = "KATEGORIE")
	@Enumerated(EnumType.STRING)
	private Mustertextkategorie kategorie;

	@Column
	private String name;

	public String getUuid() {

		return uuid;
	}

	public void setUuid(final String uuid) {

		this.uuid = uuid;
	}

	public Mustertextkategorie getKategorie() {

		return kategorie;
	}

	public void setKategorie(final Mustertextkategorie kategorie) {

		this.kategorie = kategorie;
	}

	public String getName() {

		return name;
	}

	public void setName(final String name) {

		this.name = name;
	}

}
