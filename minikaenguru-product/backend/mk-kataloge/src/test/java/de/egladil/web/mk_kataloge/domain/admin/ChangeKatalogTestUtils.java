// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.admin;

import de.egladil.web.mk_kataloge.domain.apimodel.LandPayload;
import de.egladil.web.mk_kataloge.domain.apimodel.OrtPayload;
import de.egladil.web.mk_kataloge.domain.apimodel.SchulePayload;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Land;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Ort;
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

	static SchulePayload createSchulePayloadForTest() {

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

	static OrtPayload createOrtPayloadForTest() {

		String kuerzel = "TFFFVHVH";
		String name = "Brasilia";
		String kuerzelLand = "BR";
		String nameLand = "Brasilien";

		return OrtPayload.create(kuerzel, name, kuerzelLand, nameLand);

	}

	static Ort mapFromOrtPayload(final OrtPayload ortPayload) {

		Ort result = new Ort();
		result.setKuerzel(ortPayload.kuerzel());
		result.setName(ortPayload.name());
		result.setLandKuerzel(ortPayload.kuerzelLand());
		result.setLandName(ortPayload.nameLand());
		return result;

	}

	static LandPayload createLandPayloadForTest() {

		String kuerzel = "BR";
		String name = "Brasilien";

		return LandPayload.create(kuerzel, name);
	}

	static Land mapFromLandPayload(final LandPayload landPayload) {

		Land result = new Land();
		result.setKuerzel(landPayload.kuerzel());
		result.setName(landPayload.name());
		return result;

	}

}
