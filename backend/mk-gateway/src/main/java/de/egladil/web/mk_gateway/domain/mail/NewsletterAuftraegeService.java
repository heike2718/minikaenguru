// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.api.VersandinfoAPIModel;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandFinished;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandProgress;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.VersandauftraegeHibernateRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

/**
 * NewsletterAuftraegeService
 */
@ApplicationScoped
public class NewsletterAuftraegeService {

	private static final Logger LOG = LoggerFactory.getLogger(NewsletterAuftraegeService.class);

	@Inject
	VersandauftraegeRepository versandauftraegeRepo;

	public static NewsletterAuftraegeService createForTest(final VersandauftraegeRepository repo) {

		NewsletterAuftraegeService result = new NewsletterAuftraegeService();
		result.versandauftraegeRepo = repo;
		return result;
	}

	public static NewsletterAuftraegeService createForIntegrationTest(final EntityManager entityManager) {

		NewsletterAuftraegeService result = new NewsletterAuftraegeService();
		result.versandauftraegeRepo = VersandauftraegeHibernateRepository.createForTest(entityManager);
		return result;
	}

	@Deprecated
	public List<Versandauftrag> getVersandinformationenZuNewsletter(final Identifier newsletterID) {

		if (newsletterID == null) {

			throw new IllegalArgumentException("newsletterID darf nicht null sein.");
		}

		return versandauftraegeRepo.findForNewsletter(newsletterID);
	}

	/**
	 * Gibt alle nicht beendeten Versandauftrag aufsteigend nach Einstellungsdatum zurück.
	 *
	 * @return
	 */
	public List<Versandauftrag> findNichtBeendeteVersandauftraege() {

		return versandauftraegeRepo.findNichtBeendeteVersandinfos();
	}

	/**
	 * Gibt die Versandinfo mit der UUID zurück.
	 *
	 * @param  versandinfoUuid
	 *                         String
	 * @return                 Optional
	 */
	@Deprecated
	public Optional<VersandinfoAPIModel> getStatusNewsletterVersand(final String versandinfoUuid) {

		Optional<Versandauftrag> optVersandinfo = this.versandauftraegeRepo.ofId(new Identifier(versandinfoUuid));

		LOG.info("pollen Versandinfo {}", versandinfoUuid);

		if (optVersandinfo.isEmpty()) {

			return Optional.empty();
		}

		VersandinfoAPIModel apiModel = VersandinfoAPIModel.createFromVersandinfo(optVersandinfo.get());

		return Optional.of(apiModel);
	}

	@Transactional
	public Versandauftrag versandauftragSpeichern(final Versandauftrag versandinformation) {

		if (versandinformation.identifier() == null) {

			return this.versandauftraegeRepo.addVersandinformation(versandinformation);
		}

		return this.versandauftraegeRepo.updateVersandinformation(versandinformation);
	}

	@Deprecated
	public void handleDomainEvent(@Observes final NewsletterversandProgress event) {

		Optional<Versandauftrag> optInfo = this.versandauftraegeRepo.ofId(new Identifier(event.uuid()));

		if (optInfo.isEmpty()) {

			return;
		}

		Versandauftrag versandinformation = optInfo.get()
			.withAnzahlAktuellVersendet(event.aktuellVersendet())
			.withAnzahlEmpaenger(event.anzahlEmpfaenger())
			.withVersandBegonnenAm(event.versandBegonnenAm());

		this.versandauftragSpeichern(versandinformation);
	}

	@Deprecated
	public void handleDomainEvent(@Observes final NewsletterversandFinished event) {

		Optional<Versandauftrag> optInfo = this.versandauftraegeRepo.ofId(new Identifier(event.uuid()));

		if (optInfo.isEmpty()) {

			return;
		}

		Versandauftrag versandinformation = optInfo.get()
			.withVersandBeendetAm(event.versandBeendetAm())
			.withFehlermeldung(event.message());

		this.versandauftragSpeichern(versandinformation);
	}

}
