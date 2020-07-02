// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.event;

import java.time.LocalDateTime;

/**
 * KatalogeDomainEvent
 */
public interface KatalogeDomainEvent {

	String TYPE_SECURITY_INCIDENT_REGISTERED = "KATALOG:SecurityIncidentRegistered";

	String TYPE_DATA_INCONSISTENCY_REGISTERED = "KATALOG:DataInconsistencyRegistered";

	String TYPE_KATALOG_ANTRAG_RECEIVED = "KATALOG:KatalogAntragReceived";

	/**
	 * @return LocalDateTime
	 */
	LocalDateTime occuredOn();

	/**
	 * @return String
	 */
	String typeName();
}
