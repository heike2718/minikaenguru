// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * NewsletterversandScheduler triggert einen Cron-Job.
 */
@ApplicationScoped
public class NewsletterversandScheduler {

	@Inject
	NewsletterAuslieferungProcessor newsletterversandService;

	@Scheduled(cron = "{newsletterversand.cron.expr}")
	void cronJob() {

		newsletterversandService.processNextAuslieferung();
	}

}
