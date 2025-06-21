// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.feedback;

import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagen;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;

/**
 * ActivateFeedbackDelegate ermittelt, ob das Feedback zum Wettbewerb aktiviert werden kann.
 */
public class ActivateFeedbackDelegate {

	/**
	 * Entscheidet anhand des Status des Wettbewerbs und der Zugangsberechtigung auf die Unterlagen darüber, ob der
	 * Feedback-Fragebogen angezeigt werden soll. Tatsächlich angezeigt werden sollte er aber nur dann, wenn es Lösungszettel zum
	 * aktuellen Wettbewerb wenigstens einer Teilnahme gibt.
	 *
	 * @param  statusWettbewerb
	 * @param  zugangUnterlagen
	 * @return
	 */
	public boolean canActivateFeedback(final WettbewerbStatus statusWettbewerb, final ZugangUnterlagen zugangUnterlagen) {

		if (zugangUnterlagen == ZugangUnterlagen.ENTZOGEN) {

			return false;
		}

		return statusWettbewerb != WettbewerbStatus.ERFASST;
	}

}
