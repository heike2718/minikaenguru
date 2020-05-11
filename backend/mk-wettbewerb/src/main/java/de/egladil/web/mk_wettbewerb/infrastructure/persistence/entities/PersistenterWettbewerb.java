// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbStatus;

/**
 * PersistenterWettbewerb
 */
@Entity
@Table(name = "WETTBEWERBE")
public class PersistenterWettbewerb extends ConcurrencySafeEntity {

	@Column
	@Enumerated(EnumType.STRING)
	private WettbewerbStatus status;

	@Column(name = "BEGINN")
	private Date wettbewerbsbeginn;

	@Column(name = "ENDE")
	private Date wettbewerbsende;

	@Column(name = "FREISCHALTUNG_LEHRER")
	private Date datumFreischaltungLehrer;

	@Column(name = "FREISCHALTUNG_PRIVAT")
	private Date datumFreischaltungPrivat;

	public WettbewerbStatus getStatus() {

		return status;
	}

	public void setStatus(final WettbewerbStatus status) {

		this.status = status;
	}

	public Date getWettbewerbsbeginn() {

		return wettbewerbsbeginn;
	}

	public void setWettbewerbsbeginn(final Date wettbewerbsbeginn) {

		this.wettbewerbsbeginn = wettbewerbsbeginn;
	}

	public Date getWettbewerbsende() {

		return wettbewerbsende;
	}

	public void setWettbewerbsende(final Date wettbewerbsende) {

		this.wettbewerbsende = wettbewerbsende;
	}

	public Date getDatumFreischaltungLehrer() {

		return datumFreischaltungLehrer;
	}

	public void setDatumFreischaltungLehrer(final Date datumFreischaltungLehrer) {

		this.datumFreischaltungLehrer = datumFreischaltungLehrer;
	}

	public Date getDatumFreischaltungPrivat() {

		return datumFreischaltungPrivat;
	}

	public void setDatumFreischaltungPrivat(final Date datumFreischaltungPrivat) {

		this.datumFreischaltungPrivat = datumFreischaltungPrivat;
	}

}
