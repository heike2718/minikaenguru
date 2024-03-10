// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import de.egladil.web.commons_mailer.DefaultEmailDaten;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.mail.api.MailAPIModel;

/**
 * MailToVeranstalterService
 */
@ApplicationScoped
public class MailToVeranstalterService {

	@Inject
	AdminMailService mailService;

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	public ResponsePayload sendeMailAnVeranstalter(final MailAPIModel mailModel) {

		DefaultEmailDaten maildaten = new DefaultEmailDaten();
		maildaten.setBetreff(mailModel.getBetreff());
		maildaten.setEmpfaenger(mailModel.getEmpfaenger());
		maildaten.setText(mailModel.getMailtext());
		maildaten.setMessageId("");

		mailService.sendMail(maildaten);

		String msg = MessageFormat.format(applicationMessages.getString("mailToVeranstalter.send.success"),
			new Object[] { mailModel.getEmpfaenger() });
		return ResponsePayload.messageOnly(MessagePayload.info(msg));
	}
}
