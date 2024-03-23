// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.admin;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;

/**
 * VeranstalterNewsletterService
 */
@ApplicationScoped
public class VeranstalterNewsletterService extends AbstractVeranstalterAendernService {

	@Override
	@Transactional
	protected ResponsePayload specialAendereVeranstalter(final Veranstalter zuAendernderVeranstalter, final Object data) {

		if (!zuAendernderVeranstalter.isNewsletterEmpfaenger()) {

			String msg = "Veranstalter hatte den Newsletterempfang bereits deaktiviert.";
			return ResponsePayload.messageOnly(MessagePayload.info(msg));

		}

		zuAendernderVeranstalter.toggleAboNewsletter();

		veranstalterRepository.changeVeranstalter(zuAendernderVeranstalter);

		String msg = "Newsletterempfang erfolgreich deaktiviert";
		return ResponsePayload.messageOnly(MessagePayload.info(msg));
	}

}
