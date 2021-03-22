// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.admin;

import java.util.List;

import javax.inject.Inject;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VeranstalterSuchanfrage;

/**
 * AbstractVeranstalterAendernService
 */
public abstract class AbstractVeranstalterAendernService {

	@Inject
	VeranstalterRepository veranstalterRepository;

	/**
	 * TemplateMethod zum Ändern einzelner Veranstaltereigenschaften.
	 *
	 * @param  veranstalterUuidPrefix
	 *                                String der Anfang der UUID des Veranstalters.
	 * @param  data
	 * @return                        ResponsePayload
	 */
	public ResponsePayload aendereVeranstalter(final String veranstalterUuidPrefix, final Object data) {

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

		return specialAendereVeranstalter(zuAendernder, data);
	}

	/**
	 * Nimmt die spezielle Änderung vor.
	 *
	 * @param  zuAendernderVeranstalter
	 *                                  Veranstalter
	 * @param  data
	 *                                  Object
	 * @return                          ResponsePayload
	 */
	protected abstract ResponsePayload specialAendereVeranstalter(Veranstalter zuAendernderVeranstalter, Object data);
}
