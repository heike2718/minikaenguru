// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;

/**
 * UploadAuswertungContext
 */
public class UploadAuswertungContext {

	private String teilnahmenummer;

	private Teilnahmeart teilnahmeart;

	private String kuerzelLand;

	private String wettbewerbsjahr;

	public String getKuerzelLand() {

		return kuerzelLand;
	}

	public void setKuerzelLand(final String kuerzelLand) {

		this.kuerzelLand = kuerzelLand;
	}

	public String getWettbewerbsjahr() {

		return wettbewerbsjahr;
	}

	public void setWettbewerbsjahr(final String wettbewerbsjahr) {

		this.wettbewerbsjahr = wettbewerbsjahr;
	}

}
