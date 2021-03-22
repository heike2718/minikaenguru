// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.admin;

import javax.enterprise.context.ApplicationScoped;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;

/**
 * VeranstalterNewsletterService
 */
@ApplicationScoped
public class VeranstalterNewsletterService extends AbstractVeranstalterAendernService {

	@Override
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
