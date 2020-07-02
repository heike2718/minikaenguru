// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.event;

import java.time.LocalDateTime;

/**
 * MkGatewayDomainEvent
 */
public interface MkGatewayDomainEvent {

	String TYPE_SECURITY_INCIDENT_REGISTERED = "SecurityIncidentRegistered";

	String TYPE_DATA_INCONSISTENCY_REGISTERED = "DataInconsistencyRegistered";

	String TYPE_USER_LOGGED_IN = "UserLoggedIn";

	String TYPE_USER_LOGGED_OUT = "UserLoggedOut";

	/**
	 * @return LocalDateTime
	 */
	LocalDateTime occuredOn();

	/**
	 * @return String
	 */
	String typeName();
}
