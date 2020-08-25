// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahmeart;

/**
 * PersistenteTeilnahme
 */
@Entity
@Table(name = "TEILNAHMEN")
@NamedQueries({ @NamedQuery(
	name = "TEILNAHMEN.FIND_BY_NUMMER",
	query = "select t from PersistenteTeilnahme t where t.teilnahmenummer = :teilnahmenummer") })
public class PersistenteTeilnahme extends ConcurrencySafeEntity {

	public static final String FIND_BY_NUMMER = "TEILNAHMEN.FIND_BY_NUMMER";

	@Column(name = "TEILNAHMEART")
	@Enumerated(EnumType.STRING)
	private Teilnahmeart teilnahmeart;

	@Column(name = "TEILNAHMENUMMER")
	@Size(max = 10)
	private String teilnahmenummer;

	@Column(name = "WETTBEWERB_UUID")
	@Size(max = 36)
	private String wettbewerbUUID;

	@Column(name = "SCHULNAME")
	@Size(max = 100)
	private String schulname;

	@Column(name = "ANGEMELDET_DURCH")
	@Size(max = 36)
	private String angemeldetDurch;

	public Teilnahmeart getTeilnahmeart() {

		return teilnahmeart;
	}

	public void setTeilnahmeart(final Teilnahmeart teilnahmeart) {

		this.teilnahmeart = teilnahmeart;
	}

	public String getTeilnahmenummer() {

		return teilnahmenummer;
	}

	public void setTeilnahmenummer(final String teilnahmenummer) {

		this.teilnahmenummer = teilnahmenummer;
	}

	public String getWettbewerbUUID() {

		return wettbewerbUUID;
	}

	public void setWettbewerbUUID(final String wettbewerbUUID) {

		this.wettbewerbUUID = wettbewerbUUID;
	}

	public String getSchulname() {

		return schulname;
	}

	public void setSchulname(final String schulname) {

		this.schulname = schulname;
	}

	public String getAngemeldetDurch() {

		return angemeldetDurch;
	}

	public void setAngemeldetDurch(final String angemeldetDurch) {

		this.angemeldetDurch = angemeldetDurch;
	}

}
