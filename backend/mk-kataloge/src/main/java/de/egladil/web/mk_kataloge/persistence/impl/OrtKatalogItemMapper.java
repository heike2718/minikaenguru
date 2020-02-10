// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.persistence.impl;

import java.util.function.Function;

import de.egladil.web.mk_kataloge.domain.KatalogItem;
import de.egladil.web.mk_kataloge.domain.Katalogtyp;
import de.egladil.web.mk_kataloge.persistence.impl.entities.Ort;

/**
 * OrtKatalogItemMapper
 */
public class OrtKatalogItemMapper implements Function<Ort, KatalogItem> {

	@Override
	public KatalogItem apply(final Ort ort) {

		KatalogItem katalogItem = KatalogItem.createLazy(Katalogtyp.ORT, ort.getKuerzel(), ort.getName());
		return katalogItem;
	}

}
