// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.enterprise.event.Event;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_mailer.DefaultEmailDaten;
import de.egladil.web.commons_mailer.exception.InvalidMailAddressException;
import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandFailed;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandFinished;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandProgress;

/**
 * NewsletterTask
 */
// Wenn man ein Future verwendet, wartet der Endpoint bis das Teil fertig ist. Daher Runnable!
public class NewsletterTask implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(NewsletterTask.class);

	private final Newsletter newsletter;

	private final Versandinformation versandinformation;

	private final List<List<String>> mailempfaengerGruppen;

	private final AdminMailService mailService;

	private final Event<NewsletterversandProgress> versandProgress;

	private final Event<NewsletterversandFailed> versandFailedEvent;

	private final Event<NewsletterversandFinished> versandFinished;

	public NewsletterTask(final NewsletterService newsletterService, final Newsletter newsletter, final Versandinformation versandinformation, final List<List<String>> mailempfaengerGruppen) {

		this.mailService = newsletterService.mailService;
		this.versandFailedEvent = newsletterService.versandFailedEvent;
		this.versandFinished = newsletterService.versandFinished;
		this.versandProgress = newsletterService.versandProgress;
		this.newsletter = newsletter;
		this.versandinformation = versandinformation;
		this.mailempfaengerGruppen = mailempfaengerGruppen;
	}

	@Override
	public void run() {

		try {

			this.call();
		} catch (Exception e) {

			LOG.error(e.getMessage(), e);

			throw new MkGatewayRuntimeException("Mailversand ist fehlgeschlagen: " + e.getMessage(), e);
		}

	}

	public Versandinformation call() throws Exception {

		int count = 0;
		int anzahlEmpfaenger = countMailempfaenger(mailempfaengerGruppen);
		String versandinfoUuid = this.versandinformation.identifier().identifier();

		String versandBegonnenAm = CommonTimeUtils.format(CommonTimeUtils.now());

		NewsletterversandProgress progressPayload = new NewsletterversandProgress()
			.withUuid(versandinfoUuid)
			.withAnzahlEmpfaenger(anzahlEmpfaenger)
			.withAktuellVersendet(count)
			.withVersandBegonnenAm(versandBegonnenAm);
		aktualisiereVersandinformation(progressPayload);

		versandinformation.withAnzahlEmpaenger(anzahlEmpfaenger);

		for (List<String> gruppe : mailempfaengerGruppen) {

			try {

				sendeMail(newsletter, gruppe);

				count += gruppe.size();

				progressPayload = new NewsletterversandProgress()
					.withUuid(versandinfoUuid)
					.withAnzahlEmpfaenger(anzahlEmpfaenger)
					.withAktuellVersendet(count)
					.withVersandBegonnenAm(versandBegonnenAm);

				aktualisiereVersandinformation(progressPayload);
				versandinformation.withAnzahlAktuellVersendet(count);

			} catch (InvalidMailAddressException e) {

				String msg = "Mail konnte nicht an alle Empfänger versendet werden";

				NewsletterversandFailed versandFailedEventPayload = new NewsletterversandFailed()
					.withUuid(versandinfoUuid)
					.withInvalidMailaddresses(e.getAllInvalidAdresses())
					.withMessage(msg)
					.withValidSentAddresses(e.getAllValidSentAddresses())
					.withValidUnsentAddresses(e.getAllValidUnsentAddresses());

				if (this.versandFailedEvent != null) {

					this.versandFailedEvent.fire(versandFailedEventPayload);
				} else {

					System.out.println(versandFailedEventPayload.serializeQuietly());
				}

				versandinformation.withFehlermeldung(msg);
			}

		}

		String versandBeendetAm = CommonTimeUtils.format(CommonTimeUtils.now());

		NewsletterversandFinished finishedEventPayload = new NewsletterversandFinished()
			.withUuid(versandinfoUuid)
			.withVersandBeendetAm(versandBeendetAm);

		if (versandFinished != null) {

			versandFinished.fire(finishedEventPayload);
		} else {

			System.out.println(finishedEventPayload.serializeQuietly());
		}

		versandinformation.withVersandBeendetAm(versandBeendetAm).withVersandBegonnenAm(versandBegonnenAm);

		return versandinformation;
	}

	/**
	 * @param progressPayload
	 */
	private void aktualisiereVersandinformation(final NewsletterversandProgress progressPayload) {

		if (this.versandProgress != null) {

			this.versandProgress.fire(progressPayload);
		} else {

			System.out.println(progressPayload.serializeQuietly());
		}

	}

	/**
	 * @param newsletter
	 * @param gruppe
	 */
	private void sendeMail(final Newsletter newsletter, final List<String> gruppe) {

		String text = getCompleteText(newsletter);

		DefaultEmailDaten maildaten = new DefaultEmailDaten();
		maildaten.setBetreff(newsletter.betreff());
		maildaten.setText(text);
		maildaten.addHiddenEmpfaenger(gruppe);

		waitQuietly();

		this.mailService.sendMail(maildaten);

		LOG.info("Mail an {} Empfaenger versendet", gruppe.size());
	}

	private String getCompleteText(final Newsletter newsletter) {

		try (InputStream in = getClass().getResourceAsStream("/mails/mailsuffix.txt"); StringWriter sw = new StringWriter()) {

			IOUtils.copy(in, sw, Charset.forName("UTF-8"));

			return newsletter.text() + sw.toString();

		} catch (IOException e) {

			LOG.warn("Standardmailende konnte nicht geladen werden: " + e.getMessage(), e);
			return newsletter.text();
		}

	}

	/**
	 *
	 */
	private void waitQuietly() {

		AdminEmailsConfiguration mailConfiguration = mailService.getMailConfig();

		try {

			int wartezeit = WartezeitUtil.getWartezeit(mailConfiguration.wartezeitMinSec(), mailConfiguration.wartezeitMaxSec());

			LOG.debug("warten {} Sekunden", wartezeit);

			TimeUnit.SECONDS.sleep(wartezeit);

			LOG.debug("Wartezeit vorbei");
		} catch (InterruptedException e) {

			LOG.debug("interrupt");
		}
	}

	private int countMailempfaenger(final List<List<String>> mailempfaengerGruppen) {

		int count = 0;

		for (List<String> gruppe : mailempfaengerGruppen) {

			count += gruppe.size();
		}

		return count;
	}
}
