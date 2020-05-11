// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.semantik.Aggregate;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbID;

/**
 * Schulteilnahme
 */
@Aggregate
public class Schulteilnahme extends Teilnahme {

	private final String nameSchule;

	/**
	 * @param wettbewerbID
	 * @param teilnahmekuerzel
	 */
	public Schulteilnahme(final WettbewerbID wettbewerbID, final Identifier teilnahmekuerzel, final String nameSchule) {

		super(wettbewerbID, teilnahmekuerzel);

		if (StringUtils.isBlank(nameSchule)) {

			throw new IllegalArgumentException("nameSchule darf nicht blank sein");
		}
		this.nameSchule = nameSchule;

	}

	@Override
	public Teilnahmeart teilnahmeart() {

		return Teilnahmeart.SCHULE;
	}

	public String nameSchule() {

		return this.nameSchule;
	}

	@Override
	public String toString() {

		return "Schulteilnahme [wettbewerbID()=" + wettbewerbID().toString() + ", teilnahmekuerzel()="
			+ teilnahmekuerzel().toString() + ", name=" + this.nameSchule + "]";
	}

}
