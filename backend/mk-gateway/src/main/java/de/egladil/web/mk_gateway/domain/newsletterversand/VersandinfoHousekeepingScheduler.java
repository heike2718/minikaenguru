// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * VersandinfoHousekeepingScheduler scheduled einen Job, der die Tabellen VERSANDINFOS und NEWSLETTERAUSLIEFERUNGEN aufräumt.
 */
@ApplicationScoped
public class VersandinfoHousekeepingScheduler {

	// @Scheduled(cron = "{versandinfos.housekeeping.expr}")
	void cronJob() {

	}

}
