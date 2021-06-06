// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads;

import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.error.ActionNotAuthorizedException;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;
import de.egladil.web.mk_gateway.infrastructure.upload.ScanResult;

/**
 * UploadManager steuert den workflow eines Dateiuploads.
 */
public interface UploadManager {

	/**
	 * @param  veranstalterUuid
	 * @param  teilnahmenummer
	 * @param  uploadType
	 * @return                  boolean true, anderenfalls eine der genannten Exceptions, die vom globalen ExceptionHandler
	 *                          verwurstet werden.
	 */
	boolean authorizeUpload(String veranstalterUuid, String teilnahmenummer, UploadType uploadType) throws AccessDeniedException, ActionNotAuthorizedException;

	/**
	 * Prüft die Daten des Uploads auf Viren und andere Securitydinge.
	 *
	 * @param  uploadPayload
	 * @return               ScanResult
	 */
	ScanResult scanUpload(UploadRequestPayload uploadPayload) throws UploadFormatException;

	/**
	 * Transformiert das ScanResult in eine CSV-Datei mit einer UUID als Filename. Diese UUId kann als PK für die Metadaten in
	 * UPLOADS verwendet werden.
	 *
	 * @param  scanResult
	 * @return            Response mit der gewünschten UUID des Eintrags in der Datenbank. Dies ist gleichzeitig der Name der
	 *                    CSV-Datei mit den Daten.
	 */
	PersistenterUpload transformAndPersistUpload(final UploadRequestPayload uploadPayload, ScanResult scanResult);

}
