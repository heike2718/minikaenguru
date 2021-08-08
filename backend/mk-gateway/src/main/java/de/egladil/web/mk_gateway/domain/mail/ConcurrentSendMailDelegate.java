// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandFailed;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandFinished;

/**
 * ConcurrentSendMailDelegate
 */
public class ConcurrentSendMailDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(ConcurrentSendMailDelegate.class);

	private final Versandinformation versandinformation;

	private final DomainEventHandler domainEventHandler;

	public ConcurrentSendMailDelegate(final Versandinformation versandinformation, final DomainEventHandler domainEventHandler) {

		this.versandinformation = versandinformation;
		this.domainEventHandler = domainEventHandler;
	}

	public void mailsVersenden(final NewsletterTask newsletterTask) {

		ExecutorService executorservice = Executors.newSingleThreadExecutor();

		try {

			// Wenn man ein Future verwendet, wartet der Endpoint bis das Teil fertig ist. Daher Runnable!
			executorservice.execute(newsletterTask);

			LOG.info("Mailversand an executorService delegiert. Kann jetzt eine Weile dauern...");

		} catch (Exception e) {

			if (e instanceof InterruptedException) {

				// hier ist nüscht zu tun, oder?
				LOG.debug("Interrupted");
			} else {

				Throwable cause = e.getCause();

				String msg = "Mailversand fehlgeschlagen: " + cause.getMessage();
				LOG.error(msg, cause);

				NewsletterversandFailed versandFailedEventPayload = new NewsletterversandFailed()
					.withMessage(msg);

				if (domainEventHandler != null) {

					domainEventHandler.handleEvent(versandFailedEventPayload);
				} else {

					System.out.println(versandFailedEventPayload.serializeQuietly());
				}

				String message = cause != null ? cause.getMessage() : e.getMessage();

				message = "Mailversand gescheitert (wird abgebrochen): " + message;

				String versandBeendetAm = CommonTimeUtils.format(CommonTimeUtils.now());

				NewsletterversandFinished finishedEventPayload = new NewsletterversandFinished()
					.withUuid(versandinformation.identifier().identifier())
					.withMessage(message)
					.withVersandBeendetAm(versandBeendetAm);

				if (domainEventHandler != null) {

					domainEventHandler.handleEvent(finishedEventPayload);
				} else {

					System.out.println(finishedEventPayload.serializeQuietly());
				}
			}

		} finally {

			executorservice.shutdown();
		}
	}

}
