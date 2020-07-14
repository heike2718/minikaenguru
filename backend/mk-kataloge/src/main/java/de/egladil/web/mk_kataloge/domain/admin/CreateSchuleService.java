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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_mailer.DefaultEmailDaten;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.SchuleRepository;
import de.egladil.web.mk_kataloge.domain.apimodel.SchulePayload;
import de.egladil.web.mk_kataloge.domain.error.DataInconsistencyException;
import de.egladil.web.mk_kataloge.domain.error.KatalogAPIException;
import de.egladil.web.mk_kataloge.domain.event.DataInconsistencyRegistered;
import de.egladil.web.mk_kataloge.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_kataloge.domain.event.MailNotSent;
import de.egladil.web.mk_kataloge.domain.katalogantrag.KatalogMailService;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * CreateSchuleService
 */
@ApplicationScoped
public class CreateSchuleService {

	private static final Logger LOG = LoggerFactory.getLogger(CreateSchuleService.class);

	@ConfigProperty(name = "bccEmpfaengerSchulkatalogantrag", defaultValue = "minikaenguru@egladil.de")
	String bccEmpfaenger;

	@Inject
	SchuleRepository schuleRepository;

	@Inject
	KatalogMailService mailService;

	@Inject
	Event<DataInconsistencyRegistered> dataInconsistencyEvent;

	Event<MailNotSent> mailNotSentEvent;

	private DataInconsistencyRegistered registeredDataInconsistency;

	private MailNotSent mailNotSent;

	private boolean test;

	public static CreateSchuleService createForTest(final SchuleRepository schuleRepo, final KatalogMailService mailService) {

		CreateSchuleService result = new CreateSchuleService();
		result.schuleRepository = schuleRepo;
		result.mailService = mailService;
		result.test = true;
		return result;
	}

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

			Optional<Schule> optSchule = schuleRepository.findSchuleInOrtMitName(schulePayload.kuerzelOrt(), schulePayload.name());

			if (optSchule.isPresent()) {

				SchulePayload result = SchulePayload.create(optSchule.get());

				if (StringUtils.isNotBlank(schulePayload.emailAuftraggeber())) {

					this.sendSchuleCreatedMailQuietly(schulePayload);
				}
				return new ResponsePayload(MessagePayload.warn("Diese Schule gibt es bereits."), result);
			}

			Schule schule = mapFromSchulePayload(schulePayload);

			if (test) {

				// Mockito-Tests beruhen auf schule.equals(), also gleichen kuerzeln. Daher im Test vorher das kuerzel setzen.
				schule.setKuerzel(schulePayload.kuerzel());
			}

			boolean added = schuleRepository.addSchule(schule);
			LOG.debug("Schule added=" + added);

			if (StringUtils.isNotBlank(schulePayload.emailAuftraggeber())) {

				this.sendSchuleCreatedMailQuietly(schulePayload);
			} else {

				LOG.info("emailAuftraggeber war blank - keine Mail gesendet.");
			}

			return new ResponsePayload(MessagePayload.info("Die Schule wurde erfolgreich angelegt."), schulePayload);

		} catch (DataInconsistencyException e) {

			String msg = "schuleAnlegen: " + e.getMessage();
			registeredDataInconsistency = new LoggableEventDelegate().fireDataInconsistencyEvent(msg, dataInconsistencyEvent);
			throw new KatalogAPIException(registeredDataInconsistency.message());
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

	private void sendSchuleCreatedMailQuietly(final SchulePayload schulePayload) {

		try {

			DefaultEmailDaten emailDaten = createMailDaten(schulePayload);
			this.mailService.sendMail(emailDaten);
		} catch (Exception e) {

			String msg = "Die Mail konnte nicht gesendet werden: " + e.getMessage();
			LOG.warn(msg);

			this.mailNotSent = new LoggableEventDelegate().fireMailNotSentEvent(msg, mailNotSentEvent);
		}
	}

	private DefaultEmailDaten createMailDaten(final SchulePayload schulePayload) {

		DefaultEmailDaten result = new DefaultEmailDaten();
		result.setBetreff("Minikänguru: Schulkatalog");
		result.setText(new SchuleEingetragenMailtextGenerator().getSchuleEingetragenText(schulePayload));
		result.setEmpfaenger(schulePayload.emailAuftraggeber());
		result.addHiddenEmpfaenger(bccEmpfaenger);
		return result;

	}

	DataInconsistencyRegistered getRegisteredDataInconsistency() {

		return registeredDataInconsistency;
	}

	MailNotSent getMailNotSent() {

		return mailNotSent;
	}

}
