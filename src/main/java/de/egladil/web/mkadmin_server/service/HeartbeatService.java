//=====================================================
// Project: mkadmin-server
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mkadmin_server.service;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons.payload.MessagePayload;
import de.egladil.web.commons.payload.ResponsePayload;
import de.egladil.web.mkadmin_server.dao.PacemakerDao;
import de.egladil.web.mkadmin_server.domain.Pacemaker;

/**
 * HeartbeatService
 */
@ApplicationScoped
public class HeartbeatService {

	private static final String MONITOR_ID = "mkverwaltung-database";

	private static final Logger LOG = LoggerFactory.getLogger(HeartbeatService.class.getSimpleName());

	@Inject
	PacemakerDao pacemakerDao;

	/**
	 *
	 */
	public HeartbeatService() {
	}

	/**
	 * @param pacemakerDao
	 */
	HeartbeatService(final PacemakerDao pacemakerDao) {
		this.pacemakerDao = pacemakerDao;
	}

	/**
	 * @return ResponsePayload.
	 */
	public ResponsePayload update() {
		try {
			Optional<Pacemaker> optPacemaker = pacemakerDao.findByUniqueIdentifier(HeartbeatService.MONITOR_ID);

			if (!optPacemaker.isPresent()) {
				String msg = "Fehler beim updaten des pacemakers: es gibt keinen Eintrag in mkverwaltung.PACEMAKERS mit MONITOR_ID="
					+ MONITOR_ID;
				LOG.error(msg);
				return ResponsePayload.messageOnly(MessagePayload.error(msg));
			}

			Pacemaker pacemaker = optPacemaker.get();
			pacemaker.setWert("wert-" + System.currentTimeMillis());
			Pacemaker result = pacemakerDao.save(pacemaker);
			LOG.debug("pacemaker aktualisiert: {}", result);
			return ResponsePayload.messageOnly(MessagePayload.info(MONITOR_ID + " lebt"));
		} catch (Exception e) {
			String msg = "Fehler beim Speichern des pacemakers " + MONITOR_ID + ": " + e.getMessage();
			LOG.error("Fehler beim updaten des pacemakers: {}", e.getMessage(), e);
			return ResponsePayload.messageOnly(MessagePayload.error(msg));
		}
	}

}
