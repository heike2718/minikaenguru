// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * NewsletterversandScheduler
 */
@ApplicationScoped
public class NewsletterversandScheduler {

	@Inject
	NewsletterversandService newsletterversandService;

	@Scheduled(cron = "{newsletterversand.cron.expr}")
	void cronJob() {

		newsletterversandService.checkAndSend();
	}

}
