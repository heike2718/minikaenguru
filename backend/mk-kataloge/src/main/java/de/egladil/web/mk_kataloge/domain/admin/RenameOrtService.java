// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.admin;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.SchuleRepository;
import de.egladil.web.mk_kataloge.domain.apimodel.OrtPayload;
import de.egladil.web.mk_kataloge.domain.error.KatalogAPIException;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Ort;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * RenameOrtService
 */
@ApplicationScoped
public class RenameOrtService {

	private static final Logger LOG = LoggerFactory.getLogger(RenameOrtService.class);

	@Inject
	SchuleRepository schuleRepository;

	/**
	 * @param  schuleRepository2
	 * @param  mailDelegate
	 * @return
	 */
	static RenameOrtService createForTest(final SchuleRepository schuleRepository) {

		RenameOrtService result = new RenameOrtService();
		result.schuleRepository = schuleRepository;
		return result;
	}

	/**
	 * Bennennt den Ort um und gibt ihn als KatalogItem zuürück.
	 *
	 * @param  ortPayload
	 *                    OrtPayload
	 * @return
	 */
	public ResponsePayload ortUmbenennen(final OrtPayload ortPayload) {

		try {

			Optional<Ort> optOrt = schuleRepository.getOrt(ortPayload.kuerzel());

			if (optOrt.isEmpty()) {

				throw new NotFoundException(Response.status(404)
					.entity(new ResponsePayload(MessagePayload.error("Diesen Ort gibt es nicht."), ortPayload)).build());
			}

			Ort ort = optOrt.get();

			if (!ort.getLandKuerzel().equals(ortPayload.kuerzelLand())) {

				String msg = "Umbenennung abgelehnt: Land passt nicht.";
				LOG.warn(msg + " - " + ort.printForLog() + ", " + ortPayload.toString());
				throw new WebApplicationException(
					Response.status(412).entity(ResponsePayload.messageOnly(MessagePayload.error(msg))).build());
			}

			List<Ort> orte = schuleRepository.findOrteInLand(ortPayload.kuerzelLand());

			Optional<Ort> optOrtGleichenNamens = orte.stream()
				.filter(o -> !o.getKuerzel().equals(ortPayload.kuerzel()) && ortPayload.name().equalsIgnoreCase(o.getName()))
				.findFirst();

			if (optOrtGleichenNamens.isPresent()) {

				return new ResponsePayload(MessagePayload.warn(
					"Umbenennung abgelehnt: Es gibt im gleichen Land bereits einen anderen Ort mit dem Namen " + ortPayload.name()
						+ ". Dieser wurde zurückgegeben."),
					OrtPayload.create(optOrtGleichenNamens.get()));
			}

			List<Schule> schulen = schuleRepository.findSchulenInOrt(ortPayload.kuerzel());

			schulen.forEach(s -> s.setOrtName(ortPayload.name()));

			schuleRepository.replaceSchulen(schulen);

			LOG.info("Anzahl geänderter Schulen: {}", schulen.size());

			return new ResponsePayload(
				MessagePayload.info("Der Ort wurde erfolgreich umbenannt. Anzahl geänderter Schulen: " + schulen.size()),
				ortPayload);

		} catch (PersistenceException e) {

			LOG.error("Die Schulen zum Ort {} konnten nicht geändert werden: {}", ortPayload, e.getMessage(), e);
			throw new KatalogAPIException("Der Ort konnte wegen eines Serverfehlers nicht umbenannt werden.");
		}

	}

}
