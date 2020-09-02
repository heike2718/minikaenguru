// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb;

import java.time.LocalDate;
import java.util.Objects;

import de.egladil.web.commons_net.time.CommonTimeUtils;

/**
 * Wettbewerb
 */
public class Wettbewerb {

	private final WettbewerbID wettbewerbId;

	private WettbewerbStatus status;

	private LocalDate wettbewerbsbeginn;

	private LocalDate wettbewerbsende;

	private LocalDate datumFreischaltungLehrer;

	private LocalDate datumFreischaltungPrivat;

	private String loesungsbuchstabenIkids;

	private String loesungsbuchstabenKlasse1;

	private String loesungsbuchstabenKlasse2;

	/**
	 * @param wettbewerbId
	 */
	public Wettbewerb(final WettbewerbID wettbewerbId) {

		if (wettbewerbId == null) {

			throw new IllegalArgumentException("wettbewerbId darf nicht null sein.");
		}

		this.wettbewerbId = wettbewerbId;

		// this.wettbewerbsende = LocalDate.of(wettbewerbId.jahr(), Month.AUGUST, 1);

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

	public Wettbewerb withLoesungsbuchstabenIKids(final String value) {

		this.loesungsbuchstabenIkids = value;
		return this;
	}

	public Wettbewerb withLoesungsbuchstabenKlasse1(final String value) {

		this.loesungsbuchstabenKlasse1 = value;
		return this;
	}

	public Wettbewerb withLoesungsbuchstabenKlasse2(final String value) {

		this.loesungsbuchstabenKlasse2 = value;
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

		if (this.status == WettbewerbStatus.ANMELDUNG && this.wettbewerbsbeginn == null) {

			this.wettbewerbsbeginn = CommonTimeUtils.now().toLocalDate();
		}

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

	public String loesungsbuchstabenIkids() {

		return loesungsbuchstabenIkids;
	}

	public String loesungsbuchstabenKlasse1() {

		return loesungsbuchstabenKlasse1;
	}

	public String loesungsbuchstabenKlasse2() {

		return loesungsbuchstabenKlasse2;
	}

	public boolean isBeendet() {

		return this.status == WettbewerbStatus.BEENDET;
	}

}
