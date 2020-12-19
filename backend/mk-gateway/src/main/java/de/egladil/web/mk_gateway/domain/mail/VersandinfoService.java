// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandFinished;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandProgress;

/**
 * VersandinfoService
 */
@ApplicationScoped
public class VersandinfoService {

	@Inject
	VersandinformationenRepository versandinfoRepo;

	public static VersandinfoService createForTest(final VersandinformationenRepository repo) {

		VersandinfoService result = new VersandinfoService();
		result.versandinfoRepo = repo;
		return result;
	}

	public List<Versandinformation> getVersandinformationenZuNewsletter(final Identifier newsletterID) {

		if (newsletterID == null) {

			throw new IllegalArgumentException("newsletterID darf nicht null sein.");
		}

		return versandinfoRepo.findForNewsletter(newsletterID);
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
