// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.health;

import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Pacemaker;

/**
 * PacemakerRepository
 */
public interface PacemakerRepository {

	Pacemaker findById(String monitoringId);

	Pacemaker change(Pacemaker pacemaker);

}
