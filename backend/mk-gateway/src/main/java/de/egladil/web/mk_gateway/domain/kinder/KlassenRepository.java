// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.Identifier;

/**
 * KlassenRepository
 */
public interface KlassenRepository {

	/**
	 * @param  klasseID
	 * @return
	 */
	Optional<Klasse> ofIdentifier(Identifier klasseID);

	/**
	 * Sucht alle Klassen einer Schule.
	 *
	 * @param  schuleID
	 * @return          List
	 */
	List<Klasse> findKlassenWithSchule(Identifier schuleID);

	/**
	 * Fügt eine neue PersistenteKlasse hinzu.
	 *
	 * @param  klasse
	 *                Klasse
	 * @return        Klasse die neue
	 */
	Klasse addKlasse(Klasse klasse);

	/**
	 * Ändert eine vorhandene Klasse.
	 *
	 * @param  klasse
	 * @return        Klasse
	 */
	Klasse changeKlasse(Klasse klasse);

	/**
	 * Vorhandene Klasse wird gelöscht. Dies ist Teil eines UnitOfWork, in dem alle Kinder der Klasse und deren Lösungszettel
	 * ebenfalls gelöscht werden müssen. De Transaktionsklammer beginnt also weiter außen.
	 *
	 * @param  klasse
	 *                Klasse
	 * @return        boolean true, falls gelöscht, false sonst.
	 */
	boolean removeKlasse(Klasse klasse);

}
