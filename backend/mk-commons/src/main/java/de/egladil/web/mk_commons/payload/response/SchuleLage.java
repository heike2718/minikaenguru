// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.payload.response;

/**
 * SchuleLage
 */
public class SchuleLage {

	private String landKuerzel;

	private String landName;

	private String ortKuerzel;

	private String ortName;

	/**
	 *
	 */
	public SchuleLage() {

	}

	public String getLandKuerzel() {

		return landKuerzel;
	}

	public void setLandKuerzel(final String landKuerzel) {

		this.landKuerzel = landKuerzel;
	}

	public String getLandName() {

		return landName;
	}

	public void setLandName(final String landName) {

		this.landName = landName;
	}

	public String getOrtKuerzel() {

		return ortKuerzel;
	}

	public void setOrtKuerzel(final String ortKuerzel) {

		this.ortKuerzel = ortKuerzel;
	}

	public String getOrtName() {

		return ortName;
	}

	public void setOrtName(final String ortName) {

		this.ortName = ortName;
	}

}
