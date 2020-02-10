// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.persistence.impl;

import java.util.function.Function;

import de.egladil.web.mk_kataloge.domain.KatalogItem;
import de.egladil.web.mk_kataloge.domain.Katalogtyp;
import de.egladil.web.mk_kataloge.persistence.impl.entities.Land;

/**
 * LandKatalogItemMapper
 */
public class LandKatalogItemMapper implements Function<Land, KatalogItem> {

	/**
	 *
	 */
	public LandKatalogItemMapper() {

	}

	@Override
	public KatalogItem apply(final Land land) {

		KatalogItem katalogItem = KatalogItem.createLazy(Katalogtyp.LAND, land.getKuerzel(), land.getName());
		return katalogItem;
	}

}
