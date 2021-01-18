// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.semantik.Repository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * TeilnahmenRepository
 */
@Repository
public interface TeilnahmenRepository {

	/**
	 * @param  teilnahmeIdentifier
	 * @return
	 */
	Optional<Teilnahme> ofTeilnahmeIdentifier(TeilnahmeIdentifier teilnahmeIdentifier);

	/**
	 * Gibt alle Teilnahmen mit dieser Teilnahmenummer und Art zurück.
	 *
	 * @param  teilnahmenummer
	 *                         String
	 * @param  art
	 *                         Teilnahmeart
	 * @return                 List
	 */
	List<Teilnahme> ofTeilnahmenummerArt(String teilnahmenummer, Teilnahmeart art);

	/**
	 * Sucht alle Teilnahmen mit der gegebenen Teilnahmenummer.
	 *
	 * @param  teilnahmenummer
	 *                         Teilnahmenummer
	 * @return                 List
	 */
	List<Teilnahme> ofTeilnahmenummer(String teilnahmenummer);

	/**
	 * Persistiert eine neue Teilnahme.
	 *
	 * @param  teilnahme
	 * @return           boolean
	 */
	boolean addTeilnahme(Teilnahme teilnahme);

	/**
	 * Ändert eine vorhandene Teilnahme.
	 *
	 * @param  teilnahme
	 *                               Schulteilnahme, muss eine Schulteilnahme sein es muss bereits eine gespeicherte Teilnahme
	 *                               geben.
	 * @param  uuidAenderer
	 *                               String die UUID desjenigen, der die Schulteilnahme geändert hat.
	 * @throws IllegalStateException
	 *                               falls es noch keine Teilnahme gibt.
	 */
	void changeTeilnahme(Schulteilnahme teilnahme, String uuidAenderer) throws IllegalStateException;

	/**
	 * Läd alle Teilnahmen eines gegebenen Wettbewerbsjahrs.
	 *
	 * @return List
	 */
	List<Teilnahme> loadAllForWettbewerb(WettbewerbID wettbewerbID);

}
