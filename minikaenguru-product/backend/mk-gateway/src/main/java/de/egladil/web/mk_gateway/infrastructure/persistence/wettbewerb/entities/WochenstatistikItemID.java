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

	private int jahr;

	private int woche;

	@Override
	public int hashCode() {
		return Objects.hash(jahr, woche);
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
		return Objects.equals(jahr, other.jahr) && Objects.equals(woche, other.woche);
	}

	/**
	 *
	 */
	WochenstatistikItemID() {
		super();
	}

	/**
	 * @param jahr
	 * @param woche
	 */
	public WochenstatistikItemID(int jahr, int woche) {
		super();
		this.jahr = jahr;
		this.woche = woche;
	}

}
