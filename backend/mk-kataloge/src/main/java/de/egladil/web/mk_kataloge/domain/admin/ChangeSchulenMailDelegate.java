// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.admin;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_mailer.DefaultEmailDaten;
import de.egladil.web.mk_kataloge.domain.apimodel.SchulePayload;
import de.egladil.web.mk_kataloge.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_kataloge.domain.event.MailNotSent;
import de.egladil.web.mk_kataloge.domain.katalogantrag.KatalogMailService;

/**
 * ChangeSchulenMailDelegate
 */
@ApplicationScoped
public class ChangeSchulenMailDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(ChangeSchulenMailDelegate.class);

	@ConfigProperty(name = "bccEmpfaengerSchulkatalogantrag", defaultValue = "minikaenguru@egladil.de")
	String bccEmpfaenger;

	@Inject
	KatalogMailService mailService;

	@Inject
	Event<MailNotSent> mailNotSentEvent;

	private MailNotSent mailNotSent;

	static ChangeSchulenMailDelegate createForTest(final KatalogMailService mailService) {

		ChangeSchulenMailDelegate result = new ChangeSchulenMailDelegate();
		result.mailService = mailService;
		return result;

	}

	MailNotSent getMailNotSent() {

		return mailNotSent;
	}

	boolean sendSchuleCreatedMailQuietly(final SchulePayload schulePayload) {

		try {

			DefaultEmailDaten emailDaten = createMailDaten(schulePayload);
			this.mailService.sendMail(emailDaten);
			return true;
		} catch (Exception e) {

			String msg = "Die Mail konnte nicht gesendet werden: " + e.getMessage();
			LOG.warn(msg);

			this.mailNotSent = new LoggableEventDelegate().fireMailNotSentEvent(msg, mailNotSentEvent);
			return false;
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

}
