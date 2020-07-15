// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.admin;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.SchuleRepository;
import de.egladil.web.mk_kataloge.domain.apimodel.SchulePayload;
import de.egladil.web.mk_kataloge.domain.error.DataInconsistencyException;
import de.egladil.web.mk_kataloge.domain.error.KatalogAPIException;
import de.egladil.web.mk_kataloge.domain.event.DataInconsistencyRegistered;
import de.egladil.web.mk_kataloge.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

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

	@Inject
	Event<DataInconsistencyRegistered> dataInconsistencyEvent;

	private DataInconsistencyRegistered registeredDataInconsistency;

	private boolean test;

	static RenameSchuleService createForTest(final SchuleRepository schuleRepository, final ChangeSchulenMailDelegate mailDelegate) {

		RenameSchuleService result = new RenameSchuleService();
		result.schuleRepository = schuleRepository;
		result.mailDelegate = mailDelegate;
		result.test = true;
		return result;

	}

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

			Optional<Schule> optSchule = schuleRepository.findSchuleByKuerzel(schulePayload.kuerzel());

			if (optSchule.isEmpty()) {

				throw new NotFoundException(Response.status(404)
					.entity(ResponsePayload.messageOnly(MessagePayload.error("Diese Schule gibt es nicht."))).build());
			}

			Schule schule = optSchule.get();

			if (!schule.getOrtKuerzel().equals(schulePayload.kuerzelOrt())
				|| !schule.getLandKuerzel().equals(schulePayload.kuerzelLand())) {

				String msg = "Änderung abgelehnt: Ort oder Land passt nicht.";
				LOG.warn(msg + " - " + schule.printForLog() + ", " + schulePayload.toString());
				throw new WebApplicationException(
					Response.status(412).entity(ResponsePayload.messageOnly(MessagePayload.error(msg))).build());
			}

			optSchule = schuleRepository.findSchuleInOrtMitName(schulePayload.kuerzelOrt(), schulePayload.name());

			if (optSchule.isPresent() && !optSchule.get().getKuerzel().equals(schulePayload.kuerzel())) {

				SchulePayload result = SchulePayload.create(optSchule.get());

				if (StringUtils.isNotBlank(schulePayload.emailAuftraggeber())) {

					this.mailDelegate.sendSchuleCreatedMailQuietly(schulePayload);
				}
				return new ResponsePayload(MessagePayload.warn(
					"Es gibt im gleichen Ort bereits eine andere Schule mit dem Namen " + schulePayload.name() + "."), result);
			}

			if (test) {

				// Mockito-Tests beruhen auf schule.equals(), also gleichen kuerzeln. Daher im Test vorher das kuerzel setzen.
				schule.setKuerzel(schulePayload.kuerzel());
			}

			schule.setName(schulePayload.name());

			boolean replaced = schuleRepository.replaceSchule(schule);
			LOG.debug("Schule replaced=" + replaced);

			if (StringUtils.isNotBlank(schulePayload.emailAuftraggeber())) {

				this.mailDelegate.sendSchuleCreatedMailQuietly(schulePayload);
			} else {

				LOG.debug("emailAuftraggeber war blank - keine Mail gesendet.");
			}

			return new ResponsePayload(MessagePayload.info("Die Schule wurde erfolgreich geändert."), schulePayload);
		} catch (DataInconsistencyException e) {

			String msg = "schuleUmbenennen: " + e.getMessage();
			registeredDataInconsistency = new LoggableEventDelegate().fireDataInconsistencyEvent(msg, dataInconsistencyEvent);
			throw new KatalogAPIException(registeredDataInconsistency.message());
		} catch (PersistenceException e) {

			LOG.error("Die Schule {} konnte nicht geändert werden: {}", schulePayload, e.getMessage(), e);
			throw new KatalogAPIException("Die Schule konnte wegen eines Serverfehlers nicht geändert werden.");
		}
	}

	DataInconsistencyRegistered getRegisteredDataInconsistency() {

		return registeredDataInconsistency;
	}

}
