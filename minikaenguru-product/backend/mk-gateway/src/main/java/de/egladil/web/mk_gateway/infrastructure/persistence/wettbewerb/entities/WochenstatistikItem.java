//=====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "VW_WOCHENSTATISTIK")
@IdClass(WochenstatistikItemID.class)
@NamedQueries({
	@NamedQuery(name = WochenstatistikItem.FIND_WITH_WETTBEWERB_UUID, query = "select w from WochenstatistikItem w where w.wettbewerbUUID = :wettbewerbUUID order by w.woche")
})
public class WochenstatistikItem implements Comparable<WochenstatistikItem> {

	public static final String FIND_WITH_WETTBEWERB_UUID = "WochenstatistikItem.FIND_WITH_WETTBEWERB_UUID";

	@Id
	@Column(name = "WETTBEWERB_UUID")
	private String wettbewerbUUID;

	@Id
	@Column(name = "WOCHE")
	private Long woche;

	@Column(name = "ANZAHL_LOESUNGSZETTEL")
	private long anzahlLoesungszettel;

	@Override
	public int compareTo(WochenstatistikItem o) {
		return this.woche.intValue() - o.getWoche().intValue();
	}

	public String getWettbewerbUUID() {
		return wettbewerbUUID;
	}

	public void setWettbewerbUUID(String wettbewerbUUID) {
		this.wettbewerbUUID = wettbewerbUUID;
	}

	public Long getWoche() {
		return woche;
	}

	public void setWoche(Long woche) {
		this.woche = woche;
	}

	public long getAnzahlLoesungszettel() {
		return anzahlLoesungszettel;
	}

	public void setAnzahlLoesungszettel(long anzahlLoesungszettel) {
		this.anzahlLoesungszettel = anzahlLoesungszettel;
	}

}
