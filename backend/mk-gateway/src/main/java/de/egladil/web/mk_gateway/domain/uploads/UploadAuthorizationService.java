// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.ActionNotAuthorizedException;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

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
	 * @param  wettbewerbID:
	 *                                      WettbewerbID
	 * @return                              Wettbewerb mit der ID. Falls es keinen gibt, wird ohnehin eine Exception geworfen. Daher
	 *                                      ist er nicht null.
	 * @throws ActionNotAuthorizedException
	 *                                      diese wird vom ExceptionMapper behandelt.
	 */
	Wettbewerb authorizeUploadAndReturnWettbewerb(Identifier benutzerID, String schulkuerzel, UploadType uploadType, Rolle rolle, WettbewerbID wettbewerbID) throws ActionNotAuthorizedException;

}
