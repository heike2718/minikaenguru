// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.NewsletterAuslieferung;
import de.egladil.web.mk_gateway.domain.mail.StatusAuslieferung;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenteNewsletterauslieferung;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

/**
 * NewsletterauslieferungenRepository
 */
@RequestScoped
public class NewsletterauslieferungenRepository {

	@Inject
	EntityManager em;

	/**
	 * Gibt die Liste aller Auslieferungen zu diesem Newsletter zurück.
	 *
	 * @param  versandauftragId
	 * @return
	 */
	public List<NewsletterAuslieferung> findAllWithVersandauftrag(final Identifier versandauftragId) {

		List<PersistenteNewsletterauslieferung> persistenteAuslieferungen = em
			.createNamedQuery(PersistenteNewsletterauslieferung.FIND_ALL_BY_VERDANDAUFTRAG_ID,
				PersistenteNewsletterauslieferung.class)
			.setParameter("versandauftragId", versandauftragId)
			.setParameter("statusCompleted", StatusAuslieferung.COMPLETED)
			.getResultList();

		List<NewsletterAuslieferung> result = persistenteAuslieferungen.stream().map(this::mapFromDB).toList();

		return result;
	}

	/**
	 * @param auslieferung
	 */
	public void updateAuslieferung(final NewsletterAuslieferung auslieferung) {

		PersistenteNewsletterauslieferung ausDB = em.find(PersistenteNewsletterauslieferung.class,
			auslieferung.getIdentifier().identifier());

		mergeAttributes(ausDB, auslieferung);

		em.merge(ausDB);

	}

	void mergeAttributes(final PersistenteNewsletterauslieferung ausDB, final NewsletterAuslieferung auslieferung) {

		/// SORTNR ist nicht änderbar
		ausDB.setEmpfaengergruppe(StringUtils.join(auslieferung.getEmpfaenger(), ","));
		ausDB.setVersandauftragId(auslieferung.getVersandauftragId().identifier());
		ausDB.setStatus(auslieferung.getStatus());
	}

	NewsletterAuslieferung mapFromDB(final PersistenteNewsletterauslieferung ausDB) {

		String[] alleEmpfaenger = StringUtils.split(ausDB.getEmpfaengergruppe(), ",");

		return new NewsletterAuslieferung()
			.withEmpfaenger(alleEmpfaenger)
			.withIdentifier(new Identifier(ausDB.getUuid()))
			.withVersandauftragId(new Identifier(ausDB.getVersandauftragId()))
			.withStatus(ausDB.getStatus());
	}

}
