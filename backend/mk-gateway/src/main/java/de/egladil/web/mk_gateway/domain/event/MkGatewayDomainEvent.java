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

	String TYPE_LEHRER_CHANGED = "LehrerChanged";

	String TYPE_LEHRER_DEREGISTRED_FROM_SCHULE = "LehrerDeregistredFromSchule";

	String TYPE_PRIVATTEILNAHME_CREATED = "PrivatteilnahmeCreated";

	String TYPE_SCHULTEILNAHME_CREATED = "SchulteilnahmeCreated";

	String TYPE_SCHULTEILNAHME_CHANGED = "SchulteilnahmeChanged";

	String TYPE_DELETE_VERANSTALTER_FAILED = "DeleteVeranstalterFailed";

	String TYPE_SYNCHRONIZE_VERANSTALTER_FAILED = "SynchronizeVeranstalterFailed";

	/**
	 * @return LocalDateTime
	 */
	LocalDateTime occuredOn();

	/**
	 * @return String
	 */
	String typeName();
}
