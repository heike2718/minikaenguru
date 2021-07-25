// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads;

import javax.persistence.EntityManager;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.error.ActionNotAuthorizedException;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;
import de.egladil.web.mk_gateway.domain.uploads.impl.UploadManagerImpl;

/**
 * UploadManager steuert den workflow eines Dateiuploads.
 */
public interface UploadManager {

	/**
	 * @param  benutzerUuid
	 * @param  teilnahmenummer
	 * @param  uploadType
	 * @return                  boolean true, anderenfalls eine der genannten Exceptions, die vom globalen ExceptionHandler
	 *                          verwurstet werden.
	 */
	boolean authorizeUpload(String benutzerUuid, String teilnahmenummer, UploadType uploadType) throws AccessDeniedException, ActionNotAuthorizedException;

	/**
	 * Verarbeitet den Upload.
	 *
	 * @param  uploadPayload
	 * @return                       ResponsePayload
	 * @throws UploadFormatException
	 */
	ResponsePayload processUpload(UploadRequestPayload uploadPayload) throws UploadFormatException;

	/**
	 * Erzeugt eine initialisierte Instanz für Integrationstests.
	 *
	 * @param  em
	 *            EntityManager
	 * @return    UploadManager
	 */
	static UploadManager createForIntegrationTests(final EntityManager em) {

		return UploadManagerImpl.createForIntegrationTests(em);
	}

}
