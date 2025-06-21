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
	@NamedQuery(name = WochenstatistikItem.FIND_WITH_WETTBEWERB_UUID, query = "select w from WochenstatistikItem w where w.jahr = :jahr order by w.woche") })
public class WochenstatistikItem implements Comparable<WochenstatistikItem> {

	public static final String FIND_WITH_WETTBEWERB_UUID = "WochenstatistikItem.FIND_WITH_WETTBEWERB_UUID";

	@Id
	@Column(name = "JAHR")
	private int jahr;

	@Id
	@Column(name = "KALENDERWOCHE")
	private int woche;

	@Column(name = "ANZAHL")
	private int anzahlLoesungszettel;

	/**
	 *
	 */
	public WochenstatistikItem() {
	}

	/**
	 * Nützlich für Tests. Da spielt die jahr keine Rolle.
	 *
	 * @param woche int
	 * @param anzahlLoesungszettel int
	 */
	public WochenstatistikItem(int woche, int anzahlLoesungszettel) {
		super();
		this.woche = woche;
		this.anzahlLoesungszettel = anzahlLoesungszettel;
	}

	@Override
	public int compareTo(WochenstatistikItem o) {
		return this.woche - o.getWoche();
	}

	@Override
	public String toString() {
		return "WochenstatistikItem [jahr=" + jahr + ", woche=" + woche + ", anzahlLoesungszettel="
			+ anzahlLoesungszettel + "]";
	}

	public int getWoche() {
		return woche;
	}

	public void setWoche(int woche) {
		this.woche = woche;
	}

	public int getAnzahlLoesungszettel() {
		return anzahlLoesungszettel;
	}

	public void setAnzahlLoesungszettel(int anzahlLoesungszettel) {
		this.anzahlLoesungszettel = anzahlLoesungszettel;
	}

	public int getJahr() {
		return jahr;
	}

	public void setJahr(int jahr) {
		this.jahr = jahr;
	}

}
