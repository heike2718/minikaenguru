// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.admin;

import de.egladil.web.mk_kataloge.domain.apimodel.SchulePayload;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * ChangeKatalogTestUtils
 */
public final class ChangeKatalogTestUtils {

	/**
	 *
	 */
	private ChangeKatalogTestUtils() {

	}

	static SchulePayload createPayloadForTest() {

		String kuerzel = "GHKGKGK";
		String name = "Baumschule";
		String kuerzelOrt = "TFFFVHVH";
		String nameOrt = "Brasilia";
		String kuerzelLand = "BR";
		String nameLand = "Brasilien";

		SchulePayload schulePayload = SchulePayload.create(kuerzel, name, kuerzelOrt, nameOrt, kuerzelLand, nameLand);

		return schulePayload;

	}

	static Schule mapFromSchulePayload(final SchulePayload schulePayload) {

		Schule result = new Schule();
		result.setImportiertesKuerzel(schulePayload.kuerzel());
		result.setLandKuerzel(schulePayload.kuerzelLand());
		result.setLandName(schulePayload.nameLand());
		result.setName(schulePayload.name());
		result.setOrtKuerzel(schulePayload.kuerzelOrt());
		result.setOrtName(schulePayload.nameOrt());

		return result;
	}

}
