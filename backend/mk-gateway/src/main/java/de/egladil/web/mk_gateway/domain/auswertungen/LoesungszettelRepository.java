// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen;

import java.util.List;

import de.egladil.web.mk_gateway.domain.apimodel.teilnahmen.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterLoesungszettel;

/**
 * LoesungszettelRepository
 */
public interface LoesungszettelRepository {

	/**
	 * Läd alle Lösungszettel mit dem gegebenen TeilnahmeIdentifier.
	 *
	 * @param  teilnahmeIdentifier
	 *                             TeilnahmeIdentifier
	 * @return                     List
	 */
	List<Loesungszettel> loadAll(TeilnahmeIdentifier teilnahmeIdentifier);

	/**
	 * Ermittelt die Anzahl der Lösungszettel mit dem gegebenen TeilnahmeIdentifier.
	 *
	 * @param  teilnahmeIdentifier
	 *                             TeilnahmeIdentifier
	 * @return                     int
	 */
	int anzahlLoesungszettel(TeilnahmeIdentifier teilnahmeIdentifier);

	/**
	 * Wird später abgelöst durch eine Methode, die einen Loesungszettel, also das domain-Objekt entgegennimmt
	 *
	 * @param  loesungszettel
	 * @return
	 */
	@Deprecated(forRemoval = true)
	boolean addLosungszettel(PersistenterLoesungszettel loesungszettel);

}
