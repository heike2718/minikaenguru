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

	String TYPE_LEHRER_CHANGED = "LehrerChanged";

	String TYPE_LEHRER_DEREGISTRED_FROM_SCHULE = "LehrerDeregistredFromSchule";

	String TYPE_PRIVATTEILNAHME_CREATED = "PrivatteilnahmeCreated";

	String TYPE_SCHULTEILNAHME_CREATED = "SchulteilnahmeCreated";

	String TYPE_SCHULTEILNAHME_CHANGED = "SchulteilnahmeChanged";

	String TYPE_SECURITY_INCIDENT_REGISTERED = "SecurityIncidentRegistered";

	String TYPE_DATA_INCONSISTENCY_REGISTERED = "DataInconsistencyRegistered";

	/**
	 * @return LocalDateTime
	 */
	LocalDateTime occuredOn();

	/**
	 * @return String
	 */
	String typeName();
}
