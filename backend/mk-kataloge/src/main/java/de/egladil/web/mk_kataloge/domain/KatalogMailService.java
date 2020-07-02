// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.egladil.web.commons_mailer.CommonEmailService;
import de.egladil.web.commons_mailer.DefaultEmailDaten;
import de.egladil.web.commons_mailer.EmailServiceCredentials;
import de.egladil.web.commons_mailer.exception.EmailException;
import de.egladil.web.commons_mailer.exception.InvalidMailAddressException;

/**
 * KatalogMailService
 */
@ApplicationScoped
public class KatalogMailService {

	@ConfigProperty(name = "mockTheMailserver", defaultValue = "false")
	boolean mockTheMailserver;

	@Inject
	EmailServiceCredentials emailServiceCredentials;

	@Inject
	CommonEmailService commonMailService;

	public static KatalogMailService createForTest() {

		KatalogMailService result = new KatalogMailService();
		result.mockTheMailserver = true;
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

		if (!mockTheMailserver) {

			this.commonMailService.sendMail(maildaten, emailServiceCredentials);
		} else {

			System.out.println("Mail mit Betreff " + maildaten.getBetreff() + " wurde an "
				+ maildaten.alleEmpfaengerFuersLog() + " gesendet (TO=" + maildaten.getEmpfaenger() + "):\n" + maildaten.getText());
		}
	}

}
