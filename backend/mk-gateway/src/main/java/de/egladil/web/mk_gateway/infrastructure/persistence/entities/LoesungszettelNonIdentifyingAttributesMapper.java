// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRohdaten;

/**
 * LoesungszettelNonIdentifyingAttributesMapper
 */
public class LoesungszettelNonIdentifyingAttributesMapper {

	public void copyAllAttributesButIdentifier(final PersistenterLoesungszettel target, final Loesungszettel loesungszettel) {

		LoesungszettelRohdaten rohdaten = loesungszettel.rohdaten();

		target.setAntwortcode(rohdaten.antwortcode());
		target.setAuswertungsquelle(loesungszettel.auswertungsquelle());
		target.setKaengurusprung(loesungszettel.laengeKaengurusprung());

		if (loesungszettel.kindID() != null) {

			target.setKindID(loesungszettel.kindID().identifier());
		}

		target.setKlassenstufe(loesungszettel.klassenstufe());
		target.setLandkuerzel(loesungszettel.landkuerzel());
		target.setNutzereingabe(rohdaten.nutzereingabe());
		target.setPunkte(loesungszettel.punkte());
		target.setSprache(loesungszettel.sprache());
		target.setTeilnahmeart(loesungszettel.teilnahmeIdentifier().teilnahmeart());
		target.setTeilnahmenummer(loesungszettel.teilnahmeIdentifier().teilnahmenummer());
		target.setTypo(rohdaten.hatTypo());
		target.setWertungscode(rohdaten.wertungscode());
		target.setWettbewerbUuid(loesungszettel.teilnahmeIdentifier().wettbewerbID());
	}

}
