// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;

/**
 * UploadAuswertungContext
 */
public class UploadAuswertungContext {

	private String kuerzelLand;

	private Sprache sprache;

	private Rolle rolle;

	private Wettbewerb wettbewerb;

	public String getKuerzelLand() {

		return kuerzelLand;
	}

	public UploadAuswertungContext withKuerzelLand(final String kuerzelLand) {

		this.kuerzelLand = kuerzelLand;
		return this;
	}

	public Wettbewerb getWettbewerb() {

		return wettbewerb;
	}

	public UploadAuswertungContext withWettbewerb(final Wettbewerb wettbewerb) {

		this.wettbewerb = wettbewerb;
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

	public UploadAuswertungContext withRolle(final Rolle rolle) {

		this.rolle = rolle;
		return this;
	}

}
