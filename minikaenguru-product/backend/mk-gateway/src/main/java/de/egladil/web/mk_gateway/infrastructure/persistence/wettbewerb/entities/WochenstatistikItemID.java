//=====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 */
public class WochenstatistikItemID implements Serializable {

	private static final long serialVersionUID = 7606486638886570700L;

	private String wettbewerbUUID;

	private Long woche;

	@Override
	public int hashCode() {
		return Objects.hash(wettbewerbUUID, woche);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WochenstatistikItemID other = (WochenstatistikItemID) obj;
		return Objects.equals(wettbewerbUUID, other.wettbewerbUUID) && Objects.equals(woche, other.woche);
	}

	/**
	 *
	 */
	WochenstatistikItemID() {
		super();
	}

	/**
	 * @param wettbewerbUUID
	 * @param woche
	 */
	public WochenstatistikItemID(String wettbewerbUUID, Long woche) {
		super();
		this.wettbewerbUUID = wettbewerbUUID;
		this.woche = woche;
	}

}
