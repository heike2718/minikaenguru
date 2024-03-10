// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.AdminMailService;
import de.egladil.web.mk_gateway.domain.newsletterversand.NewsletterVersandauftragService;
import de.egladil.web.mk_gateway.domain.newsletterversand.VersandauftraegeRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterMailinfoService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * NewsletterService
 */
@RequestScoped
public class NewsletterService {

	@Inject
	NewsletterRepository newsletterRepositiory;

	@Inject
	NewsletterVersandauftragService newsletterAuftraegeService;

	@Inject
	VeranstalterMailinfoService veranstalterMailinfoService;

	@Inject
	public AdminMailService mailService;

	@Inject
	VersandauftraegeRepository versandauftraegeRepo;

	/**
	 * @return
	 */
	public List<NewsletterAPIModel> getAllNewsletters() {

		List<Newsletter> newsletters = newsletterRepositiory.loadAll();

		final List<NewsletterAPIModel> result = new ArrayList<>(newsletters.size());

		newsletters.stream().forEach(nl -> {

			result.add(NewsletterAPIModel.createFromNewsletter(nl));

		});

		return result;
	}

	/**
	 * @param  newsletterID
	 * @return
	 */
	public Optional<Newsletter> findNewsletterWithID(final Identifier newsletterID) {

		return this.newsletterRepositiory.ofId(newsletterID);
	}

	@Transactional
	public NewsletterAPIModel newsletterSpeichern(final NewsletterAPIModel model) {

		Newsletter persistierter = null;

		Newsletter newsletter = new Newsletter().withBetreff(model.betreff())
			.withText(model.text());

		if (NewsletterAPIModel.KEINE_UUID.equals(model.uuid())) {

			persistierter = newsletterRepositiory.addNewsletter(newsletter);
		} else {

			persistierter = newsletterRepositiory.updateNewsletter(newsletter.withIdentifier(new Identifier(model.uuid())));
		}

		return NewsletterAPIModel.createFromNewsletter(persistierter);
	}

	@Transactional
	public void newsletterLoeschen(final Identifier identifier) {

		this.newsletterRepositiory.removeNewsletter(identifier);

	}

}
