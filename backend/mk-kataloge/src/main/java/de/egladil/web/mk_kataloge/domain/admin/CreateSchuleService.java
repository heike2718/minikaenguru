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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.SchuleRepository;
import de.egladil.web.mk_kataloge.domain.apimodel.SchulePayload;
import de.egladil.web.mk_kataloge.domain.error.DuplicateEntityException;
import de.egladil.web.mk_kataloge.domain.error.KatalogAPIException;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * CreateSchuleService
 */
@ApplicationScoped
public class CreateSchuleService {

	private static final Logger LOG = LoggerFactory.getLogger(CreateSchuleService.class);

	@Inject
	SchuleRepository schuleRepository;

	@Inject
	ChangeSchulenMailDelegate mailDelegate;

	/**
	 * Falls es die Schule noch nicht gibt (Gleichheit ortKuerzel und Name), wird sie angelegt und persistiert. Eine Meil wird an
	 * die Adresse des Auftraggebers gesendet, sofern diese angegeben ist. Wenn es den Ort oder das Land noch nicgt gibt, wird es
	 * ebenfalls angelegt
	 *
	 * @param  schulePayload
	 * @return               ResponsePayload mit SchulePayload als Daten.
	 */
	public ResponsePayload schuleAnlegen(final SchulePayload schulePayload) {

		try {

			List<Schule> schulen = schuleRepository.findSchulenInOrt(schulePayload.kuerzelOrt());
			Optional<Schule> optSchule = schulen.stream()
				.filter(s -> !s.getKuerzel().equals(schulePayload.kuerzel()) && s.getName().equalsIgnoreCase(schulePayload.name()))
				.findFirst();

			if (optSchule.isPresent()) {

				SchulePayload result = SchulePayload.create(optSchule.get());

				if (StringUtils.isNotBlank(schulePayload.emailAuftraggeber())) {

					this.mailDelegate.sendSchuleCreatedMailQuietly(schulePayload);
				}
				return new ResponsePayload(MessagePayload.warn("Diese Schule gibt es bereits."), result);
			}

			Schule schule = mapFromSchulePayload(schulePayload);

			boolean added = schuleRepository.addSchule(schule);
			LOG.debug("Schule added=" + added);

			if (StringUtils.isNotBlank(schulePayload.emailAuftraggeber())) {

				this.mailDelegate.sendSchuleCreatedMailQuietly(schulePayload);
			} else {

				LOG.debug("emailAuftraggeber war blank - keine Mail gesendet.");
			}

			return new ResponsePayload(MessagePayload.info("Die Schule wurde erfolgreich angelegt."), schulePayload);

		} catch (DuplicateEntityException e) {

			String msg = "schuleAnlegen: " + e.getMessage();
			throw new KatalogAPIException(msg);
		} catch (PersistenceException e) {

			LOG.error("Die Schule {} konnte nicht angelegt werden: {}", schulePayload, e.getMessage(), e);
			throw new KatalogAPIException("Die Schule konnte wegen eines Serverfehlers nicht angelegt werden.");
		}
	}

	Schule mapFromSchulePayload(final SchulePayload schulePayload) {

		Schule result = new Schule();
		result.setImportiertesKuerzel(schulePayload.kuerzel());
		result.setLandKuerzel(schulePayload.kuerzelLand());
		result.setLandName(schulePayload.nameLand());
		result.setName(schulePayload.name());
		result.setOrtKuerzel(schulePayload.kuerzelOrt());
		result.setOrtName(schulePayload.nameOrt());

		return result;
	}
}
