// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.model;

import java.time.LocalDateTime;

/**
 * DomainEvent
 */
public interface DomainEvent {

	/**
	 * @return LocalDateTime
	 */
	LocalDateTime occuredOn();

	/**
	 * @return String
	 */
	String typeName();
}
