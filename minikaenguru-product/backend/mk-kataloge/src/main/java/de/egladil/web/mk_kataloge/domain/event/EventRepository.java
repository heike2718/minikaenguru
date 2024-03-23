// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.event;

import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.StoredEvent;

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

}
