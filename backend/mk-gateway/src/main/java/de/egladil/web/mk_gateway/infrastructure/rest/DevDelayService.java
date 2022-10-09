// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * DevDelayService
 */
@ApplicationScoped
public class DevDelayService {

	@ConfigProperty(name = "delay.milliseconds", defaultValue = "0")
	long delayMillis = 0;

	public void pause() {

		if (delayMillis == 0) {

			return;
		}

		try {

			Thread.sleep(delayMillis);
		} catch (InterruptedException e) {

			//
		}
	}

}
