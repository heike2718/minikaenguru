// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.payload.response;

/**
 * SchulePayload
 */
public class SchulePayload {

	private String kuerzel;

	private String name;

	private SchuleLage lage;

	/**
	 *
	 */
	public SchulePayload() {

	}

	public String getKuerzel() {

		return kuerzel;
	}

	public void setKuerzel(final String kuerzel) {

		this.kuerzel = kuerzel;
	}

	public String getName() {

		return name;
	}

	public void setName(final String name) {

		this.name = name;
	}

	public SchuleLage getLage() {

		return lage;
	}

	public void setLage(final SchuleLage lage) {

		this.lage = lage;
	}

}
