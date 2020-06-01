// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb;

import java.time.LocalDate;
import java.time.Month;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Wettbewerb
 */
public class Wettbewerb {

	@JsonProperty
	private final WettbewerbID wettbewerbId;

	@JsonProperty
	private WettbewerbStatus status;

	@JsonProperty
	private LocalDate wettbewerbsbeginn;

	@JsonProperty
	private LocalDate wettbewerbsende;

	@JsonProperty
	private LocalDate datumFreischaltungLehrer;

	@JsonProperty
	private LocalDate datumFreischaltungPrivat;

	/**
	 * @param wettbewerbId
	 */
	public Wettbewerb(final WettbewerbID wettbewerbId) {

		if (wettbewerbId == null) {

			throw new IllegalArgumentException("wettbewerbId darf nicht null sein.");
		}

		this.wettbewerbId = wettbewerbId;

		this.wettbewerbsende = LocalDate.of(wettbewerbId.jahr(), Month.AUGUST, 1);

		this.status = WettbewerbStatus.ERFASST;
	}

	public Wettbewerb withWettbewerbsbeginn(final LocalDate wettbewerbsbeginn) {

		this.wettbewerbsbeginn = wettbewerbsbeginn;
		return this;
	}

	public Wettbewerb withWettbewerbsende(final LocalDate wettbewerbsende) {

		this.wettbewerbsende = wettbewerbsende;
		return this;
	}

	public Wettbewerb withDatumFreischaltungLehrer(final LocalDate datumFreischaltungLehrer) {

		this.datumFreischaltungLehrer = datumFreischaltungLehrer;
		return this;
	}

	public Wettbewerb withDatumFreischaltungPrivat(final LocalDate datumFreischaltungPrivat) {

		this.datumFreischaltungPrivat = datumFreischaltungPrivat;
		return this;
	}

	public Wettbewerb withStatus(final WettbewerbStatus status) {

		this.status = status;
		return this;
	}

	@Override
	public int hashCode() {

		return Objects.hash(wettbewerbId);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null) {

			return false;
		}

		if (getClass() != obj.getClass()) {

			return false;
		}
		Wettbewerb other = (Wettbewerb) obj;
		return Objects.equals(wettbewerbId, other.wettbewerbId);
	}

	@Override
	public String toString() {

		return this.wettbewerbId.jahr().toString();
	}

	public WettbewerbStatus status() {

		return status;
	}

	public void naechsterStatus() throws IllegalStateException {

		this.status = WettbewerbStatus.nextStatus(status);

	}

	public WettbewerbID id() {

		return wettbewerbId;
	}

	public LocalDate wettbewerbsbeginn() {

		return wettbewerbsbeginn;
	}

	public LocalDate wettbewerbsende() {

		return wettbewerbsende;
	}

	public LocalDate datumFreischaltungLehrer() {

		return datumFreischaltungLehrer;
	}

	public LocalDate datumFreischaltungPrivat() {

		return datumFreischaltungPrivat;
	}

}
