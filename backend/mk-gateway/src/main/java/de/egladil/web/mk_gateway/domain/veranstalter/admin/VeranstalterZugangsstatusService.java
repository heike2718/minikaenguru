// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.admin;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import org.jboss.weld.exceptions.IllegalArgumentException;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagen;

/**
 * VeranstalterZugangsstatusService
 */
@ApplicationScoped
public class VeranstalterZugangsstatusService extends AbstractVeranstalterAendernService {

	@Override
	@Transactional
	protected ResponsePayload specialAendereVeranstalter(final Veranstalter zuAendernderVeranstalter, final Object data) {

		ZugangUnterlagen neuerZugangsstatus = (ZugangUnterlagen) data;

		if (neuerZugangsstatus == zuAendernderVeranstalter.zugangUnterlagen()) {

			String msg = "Veranstalter hatte bereits den gewünschten Zugangsstatus Unterlagen.";
			ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info(msg), neuerZugangsstatus);
			return responsePayload;
		}

		switch (neuerZugangsstatus) {

		case DEFAULT:
			zuAendernderVeranstalter.setzeZugangUnterlagenZurueck();
			break;

		case ENTZOGEN:
			zuAendernderVeranstalter.verwehreZugangUnterlagen();
			break;

		case ERTEILT:
			zuAendernderVeranstalter.erlaubeZugangUnterlagen();
			break;

		default:
			throw new IllegalArgumentException("unbekannter Zugangsstatus " + neuerZugangsstatus);
		}

		veranstalterRepository.changeVeranstalter(zuAendernderVeranstalter);

		String msg = "Zugangsstatus Unterlagen erfolgreich geändert.";
		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info(msg), neuerZugangsstatus);
		return responsePayload;
	}
}
