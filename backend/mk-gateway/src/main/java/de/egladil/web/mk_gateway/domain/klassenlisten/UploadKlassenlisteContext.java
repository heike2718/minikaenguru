// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten;

import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;

/**
 * UploadKlassenlisteContext
 */
public class UploadKlassenlisteContext {

	private Sprache sprache;

	private boolean nachnameAlsZusatz;

	private String kuerzelLand;

	public Sprache getSprache() {

		return sprache;
	}

	public UploadKlassenlisteContext withSprache(final Sprache sprache) {

		this.sprache = sprache;
		return this;
	}

	public boolean isNachnameAlsZusatz() {

		return nachnameAlsZusatz;
	}

	public UploadKlassenlisteContext withNachnameAlsZusatz(final boolean nachnameAlsZusatz) {

		this.nachnameAlsZusatz = nachnameAlsZusatz;
		return this;
	}

	public String getKuerzelLand() {

		return kuerzelLand;
	}

	public UploadKlassenlisteContext withKuerzelLand(final String kuerzelLand) {

		this.kuerzelLand = kuerzelLand;
		return this;
	}

}
