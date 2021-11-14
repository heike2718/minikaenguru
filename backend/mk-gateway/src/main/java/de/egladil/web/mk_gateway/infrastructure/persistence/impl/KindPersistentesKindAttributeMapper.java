// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistentesKind;

/**
 * KindPersistentesKindAttributeMapper
 */
public class KindPersistentesKindAttributeMapper {

	/**
	 * Kopiert alle Attribute außer der UUID
	 *
	 * @param source
	 * @param target
	 */
	public void copyAttributesFromKindWithoutUuid(final Kind source, final PersistentesKind target) {

		target.setKlassenstufe(source.klassenstufe());

		if (source.klasseID() != null) {

			target.setKlasseUUID(source.klasseID().identifier());
		} else {

			target.setKlasseUUID(null);
		}

		if (source.loesungszettelID() != null) {

			target.setLoesungszettelUUID(source.loesungszettelID().identifier());
		} else {

			target.setLoesungszettelUUID(null);
		}

		target.setNachname(source.nachname());
		target.setSprache(source.sprache());
		target.setTeilnahmeart(source.teilnahmeIdentifier().teilnahmeart());
		target.setTeilnahmenummer(source.teilnahmeIdentifier().teilnahmenummer());
		target.setVorname(source.vorname());
		target.setZusatz(source.zusatz());
		target.setLandkuerzel(source.landkuerzel());
		target.setDublettePruefen(source.isDublettePruefen());
		target.setKlassenstufePruefen(source.isKlassenstufePruefen());
		target.setImportiert(source.isImportiert());
	}

}
