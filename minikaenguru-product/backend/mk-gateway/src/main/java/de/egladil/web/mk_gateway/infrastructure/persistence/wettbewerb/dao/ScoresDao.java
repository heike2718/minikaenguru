// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.dao;

import de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities.ScoreAufgabeEntity;
import de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities.ScoreKlassenstufeEntity;
import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

/**
 * ScoresDao
 */
@RequestScoped
public class ScoresDao {

	@Inject
	@PersistenceUnit("wettbewerb")
	EntityManager entityManager;

	/**
	 * Schreibt neue Zeile in die Datenbank.
	 *
	 * @param scoreAufgabeEntity
	 */
	public void insertScoreAufgabe(final ScoreAufgabeEntity scoreAufgabeEntity) {

		entityManager.persist(scoreAufgabeEntity);
	}

	/**
	 * @param  scoreAufgabeEntity
	 * @return                    int die ID zum Vernüpfen mit den Aufgabenbewertungen.
	 */
	public int insertScoreKlassenstufe(final ScoreKlassenstufeEntity scoreAufgabeEntity) {

		entityManager.persist(scoreAufgabeEntity);
		return scoreAufgabeEntity.getId();
	}
}
