// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_mailer.CommonEmailService;
import de.egladil.web.commons_mailer.DefaultEmailDaten;
import de.egladil.web.commons_mailer.EmailServiceCredentials;
import de.egladil.web.commons_mailer.exception.EmailException;
import de.egladil.web.commons_mailer.exception.InvalidMailAddressException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.event.MailNotSent;
import de.egladil.web.mk_gateway.domain.kataloge.SchuleEingetragenMailtextGenerator;
import de.egladil.web.mk_gateway.domain.kataloge.api.SchulePayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * AdminMailService
 */
@ApplicationScoped
public class AdminMailService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdminMailService.class);

	@Inject
	AdminEmailsConfiguration mailConfig;

	@Inject
	EmailServiceCredentials emailServiceCredentials;

	@Inject
	CommonEmailService commonMailService;

	@Inject
	LoggableEventDelegate eventDelegate;

	@Inject
	DomainEventHandler domainEventHandler;

	private boolean mailSent;

	private boolean shouldThrowMailException = false;

	private boolean shouldThrowInvalidMailaddresses = false;

	public static AdminMailService createForTest() {

		AdminMailService result = new AdminMailService();
		result.mailConfig = AdminEmailsConfiguration.createForTest("hdwinkel@egladil.de", 3);
		return result;
	}

	public static AdminMailService createForTest(final AdminEmailsConfiguration mailConfig) {

		AdminMailService result = new AdminMailService();
		result.mailConfig = mailConfig;
		return result;
	}

	public static AdminMailService createForTestWithMailException() {

		AdminMailService result = new AdminMailService();
		result.mailConfig = AdminEmailsConfiguration.createForTest("hdwinkel@egladil.de", 3);
		result.shouldThrowMailException = true;
		return result;
	}

	public static AdminMailService createForTestWithInvalidMailaddressesException() {

		AdminMailService result = new AdminMailService();
		result.mailConfig = AdminEmailsConfiguration.createForTest("hdwinkel@egladil.de", 3);
		result.shouldThrowInvalidMailaddresses = true;
		return result;
	}

	/**
	 * Sendet die Mail.
	 *
	 * @param maildaten
	 * @throws EmailException
	 * @throws InvalidMailAddressException
	 */
	public void sendMail(final DefaultEmailDaten maildaten) throws EmailException, InvalidMailAddressException {

		if (!mailConfig.mockup()) {

			this.commonMailService.sendMail(maildaten, emailServiceCredentials);
		} else {

			if (shouldThrowMailException) {

				throw new EmailException("Das ist eine gemockte Mailexception");
			}

			if (shouldThrowInvalidMailaddresses) {

				throw new InvalidMailAddressException("Das ist eine gemockte InvalidMailAddressException",
					new SendFailedExceptionAdapter());
			}

			LOGGER.info("Mail mit Betreff " + maildaten.getBetreff() + " wurde an " + maildaten.alleEmpfaengerFuersLog()
				+ " gesendet (TO=" + maildaten.getEmpfaenger() + "):\n" + maildaten.getText());
		}

		mailSent = true;
	}

	public boolean isMailSent() {

		return mailSent;
	}

	AdminEmailsConfiguration getMailConfig() {

		return mailConfig;
	}

	/**
	 * Sendet eine Mail an den gegebenen Empfänger. Exceptions werden nur geloggt.
	 *
	 * @param payload SchulePayload
	 * @param bccEmpfaenger String
	 */
	public void sendSchuleCreatedMailQuietly(SchulePayload payload, String bccEmpfaenger) {
		try {

			DefaultEmailDaten emailDaten = createMailDaten(payload);
			emailDaten.addHiddenEmpfaenger(bccEmpfaenger);
			this.sendMail(emailDaten);
		} catch (Exception e) {

			String msg = "Die Mail konnte nicht gesendet werden: " + e.getMessage();
			LOGGER.warn(msg);

			MailNotSent mailNotSentEvent = new MailNotSent(msg);
			eventDelegate.fireMailNotSent(mailNotSentEvent, domainEventHandler);
		}
	}

	private DefaultEmailDaten createMailDaten(SchulePayload payload) {

		DefaultEmailDaten result = new DefaultEmailDaten();
		result.setBetreff("Minikänguru: Schulkatalog");
		result.setText(new SchuleEingetragenMailtextGenerator().getSchuleEingetragenText(payload));
		result.setEmpfaenger(payload.emailAuftraggeber());


		return result;
	}

}
