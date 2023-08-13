// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.api.VersandinfoAPIModel;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandFinished;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandProgress;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.VersandinformationenHibernateRepository;

/**
 * VersandinfoService
 */
@ApplicationScoped
public class VersandinfoService {

	private static final Logger LOG = LoggerFactory.getLogger(VersandinfoService.class);

	@Inject
	VersandinformationenRepository versandinfoRepo;

	public static VersandinfoService createForTest(final VersandinformationenRepository repo) {

		VersandinfoService result = new VersandinfoService();
		result.versandinfoRepo = repo;
		return result;
	}

	public static VersandinfoService createForIntegrationTest(final EntityManager entityManager) {

		VersandinfoService result = new VersandinfoService();
		result.versandinfoRepo = VersandinformationenHibernateRepository.createForTest(entityManager);
		return result;
	}

	public List<Versandinformation> getVersandinformationenZuNewsletter(final Identifier newsletterID) {

		if (newsletterID == null) {

			throw new IllegalArgumentException("newsletterID darf nicht null sein.");
		}

		return versandinfoRepo.findForNewsletter(newsletterID);
	}

	/**
	 * Gibt die Versandinfo mit der UUID zurück.
	 * 
	 * @param  versandinfoUuid
	 *                         String
	 * @return                 Optional
	 */
	public Optional<VersandinfoAPIModel> getStatusNewsletterVersand(final String versandinfoUuid) {

		Optional<Versandinformation> optVersandinfo = this.versandinfoRepo.ofId(new Identifier(versandinfoUuid));

		LOG.info("pollen Versandinfo {}", versandinfoUuid);

		if (optVersandinfo.isEmpty()) {

			return Optional.empty();
		}

		VersandinfoAPIModel apiModel = VersandinfoAPIModel.createFromVersandinfo(optVersandinfo.get());

		return Optional.of(apiModel);
	}

	@Transactional
	public Versandinformation versandinformationSpeichern(final Versandinformation versandinformation) {

		if (versandinformation.identifier() == null) {

			return this.versandinfoRepo.addVersandinformation(versandinformation);
		}

		return this.versandinfoRepo.updateVersandinformation(versandinformation);
	}

	public void handleDomainEvent(@Observes final NewsletterversandProgress event) {

		Optional<Versandinformation> optInfo = this.versandinfoRepo.ofId(new Identifier(event.uuid()));

		if (optInfo.isEmpty()) {

			return;
		}

		Versandinformation versandinformation = optInfo.get()
			.withAnzahlAktuellVersendet(event.aktuellVersendet())
			.withAnzahlEmpaenger(event.anzahlEmpfaenger())
			.withVersandBegonnenAm(event.versandBegonnenAm());

		this.versandinformationSpeichern(versandinformation);
	}

	public void handleDomainEvent(@Observes final NewsletterversandFinished event) {

		Optional<Versandinformation> optInfo = this.versandinfoRepo.ofId(new Identifier(event.uuid()));

		if (optInfo.isEmpty()) {

			return;
		}

		Versandinformation versandinformation = optInfo.get()
			.withVersandBeendetAm(event.versandBeendetAm())
			.withFehlermeldung(event.message());

		this.versandinformationSpeichern(versandinformation);
	}

}
