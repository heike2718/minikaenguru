// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen;

import java.util.List;

import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterLoesungszettel;

/**
 * LoesungszettelRepository
 */
public interface LoesungszettelRepository {

	List<PersistenterLoesungszettel> loadAll();

	/**
	 * Wird später abgelöst durch eine Methode, die einen Loesungszettel, also das domain-Objekt entgegennimmt
	 *
	 * @param  loesungszettel
	 * @return
	 */
	@Deprecated(forRemoval = true)
	boolean addLosungszettel(PersistenterLoesungszettel loesungszettel);

}
