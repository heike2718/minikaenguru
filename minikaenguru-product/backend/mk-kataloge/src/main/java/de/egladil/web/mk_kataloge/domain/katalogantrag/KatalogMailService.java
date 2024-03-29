// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.katalogantrag;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.egladil.web.commons_mailer.CommonEmailService;
import de.egladil.web.commons_mailer.DefaultEmailDaten;
import de.egladil.web.commons_mailer.EmailServiceCredentials;
import de.egladil.web.commons_mailer.exception.EmailException;
import de.egladil.web.commons_mailer.exception.InvalidMailAddressException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * KatalogMailService
 */
@ApplicationScoped
public class KatalogMailService {

	@ConfigProperty(name = "mockTheMailserver")
	boolean mockTheMailserver;

	@Inject
	EmailServiceCredentials emailServiceCredentials;

	@Inject
	CommonEmailService commonMailService;

	private boolean mailSent;

	/**
	 * Sendet die Mail.
	 *
	 * @param  maildaten
	 * @throws EmailException
	 * @throws InvalidMailAddressException
	 */
	public void sendMail(final DefaultEmailDaten maildaten) throws EmailException, InvalidMailAddressException {

		if (!mockTheMailserver) {

			this.commonMailService.sendMail(maildaten, emailServiceCredentials);
		} else {

			System.out.println("Mail mit Betreff " + maildaten.getBetreff() + " wurde an "
				+ maildaten.alleEmpfaengerFuersLog() + " gesendet (TO=" + maildaten.getEmpfaenger() + "):\n" + maildaten.getText());
		}

		mailSent = true;
	}

	public boolean isMailSent() {

		return mailSent;
	}

}
