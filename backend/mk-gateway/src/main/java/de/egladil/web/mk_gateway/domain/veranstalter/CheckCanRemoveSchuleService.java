// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.SchuleDetailsService;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleDetails;

/**
 * CheckCanRemoveSchuleService
 */
@ApplicationScoped
public class CheckCanRemoveSchuleService {

	@Inject
	AuthorizationService veranstalterAuthService;

	@Inject
	SchuleDetailsService schuleDetailsService;

	/**
	 * Prüft, ob die Voraussetzungen zum Abmelden des Lehrers von der Schule erfüllt sind.
	 *
	 * @param  lehrerID
	 *                  Identifier
	 * @param  schuleID
	 *                  Identifier
	 * @return          boolean true, wenn es geht, sonst false
	 */
	public boolean kannLehrerVonSchuleAbmelden(final Identifier lehrerID, final Identifier schuleID) {

		veranstalterAuthService.checkPermissionForTeilnahmenummerAndReturnRolle(lehrerID, schuleID,
			"[removeSchule - " + schuleID.identifier() + "]");

		SchuleDetails details = schuleDetailsService.ermittleSchuldetails(schuleID, lehrerID);

		if (details.angemeldetDurch() == null) {

			return true;
		}

		if (!StringUtils.isBlank(details.kollegen())) {

			return true;
		}

		return false;
	}

}
