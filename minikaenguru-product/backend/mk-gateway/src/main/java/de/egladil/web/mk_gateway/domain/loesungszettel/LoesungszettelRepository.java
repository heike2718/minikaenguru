// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.EntityConcurrentlyModifiedException;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Auspraegung;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterLoesungszettel;

/**
 * LoesungszettelRepository
 */
public interface LoesungszettelRepository {

	/**
	 * @return long
	 */
	long anzahlForWettbewerb(WettbewerbID wettbewerbID);

	/**
	 * Läd eine Page.
	 *
	 * @param  limit
	 *                int die Anzahl, die gelesen werden soll
	 * @param  offset
	 *                int der Index, an dem das Lesen beginnen soll.
	 * @return        List
	 */
	List<Loesungszettel> loadLoadPageForWettbewerb(WettbewerbID wettbewerbID, int limit, int offset);

	/**
	 * Läd alle Lösungszettel zum gegebenen Wettbewerb.
	 *
	 * @param  wettbewerbID
	 * @return
	 */
	List<Loesungszettel> loadAllForWettbewerb(WettbewerbID wettbewerbID);

	/**
	 * Läd die Aufschlüsselung der Lösungszettel nach Auswertungsquelle für das gegebene Wettbewerbsjahr.
	 *
	 * @param  wettbewerbID
	 *                      WettbewerbID
	 * @return              List
	 */
	List<Pair<Auswertungsquelle, Integer>> getAuswertungsquelleMitAnzahl(WettbewerbID wettbewerbID);

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
	 * Läd alle Lösungszettel zum gegebenen Wettbewerb mit der gegebenen Teilnahmenummer.
	 *
	 * @param  teilnahmenummer
	 *                         String
	 * @param  wettbewerbID
	 *                         WettbewerbID
	 * @return
	 */
	List<Loesungszettel> loadAllWithTeilnahmenummerForWettbewerb(String teilnahmenummer, WettbewerbID wettbewerbID);

	/**
	 * Ermittelt die Anzahl der Lösungszettel mit dem gegebenen TeilnahmeIdentifier.
	 *
	 * @param  teilnahmeIdentifier
	 *                             TeilnahmeIdentifier
	 * @return                     int
	 */
	int anzahlLoesungszettel(TeilnahmeIdentifier teilnahmeIdentifier);

	/**
	 * Ermitelt für alle Auswertunsquellen die Anzahl der Lösungszettel.
	 *
	 * @param  teilnahmeIdentifier
	 *                             TeilnahmeIdentifier
	 * @return                     List
	 */
	List<Pair<Auswertungsquelle, Integer>> getAuswertungsquellenMitAnzahl(TeilnahmeIdentifier teilnahmeIdentifier);

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
	 * Sucht den Lösungszettel zu einem gegebenen Kind, sofern er existiert.
	 *
	 * @param  kindID
	 *                Identifier darf nicht null sein
	 * @return        Optional
	 */
	Optional<Loesungszettel> findLoesungszettelWithKindID(Identifier kindID);

	/**
	 * Fügt einen neuen Löungszettel hinzu.
	 *
	 * @param  loesungszettel
	 *                        Lösungszettel
	 * @return                Loesungszettel
	 */
	Loesungszettel addLoesungszettel(Loesungszettel loesungszettel) throws EntityConcurrentlyModifiedException;

	/**
	 * Ändert einen vorhandenen Lösungszettel in der DB.
	 *
	 * @param  loesungszettel
	 * @return                Loesungszettel - wenn empty, dann konkurrierend gelöscht.
	 */
	Loesungszettel updateLoesungszettel(Loesungszettel loesungszettel) throws EntityConcurrentlyModifiedException;

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
	Optional<PersistenterLoesungszettel> removeLoesungszettel(Identifier identifier);

	/**
	 * Selektiert die Lösungszettel des gegebenen Wettbewerbs und gruppiert sie nach der gewünschten Spalte.
	 *
	 * @param  wettbewerbID
	 *                      WettbewerbID
	 * @param  columnName
	 *                      String name des group by- Kriteriums.
	 * @return
	 */
	List<Auspraegung> countAuspraegungenForWettbewerbByColumnName(WettbewerbID wettbewerbID, String columnName);

	/**
	 * Selektiert die Lösungszettel des gegebenen Wettbewerbs und gruppiert sie nach der gewünschten Spalte.
	 *
	 * @param  teilnahmeIdentifier
	 *                             TeilnahmeIdentifier
	 * @param  columnName
	 *                             String name des group by- Kriteriums.
	 * @return
	 */
	List<Auspraegung> countAuspraegungenForTeilnahmeByColumnName(TeilnahmeIdentifier teilnahmeIdentifier, String columnName);
}
