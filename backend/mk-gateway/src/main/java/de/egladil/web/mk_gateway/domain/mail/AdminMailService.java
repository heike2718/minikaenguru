// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_mailer.CommonEmailService;
import de.egladil.web.commons_mailer.DefaultEmailDaten;
import de.egladil.web.commons_mailer.EmailServiceCredentials;
import de.egladil.web.commons_mailer.exception.EmailException;
import de.egladil.web.commons_mailer.exception.InvalidMailAddressException;

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
	 * @param  maildaten
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

			LOGGER.info("Mail mit Betreff " + maildaten.getBetreff() + " wurde an "
				+ maildaten.alleEmpfaengerFuersLog() + " gesendet (TO=" + maildaten.getEmpfaenger() + "):\n" + maildaten.getText());
		}

		mailSent = true;
	}

	public boolean isMailSent() {

		return mailSent;
	}

	AdminEmailsConfiguration getMailConfig() {

		return mailConfig;
	}

}
