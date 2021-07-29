// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * UploadAuswertungContext
 */
public class UploadAuswertungContext {

	private String kuerzelLand;

	private Integer wettbewerbsjahr;

	private Sprache sprache;

	private Rolle rolle;

	public String getKuerzelLand() {

		return kuerzelLand;
	}

	public UploadAuswertungContext withKuerzelLand(final String kuerzelLand) {

		this.kuerzelLand = kuerzelLand;
		return this;
	}

	public Integer getWettbewerbsjahr() {

		return wettbewerbsjahr;
	}

	public UploadAuswertungContext withWettbewerbsjahr(final Integer wettbewerbsjahr) {

		this.wettbewerbsjahr = wettbewerbsjahr;
		return this;
	}

	public Sprache getSprache() {

		return sprache;
	}

	public UploadAuswertungContext withSprache(final Sprache sprache) {

		this.sprache = sprache;
		return this;
	}

	public Rolle getRolle() {

		return rolle;
	}

	public void setRolle(final Rolle rolle) {

		this.rolle = rolle;
	}

}
