// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandFailed;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandFinished;

/**
 * ConcurrentSendMailDelegate
 */
@RequestScoped
public class ConcurrentSendMailDelegate {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentSendMailDelegate.class);

	@Inject
	Event<NewsletterversandFinished> versandFinished;

	@Inject
	Event<NewsletterversandFailed> versandFailedEvent;

	public void mailsVersenden(final NewsletterTask newsletterTask, final Versandinformation versandinformation) {

		ExecutorService executorservice = Executors.newSingleThreadExecutor();

		try {

			// Wenn man ein Future verwendet, wartet der Endpoint bis das Teil fertig ist. Daher Runnable!
			executorservice.execute(newsletterTask);

			LOGGER.info("Mailversand an executorService delegiert. Kann jetzt eine Weile dauern...");

		} catch (Exception e) {

			if (e instanceof InterruptedException) {

				// hier ist nüscht zu tun, oder?
				LOGGER.debug("Interrupted");
			} else {

				Throwable cause = e.getCause();

				String msg = "Mailversand fehlgeschlagen: " + cause.getMessage();
				LOGGER.error(msg, cause);

				NewsletterversandFailed versandFailedEventPayload = new NewsletterversandFailed()
					.withMessage(msg);

				if (versandFailedEvent != null) {

					versandFailedEvent.fire(versandFailedEventPayload);
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

				if (versandFinished != null) {

					versandFinished.fire(finishedEventPayload);
				} else {

					System.out.println(finishedEventPayload.serializeQuietly());
				}
			}

		} finally {

			executorservice.shutdown();
		}
	}

}
