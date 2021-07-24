// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.ActionNotAuthorizedException;

/**
 * UploadAuthorizationService
 */
public interface UploadAuthorizationService {

	/**
	 * Autorisiert den gewünschten Upload.
	 *
	 * @param  veranstalterID
	 * @param  schulkuerzel
	 * @param  uploadType
	 * @return                              boolean zum Mocken
	 * @throws ActionNotAuthorizedException
	 *                                      diese wird vom ExceptionMapper behandelt.
	 */
	boolean authorizeUpload(Identifier veranstalterID, String schulkuerzel, UploadType uploadType) throws ActionNotAuthorizedException;

}
