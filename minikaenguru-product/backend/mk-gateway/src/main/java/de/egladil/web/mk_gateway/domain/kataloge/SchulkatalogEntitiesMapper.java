//=====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_gateway.domain.kataloge;

import de.egladil.web.mk_gateway.domain.kataloge.dto.KatalogItem;
import de.egladil.web.mk_gateway.domain.kataloge.dto.Katalogtyp;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.entities.Land;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.entities.Ort;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.entities.Schule;

/**
 *
 */
public class SchulkatalogEntitiesMapper {

	/**
	 * Mapped Schule auf SchuleAPIModel. Davon nur den Teil, der aus dem Katalog stammt. Der Rest muss hinzugemerged
	 * werden, wenn erforderlich.
	 *
	 * @param schule Schule
	 * @return SchuleAPIModel
	 */
	public SchuleAPIModel mapSchuleToSchuleAPIModel(Schule schule) {

		SchuleAPIModel result = new SchuleAPIModel().withKuerzel(schule.getKuerzel()).withKuerzelLand(schule.getLandKuerzel())
			.withLand(schule.getLandName()).withName(schule.getName()).withOrt(schule.getOrtName());

		return result;
	}

	/**
	 * Mapped die Schule zu einem KatalogItem mit seinen 2 Eltern.
	 *
	 * @param schule Schule
	 * @param ort Ort
	 * @param land Land
	 * @return
	 */
	public KatalogItem mapSchuleToKatalogItem(final Schule schule, final Ort ort, final Land land) {

		KatalogItem schuleItem = KatalogItem.createWithTypKuerzelName(Katalogtyp.SCHULE, schule.getKuerzel(), schule.getName(), 0);

		KatalogItem ortItem = KatalogItem.createWithTypKuerzelName(Katalogtyp.ORT, schule.getOrtKuerzel(), schule.getOrtName(),
			ort.getAnzahlSchulen());

		KatalogItem landItem = KatalogItem.createWithTypKuerzelName(Katalogtyp.LAND, schule.getLandKuerzel(), schule.getLandName(),
			land.getAnzahlOrte());

		ortItem.setParent(landItem);
		schuleItem.setParent(ortItem);

		schuleItem.setPfad(schule.getLandName() + " -> " + schule.getOrtName() + " -> " + schule.getName());

		return schuleItem;
	}

	/**
	 * Mapped den Ort zu einem KatalogItem mit dem Elternknoten.
	 *
	 *@param ort Ort
	 * @param land Land
	 * @return KatalogItem
	 */
	public KatalogItem mapOrtToKatalogItem(final Ort ort, final Land land) {

		KatalogItem ortItem = KatalogItem.createWithTypKuerzelName(Katalogtyp.ORT, ort.getKuerzel(), ort.getName(),
			ort.getAnzahlSchulen());

		KatalogItem landItem = KatalogItem.createWithTypKuerzelName(Katalogtyp.LAND, ort.getLandKuerzel(), ort.getLandName(),
			land.getAnzahlOrte());

		ortItem.setParent(landItem);

		ortItem.setPfad(ort.getLandName() + " -> " + ort.getName());

		return ortItem;
	}

}
