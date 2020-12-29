// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden;

/**
 * Urkundenart
 */
public enum Urkundenart {

	KAENGURUSPRUNG("minikaenguru_urkunde_kaengurusprung"),
	TEILNAHME("minikaenguru_teilnahmeurkunde");

	private final String praefixDateiname;

	private Urkundenart(final String praefixDateiname) {

		this.praefixDateiname = praefixDateiname;
	}

	public String praefixDateiname() {

		return praefixDateiname;
	}

}
