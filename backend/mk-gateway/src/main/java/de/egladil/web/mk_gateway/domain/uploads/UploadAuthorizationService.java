// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.ActionNotAuthorizedException;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * UploadAuthorizationService
 */
public interface UploadAuthorizationService {

	/**
	 * Autorisiert den gewünschten Upload.
	 *
	 * @param  benutzerID
	 * @param  schulkuerzel
	 * @param  uploadType
	 * @param  rolle
	 *                                      Rolle
	 * @return                              boolean zum Mocken
	 * @throws ActionNotAuthorizedException
	 *                                      diese wird vom ExceptionMapper behandelt.
	 */
	boolean authorizeUpload(Identifier benutzerID, String schulkuerzel, UploadType uploadType, Rolle rolle) throws ActionNotAuthorizedException;

}
