// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.persistence.impl.entities;

import java.util.Optional;
import java.util.function.Function;

import de.egladil.web.mk_kataloge.domain.KatalogItem;
import de.egladil.web.mk_kataloge.domain.Katalogtyp;
import de.egladil.web.mk_kataloge.persistence.KatalogeRepository;

/**
 * OrtToKatalogItemMapper
 */
public class OrtToKatalogItemMapper implements Function<Ort, KatalogItem> {

	private final KatalogeRepository repository;

	/**
	 * @param repository
	 */
	public OrtToKatalogItemMapper(final KatalogeRepository repository) {

		this.repository = repository;
	}

	@Override
	public KatalogItem apply(final Ort ort) {

		KatalogItem result = KatalogItem.createWithTypKuerzelName(Katalogtyp.ORT, ort.getKuerzel(),
			ort.getName(), ort.getAnzahlSchulen());

		Optional<Land> optLand = repository.findLandWithKuerzel(ort.getLandKuerzel());
		Land landEntity = optLand.get();
		KatalogItem land = KatalogItem.createWithTypKuerzelName(Katalogtyp.LAND, ort.getLandKuerzel(), ort.getLandName(),
			landEntity.getAnzahlOrte());

		result.setPfad(ort.getLandName() + " -> " + ort.getName());

		result.setParent(land);

		return result;
	}

}
