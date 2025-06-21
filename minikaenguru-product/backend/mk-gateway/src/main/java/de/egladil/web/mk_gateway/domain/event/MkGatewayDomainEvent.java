// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.event;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * MkGatewayDomainEvent
 */
public interface MkGatewayDomainEvent {

	/**
	 * @return LocalDateTime
	 */
	@JsonIgnore
	LocalDateTime occuredOn();

	/**
	 * @return String
	 */
	@JsonIgnore
	String typeName();
}
