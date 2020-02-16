// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.persistence.impl.entities;

import java.util.function.Function;

import de.egladil.web.mk_kataloge.domain.InverseKatalogItem;
import de.egladil.web.mk_kataloge.domain.Katalogtyp;

/**
 * OrtInverseToInverseKatalogItemMapper
 */
public class OrtInverseToInverseKatalogItemMapper implements Function<OrtInverse, InverseKatalogItem> {

	@Override
	public InverseKatalogItem apply(final OrtInverse ort) {

		InverseKatalogItem result = InverseKatalogItem.createWithTypKuerzelName(Katalogtyp.ORT, ort.getKuerzel(),
			ort.getName());

		result.setParent(InverseKatalogItem.createWithTypKuerzelName(Katalogtyp.LAND, ort.getLandKuerzel(), ort.getLandName()));

		return result;
	}

}
