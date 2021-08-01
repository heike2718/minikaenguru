// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.tuple.Pair;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.error.ActionNotAuthorizedException;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;
import de.egladil.web.mk_gateway.domain.uploads.impl.UploadManagerImpl;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * UploadManager steuert den workflow eines Dateiuploads.
 */
public interface UploadManager {

	/**
	 * @param  benutzerUuid
	 * @param  teilnahmenummer
	 * @param  uploadType
	 * @param  wettbewerbID
	 *                         TODO
	 * @return                 Pair aus Wettbewerb (right) und Rolle (left) ,die der Benutzer hat, anderenfalls eine der genannten
	 *                         Exceptions, die vom
	 *                         globalen ExceptionHandler verwurstet werden. Sie ist daher nie null.
	 */
	Pair<Rolle, Wettbewerb> authorizeUpload(String benutzerUuid, String teilnahmenummer, UploadType uploadType, WettbewerbID wettbewerbID) throws AccessDeniedException, ActionNotAuthorizedException;

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
