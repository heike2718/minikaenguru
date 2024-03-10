// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.admin;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.SchuleRepository;
import de.egladil.web.mk_kataloge.domain.apimodel.SchulePayload;
import de.egladil.web.mk_kataloge.domain.error.KatalogAPIException;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * RenameSchuleService
 */
@ApplicationScoped
public class RenameSchuleService {

	private static final Logger LOG = LoggerFactory.getLogger(RenameSchuleService.class);

	@Inject
	SchuleRepository schuleRepository;

	@Inject
	ChangeSchulenMailDelegate mailDelegate;

	/**
	 * Bennent die gegebene Schule um und gibt sie als KatalogItem zurück.
	 *
	 * @param  schulkuerzel
	 *                      String
	 * @param  neuerName
	 *                      String
	 * @return              KatalogItem
	 */
	public ResponsePayload schuleUmbenennen(final SchulePayload schulePayload) {

		try {

			Optional<Schule> optSchule = schuleRepository.getSchule(schulePayload.kuerzel());

			if (optSchule.isEmpty()) {

				throw new NotFoundException(Response.status(404)
					.entity(new ResponsePayload(MessagePayload.error("Diese Schule gibt es nicht."), schulePayload)).build());
			}

			Schule schule = optSchule.get();

			if (!schule.getOrtKuerzel().equals(schulePayload.kuerzelOrt())
				|| !schule.getLandKuerzel().equals(schulePayload.kuerzelLand())) {

				String msg = "Umbenennung abgelehnt: Ort oder Land passt nicht.";
				LOG.warn(msg + " - " + schule.printForLog() + ", " + schulePayload.toString());
				throw new WebApplicationException(
					Response.status(412).entity(ResponsePayload.messageOnly(MessagePayload.error(msg))).build());
			}

			List<Schule> schulen = schuleRepository.findSchulenInOrt(schulePayload.kuerzelOrt());
			optSchule = schulen.stream()
				.filter(s -> !s.getKuerzel().equals(schulePayload.kuerzel()) && s.getName().equalsIgnoreCase(schulePayload.name()))
				.findFirst();

			if (optSchule.isPresent()) {

				SchulePayload result = SchulePayload.create(optSchule.get());

				if (StringUtils.isNotBlank(schulePayload.emailAuftraggeber())) {

					this.mailDelegate.sendSchuleCreatedMailQuietly(schulePayload);
				}
				return new ResponsePayload(MessagePayload.warn(
					"Umbenennung abgelehnt: Es gibt im gleichen Ort bereits eine andere Schule mit dem Namen "
						+ schulePayload.name()
						+ ". Diese wurde zurückgegeben."),
					result);
			}

			schule.setName(schulePayload.name());

			boolean replaced = schuleRepository.replaceSchulen(Arrays.asList(new Schule[] { schule }));
			LOG.debug("Schule replaced=" + replaced);

			if (StringUtils.isNotBlank(schulePayload.emailAuftraggeber())) {

				this.mailDelegate.sendSchuleCreatedMailQuietly(schulePayload);
			} else {

				LOG.debug("emailAuftraggeber war blank - keine Mail gesendet.");
			}

			return new ResponsePayload(MessagePayload.info("Die Schule wurde erfolgreich geändert."), schulePayload);
		} catch (PersistenceException e) {

			LOG.error("Die Schule {} konnte nicht geändert werden: {}", schulePayload, e.getMessage(), e);
			throw new KatalogAPIException("Die Schule konnte wegen eines Serverfehlers nicht geändert werden.");
		}
	}

}
