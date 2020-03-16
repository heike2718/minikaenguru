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
 * SchuleToKatalogItemMapper
 */
public class SchuleToKatalogItemMapper implements Function<Schule, KatalogItem> {

	private final KatalogeRepository repository;

	/**
	 * @param repository
	 */
	public SchuleToKatalogItemMapper(final KatalogeRepository repository) {

		this.repository = repository;
	}

	@Override
	public KatalogItem apply(final Schule schule) {

		KatalogItem result = KatalogItem.createWithTypKuerzelName(Katalogtyp.SCHULE, schule.getKuerzel(),
			schule.getName(), 0);

		Optional<Ort> optOrt = repository.findOrtWithKuerzel(schule.getOrtKuerzel());

		Ort ortEntity = optOrt.get();
		KatalogItem ort = KatalogItem.createWithTypKuerzelName(Katalogtyp.ORT, schule.getOrtKuerzel(),
			schule.getOrtName(), ortEntity.getAnzahlSchulen());

		Optional<Land> optLand = repository.findLandWithKuerzel(schule.getLandKuerzel());
		Land landEntity = optLand.get();
		KatalogItem land = KatalogItem.createWithTypKuerzelName(Katalogtyp.LAND, schule.getLandKuerzel(), schule.getLandName(),
			landEntity.getAnzahlOrte());

		ort.setParent(land);
		result.setParent(ort);

		return result;
	}

}
