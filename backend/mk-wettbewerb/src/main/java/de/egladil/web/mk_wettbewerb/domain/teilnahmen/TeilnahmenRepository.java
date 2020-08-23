// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_wettbewerb.domain.semantik.Repository;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities.TemporaerePersistentePrivatteilnahme;

/**
 * TeilnahmenRepository
 */
@Repository
public interface TeilnahmenRepository {

	/**
	 * Gibt die Teilnahme mit den 3 genannten Merkmalen zurück, falls sie existiert.
	 *
	 * @param  teilnahmenummer
	 * @param  art
	 * @param  wettbewerbId
	 * @return                 Optional
	 */
	Optional<Teilnahme> ofTeilnahmenummerArtWettbewerb(String teilnahmenummer, Teilnahmeart art, WettbewerbID wettbewerbId);

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
	 * @return
	 */
	@Deprecated(forRemoval = true)
	List<TemporaerePersistentePrivatteilnahme> loadAllTemporaryPrivatteilnahmen();

	@Deprecated(forRemoval = true)
	TemporaerePersistentePrivatteilnahme save(TemporaerePersistentePrivatteilnahme teilnahme);

	/**
	 * @return
	 */
	List<Teilnahme> loadAllPrivatteilnahmen();

}
