// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.admin;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.jboss.weld.exceptions.IllegalArgumentException;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagen;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VeranstalterSuchanfrage;

/**
 * VeranstalterZugangsstatusService
 */
@ApplicationScoped
public class VeranstalterZugangsstatusService {

	@Inject
	VeranstalterRepository veranstalterRepository;

	/**
	 * Ändert beim Veranstalter mit dem veranstalterUuidPrefix den Zugangsstatus für die Unterlagen.
	 *
	 * @param  veranstalterUuidPrefix
	 *                                String erste 8 Stellen der UUID
	 * @param  neuerZugangsstatus
	 *                                ZugangUnterlagen der neue Zugangsstatus
	 * @return                        ResponsePayload
	 */
	@Transactional
	public ResponsePayload zugangsstatusAendern(final String veranstalterUuidPrefix, final ZugangUnterlagen neuerZugangsstatus) {

		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage(VeranstalterSuchkriterium.UUID, veranstalterUuidPrefix);
		List<Veranstalter> veranstalter = veranstalterRepository.findVeranstalter(suchanfrage);

		if (veranstalter.isEmpty()) {

			String msg = "Veranstalter mit UUID like '" + veranstalterUuidPrefix + "%' existiert nicht.";
			ResponsePayload responsePayload = ResponsePayload.messageOnly(MessagePayload.error(msg));
			return responsePayload;
		}

		if (veranstalter.size() > 1) {

			String msg = veranstalter.size() + " Veranstalter mit UUID like '" + veranstalterUuidPrefix + "%' gefunden.";
			ResponsePayload responsePayload = ResponsePayload.messageOnly(MessagePayload.error(msg));
			return responsePayload;
		}

		Veranstalter zuAendernder = veranstalter.get(0);

		if (neuerZugangsstatus == zuAendernder.zugangUnterlagen()) {

			String msg = "Veranstalter hatte bereits den gewünschten Zugangsstatus Unterlagen.";
			ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info(msg), neuerZugangsstatus);
			return responsePayload;
		}

		switch (neuerZugangsstatus) {

		case DEFAULT:
			zuAendernder.setzeZugangUnterlagenZurueck();
			break;

		case ENTZOGEN:
			zuAendernder.verwehreZugangUnterlagen();
			break;

		case ERTEILT:
			zuAendernder.erlaubeZugangUnterlagen();
			break;

		default:
			throw new IllegalArgumentException("unbekannter Zugangsstatus " + neuerZugangsstatus);
		}

		veranstalterRepository.changeVeranstalter(zuAendernder);

		String msg = "Zugangsstatus Unterlagen erfolgreich geändert.";
		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info(msg), neuerZugangsstatus);
		return responsePayload;
	}
}
