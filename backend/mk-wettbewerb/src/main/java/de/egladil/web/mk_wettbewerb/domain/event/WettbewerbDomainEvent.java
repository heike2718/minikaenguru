// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.event;

import java.time.LocalDateTime;

/**
 * WettbewerbDomainEvent
 */
public interface WettbewerbDomainEvent {

	/**
	 * @return LocalDateTime
	 */
	LocalDateTime occuredOn();

	/**
	 * @return String
	 */
	String typeName();
}
