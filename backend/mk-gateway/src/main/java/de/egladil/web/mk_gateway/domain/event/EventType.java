// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.event;

/**
 * EventType
 */
public enum EventType {

	DATA_INCONSISTENCY_REGISTERED("DataInconsistencyRegistered"),
	DELETE_VERANSTALTER_FAILED("DeleteVeranstalterFailed"),
	KIND_CHANGED("KindChanged"),
	KIND_CREATED("KindCreated"),
	KIND_DELETED("KindDeleted"),
	LEHRER_CHANGED("LehrerChanged"),
	LEHRER_DEREGISTRED_FROM_SCHULE("LehrerDeregistredFromSchule"),
	PRIVATTEILNAHME_CREATED("PrivatteilnahmeCreated"),
	SCHULTEILNAHME_CREATED("SchulteilnahmeCreated"),
	SCHULTEILNAHME_CHANGED("SchulteilnahmeChanged"),
	SECURITY_INCIDENT_REGISTERED("SecurityIncidentRegistered"),
	SYNCHRONIZE_VERANSTALTER_FAILED("SynchronizeVeranstalterFailed"),
	USER_LOGGED_IN("UserLoggedIn"),
	USER_LOGGED_OUT("UserLoggedOut");

	private final String label;

	private EventType(final String label) {

		this.label = label;
	}

	public String getLabel() {

		return label;
	}

}
