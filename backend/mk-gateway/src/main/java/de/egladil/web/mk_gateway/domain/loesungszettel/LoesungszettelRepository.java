// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterLoesungszettel;

/**
 * LoesungszettelRepository
 */
public interface LoesungszettelRepository {

	/**
	 * Läd alle Lösungszettel zum gegebenen Wettbewerb.
	 *
	 * @param  wettbewerbID
	 * @return
	 */
	List<Loesungszettel> loadAllForWettbewerb(WettbewerbID wettbewerbID);

	/**
	 * Läd alle Lösungszettel zum gegebenen Wettbewerb mit der Klassenstufe.
	 *
	 * @param  wettbewerbID
	 *                      WettbewerbID
	 * @param  klassenstufe
	 *                      Klassenstufe
	 * @return
	 */
	List<Loesungszettel> loadAllForWettbewerbAndKlassenstufe(WettbewerbID wettbewerbID, Klassenstufe klassenstufe);

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
	 * Sucht den Lösungszettel anhand des Identifiers.
	 *
	 * @param  identifier
	 * @return            Optional
	 */
	Optional<PersistenterLoesungszettel> findPersistentenLoesungszettel(Identifier identifier);

	/**
	 * Sucht den Lösungszettel anhand des Identifiers.
	 *
	 * @param  identifier
	 *                    Identifier oder null
	 * @return            Optional
	 */
	Optional<Loesungszettel> ofID(Identifier identifier);

	/**
	 * Fügt einen neuen Löungszettel hinzu.
	 *
	 * @param  loesungszettel
	 *                        Lösungszettel
	 * @return                Identifier
	 */
	Identifier addLoesungszettel(Loesungszettel loesungszettel);

	/**
	 * Ändert einen vorhandenen Lösungszettel in der DB.
	 *
	 * @param  loesungszettel
	 * @return                boolean
	 */
	boolean updateLoesungszettel(Loesungszettel loesungszettel);

	/**
	 * @param  persistenterLoesungszettel
	 *                                    PersistenterLoesungszettel
	 * @return                            PersistenterLoesungszettel
	 */
	PersistenterLoesungszettel updateLoesungszettelInTransaction(PersistenterLoesungszettel persistenterLoesungszettel);

	/**
	 * @param  identifier
	 * @return            Optional den gelöschten Lösungszettel oder empty.
	 */
	Optional<PersistenterLoesungszettel> removeLoesungszettel(Identifier identifier, String veranstalterUuid);
}
