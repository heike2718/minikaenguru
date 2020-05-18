// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.personen.Person;
import de.egladil.web.mk_wettbewerb.domain.semantik.Aggregate;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbID;

/**
 * Schulteilnahme
 */
@Aggregate
public class Schulteilnahme extends Teilnahme {

	private final String nameSchule;

	private final Person angemeldetDurch;

	/**
	 * @param wettbewerbID
	 * @param teilnahmenummer
	 */
	public Schulteilnahme(final WettbewerbID wettbewerbID, final Identifier teilnahmenummer, final String nameSchule, final Person angemeldetDurch) {

		super(wettbewerbID, teilnahmenummer);

		if (StringUtils.isBlank(nameSchule)) {

			throw new IllegalArgumentException("nameSchule darf nicht blank sein");
		}

		if (angemeldetDurch == null) {

			throw new IllegalArgumentException("angemeldetDurch darf nicht null sein");
		}
		this.nameSchule = nameSchule;
		this.angemeldetDurch = angemeldetDurch;

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
			+ teilnahmenummer().toString() + ", name=" + this.nameSchule + "]";
	}

	public Person angemeldetDurch() {

		return angemeldetDurch;
	}

}
