// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb;

import java.util.List;
import java.util.Optional;

/**
 * WettbewerbRepository
 */
public interface WettbewerbRepository {

	/**
	 * Sucht den Wettbewerb mit der gegebenen WettbewerbID.
	 *
	 * @param  wettbewerbID
	 * @return              Optional eines Wettbewerbs
	 */
	Optional<Wettbewerb> wettbewerbMitID(WettbewerbID wettbewerbID);

	/**
	 * Erzeugt einen neuen Eintrag im Speicher.
	 *
	 * @param wettbewerb
	 *                   Wettbewerb
	 */
	void addWettbewerb(Wettbewerb wettbewerb);

	/**
	 * Ändert die vorhandene Persistierung eines Wettbewerbs.
	 *
	 * @param wettbewerb
	 *                   Wettbewerb
	 */
	void changeWettbewerb(Wettbewerb wettbewerb);

	/**
	 * Nur der Status wird aktualisiert.
	 *
	 * @param  wettbewerbId
	 * @param  neuerStatus
	 * @return              boolean (nicht void wegen Mockito)
	 */
	boolean changeWettbewerbStatus(WettbewerbID wettbewerbId, WettbewerbStatus neuerStatus);

	/**
	 * Läd alle Wettbewerbe.
	 *
	 * @return List
	 */
	List<Wettbewerb> loadWettbewerbe();

}
