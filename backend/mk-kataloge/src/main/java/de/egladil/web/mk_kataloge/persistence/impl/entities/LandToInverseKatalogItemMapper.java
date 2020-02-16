// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.persistence.impl.entities;

import java.util.function.Function;

import de.egladil.web.mk_kataloge.domain.InverseKatalogItem;
import de.egladil.web.mk_kataloge.domain.Katalogtyp;

/**
 * LandToInverseKatalogItemMapper
 */
public class LandToInverseKatalogItemMapper implements Function<LandInverse, InverseKatalogItem> {

	@Override
	public InverseKatalogItem apply(final LandInverse land) {

		InverseKatalogItem result = InverseKatalogItem.createWithTypKuerzelName(Katalogtyp.LAND, land.getKuerzel(), land.getName());
		result.setAnzahlKinder(land.getAnzahlOrte());

		return result;
	}

}
