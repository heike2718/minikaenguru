// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.health;

import de.egladil.web.mk_gateway.infrastructure.persistence.entities.Pacemaker;

/**
 * PacemakerRepository
 */
public interface PacemakerRepository {

	Pacemaker findById(String monitoringId);

	Pacemaker change(Pacemaker pacemaker);

}
