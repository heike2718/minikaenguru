// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities;

import java.util.Date;

import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * PersistenterWettbewerb
 */
@Entity
@Table(name = "WETTBEWERBE")
@NamedQueries({ @NamedQuery(
	name = PersistenterWettbewerb.FIND_WETTBEWERB_BY_ID_QUERY, query = "select w from PersistenterWettbewerb w where w.uuid = :uuid"),
	@NamedQuery(
		name = PersistenterWettbewerb.LOAD_WETTBEWERBE_QUERY, query = "select w from PersistenterWettbewerb w order by w.uuid") })
public class PersistenterWettbewerb extends ConcurrencySafeEntity {

	private static final long serialVersionUID = -9054131483715063984L;

	public static final String LOAD_WETTBEWERBE_QUERY = "PersistenterWettbewerb.LOAD_WETTBEWERBE";

	public static final String FIND_WETTBEWERB_BY_ID_QUERY = "PersistenterWettbewerb.FIND_WETTBEWERB_BY_ID";

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

	@Column(name = "IKID")
	private String loesungsbuchstabenIkids;

	@Column(name = "EINS")
	private String loesungsbuchstabenKlasse1;

	@Column(name = "ZWEI")
	private String loesungsbuchstabenKlasse2;

	@Column(name = "MEDIAN_IKID")
	private Integer medianIkids;

	@Column(name = "MEDIAN_EINS")
	private Integer medianKlasseEins;

	@Column(name = "MEDIAN_ZWEI")
	private Integer medianKlasseZwei;

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

	public String getLoesungsbuchstabenIkids() {

		return loesungsbuchstabenIkids;
	}

	public void setLoesungsbuchstabenIkids(final String loesungsbuchstabenIkids) {

		this.loesungsbuchstabenIkids = loesungsbuchstabenIkids;
	}

	public String getLoesungsbuchstabenKlasse1() {

		return loesungsbuchstabenKlasse1;
	}

	public void setLoesungsbuchstabenKlasse1(final String loesungsbuchstabenKlasse1) {

		this.loesungsbuchstabenKlasse1 = loesungsbuchstabenKlasse1;
	}

	public String getLoesungsbuchstabenKlasse2() {

		return loesungsbuchstabenKlasse2;
	}

	public void setLoesungsbuchstabenKlasse2(final String loesungsbuchstabenKlasse2) {

		this.loesungsbuchstabenKlasse2 = loesungsbuchstabenKlasse2;
	}

	public Integer getMedianIkids() {

		return medianIkids;
	}

	public void setMedianIkids(final Integer medianIkids) {

		this.medianIkids = medianIkids;
	}

	public Integer getMedianKlasseEins() {

		return medianKlasseEins;
	}

	public void setMedianKlasseEins(final Integer medianKlasseEins) {

		this.medianKlasseEins = medianKlasseEins;
	}

	public Integer getMedianKlasseZwei() {

		return medianKlasseZwei;
	}

	public void setMedianKlasseZwei(final Integer medianKlasseZwei) {

		this.medianKlasseZwei = medianKlasseZwei;
	}

}
