// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.persistence.impl.entities;

import java.util.function.Function;

import de.egladil.web.mk_kataloge.domain.InverseKatalogItem;
import de.egladil.web.mk_kataloge.domain.Katalogtyp;

/**
 * SchuleInverseToInverseKatalogItemMapper
 */
public class SchuleInverseToInverseKatalogItemMapper implements Function<SchuleInverse, InverseKatalogItem> {

	@Override
	public InverseKatalogItem apply(final SchuleInverse schule) {

		InverseKatalogItem result = InverseKatalogItem.createWithTypKuerzelName(Katalogtyp.SCHULE, schule.getKuerzel(),
			schule.getName());

		InverseKatalogItem ort = InverseKatalogItem.createWithTypKuerzelName(Katalogtyp.ORT, schule.getOrtKuerzel(),
			schule.getOrtName());

		ort.setParent(InverseKatalogItem.createWithTypKuerzelName(Katalogtyp.LAND, schule.getLandKuerzel(), schule.getLandName()));

		result.setParent(ort);

		return result;
	}

}
