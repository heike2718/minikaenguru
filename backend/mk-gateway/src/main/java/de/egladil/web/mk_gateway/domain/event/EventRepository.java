// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.event;

import java.time.LocalDateTime;
import java.util.List;

import de.egladil.web.mk_gateway.infrastructure.persistence.entities.StoredEvent;

/**
 * EventRepository
 */
public interface EventRepository {

	/**
	 * Speichert das event in der Datenbank.
	 *
	 * @param event
	 */
	void appendEvent(StoredEvent event);

	/**
	 * @param  datum
	 *               Date
	 * @return       LIst
	 */
	List<StoredEvent> findEventsAfter(LocalDateTime datum);

}
