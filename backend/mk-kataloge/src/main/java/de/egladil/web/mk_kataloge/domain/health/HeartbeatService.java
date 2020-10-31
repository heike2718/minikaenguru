// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.health;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Pacemaker;

/**
 * HeartbeatService
 */
@ApplicationScoped
public class HeartbeatService {

	public static final String MONITOR_ID = "mk-kataloge-database";

	@Inject
	PacemakerRepository pacemakerRepository;

	@Transactional
	public ResponsePayload updatePacemaker() {

		Pacemaker pacemaker = pacemakerRepository.findById(MONITOR_ID);

		if (pacemaker == null) {

			throw new RuntimeException("Konnte keinen Pacemaker mit uuid=" + MONITOR_ID + " finden :/");
		}

		pacemaker.setWert("wert-" + System.currentTimeMillis());
		pacemakerRepository.change(pacemaker);

		return ResponsePayload.messageOnly(MessagePayload.info(MONITOR_ID + " lebt"));

	}

}
