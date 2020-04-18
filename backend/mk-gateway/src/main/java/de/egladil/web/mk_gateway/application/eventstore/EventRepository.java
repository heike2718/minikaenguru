// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.application.eventstore;

import java.time.LocalDateTime;
import java.util.List;

import de.egladil.web.mk_gateway.infrastructure.persistence.entities.StoredEvent;

/**
 * EventRepository
 */
public interface EventRepository {

	/**
	 * Speichert das event in der Datenbank
	 *
	 * @param event
	 */
	void saveEvent(StoredEvent event);

	/**
	 * Liest alle Events, die neuer sind als der gegebene Zeitpunkt.
	 *
	 * @param  time
	 *              LocalDateTime
	 * @return      List
	 */
	List<StoredEvent> getEventsNewerThanTime(LocalDateTime time);

}
