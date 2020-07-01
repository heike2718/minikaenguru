// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.event;

import java.time.LocalDateTime;

/**
 * WettbewerbDomainEvent
 */
public interface WettbewerbDomainEvent {

	String TYPE_SECURITY_INCIDENT_REGISTERED = "ADMIN:SecurityIncidentRegistered";

	String TYPE_DATA_INCONSISTENCY_REGISTERED = "ADMIN:DataInconsistencyRegistered";

	/**
	 * @return LocalDateTime
	 */
	LocalDateTime occuredOn();

	/**
	 * @return String
	 */
	String typeName();
}
