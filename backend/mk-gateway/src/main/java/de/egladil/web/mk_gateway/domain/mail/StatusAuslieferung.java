// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

/**
 * StatusAuslieferung Status des Versands eines bestimmten Newsletters an eine Gruppe von Mailempfaengern.
 */
public enum StatusAuslieferung {

	WAITING,
	IN_PROGRESS,
	COMPLETED,
	ERRORS;

}
