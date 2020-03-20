// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.persistence.impl.entities;

import java.util.function.Function;

import de.egladil.web.mk_kataloge.domain.KatalogItem;
import de.egladil.web.mk_kataloge.domain.Katalogtyp;

/**
 * LandToKatalogItemMapper
 */
public class LandToKatalogItemMapper implements Function<Land, KatalogItem> {

	@Override
	public KatalogItem apply(final Land land) {

		KatalogItem result = KatalogItem.createWithTypKuerzelName(Katalogtyp.LAND, land.getKuerzel(), land.getName(),
			land.getAnzahlOrte());

		result.setLage(land.getName());

		return result;
	}

}
