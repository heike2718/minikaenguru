// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;

/**
 * KlassenlisteImportService
 */
public interface KlassenlisteImportService {

	/**
	 * Importiert die Kinder aus einer CSV-Datei.
	 *
	 * @param  uploadKlassenlisteContext
	 *                                   UploadKlassenlisteContext - Daten die zum Anlegen von Kindern erforderlich sind.
	 * @param  uploadMetadata
	 *                                   PersistenterUpload - weitere Daten, die zum Anlegen der Kinder und auch zum Finden der
	 *                                   CSV-Datei erforderlich sind.
	 * @return
	 */
	ResponsePayload importiereKinder(final UploadKlassenlisteContext uploadKlassenlisteContext, final PersistenterUpload uploadMetadata);

}
