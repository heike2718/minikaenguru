// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahmeart;

/**
 * PersistenteTeilnahme
 */
@Entity
@Table(name = "TEILNAHMEN")
public class PersistenteTeilnahme extends ConcurrencySafeEntity {

	@Column(name = "TEILNAHMEART")
	@Enumerated(EnumType.STRING)
	private Teilnahmeart teilnahmeart;

	@Column(name = "TEILNAHMENUMMER")
	private String teilnahmenummer;

	@Column(name = "WETTBEWERB_UUID")
	private String wettbewerbUUID;

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

}
