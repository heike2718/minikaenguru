// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.auswertungen;

import java.util.List;

import de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities.PersistenterImportierterLoesungszettel;
import de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities.PersistenterLoesungszettel;

/**
 * LoesungszettelRepository
 */
public interface LoesungszettelRepository {

	/**
	 * @return
	 */
	@Deprecated(forRemoval = true)
	List<PersistenterImportierterLoesungszettel> loadAllImportiert();

	List<PersistenterLoesungszettel> loadAll();

	/**
	 * Wird später abgelöst durch eine Methode, die einen Loesungszettel, also das domain-Objekt entgegennimmt
	 *
	 * @param  loesungszettel
	 * @return
	 */
	@Deprecated(forRemoval = true)
	boolean addLosungszettel(PersistenterLoesungszettel loesungszettel);

	@Deprecated(forRemoval = true)
	PersistenterImportierterLoesungszettel updateLoesungszettel(PersistenterImportierterLoesungszettel zettel);

}
