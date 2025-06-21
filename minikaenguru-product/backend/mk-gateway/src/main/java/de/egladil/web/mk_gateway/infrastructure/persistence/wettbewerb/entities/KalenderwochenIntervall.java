//=====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities;

/**
 *
 */
public class KalenderwochenIntervall {

	private final Integer minKw;

	private final Integer maxKw;

	/**
	 * @param minKw
	 * @param maxKw
	 */
	public KalenderwochenIntervall(Integer minKw, Integer maxKw) {
		super();
		this.minKw = minKw;
		this.maxKw = maxKw;
	}

	public Integer getMinKw() {
		return minKw;
	}

	public Integer getMaxKw() {
		return maxKw;
	}

}
