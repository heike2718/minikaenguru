// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.mail.api.NewsletterAPIModel;
import de.egladil.web.mk_gateway.domain.mail.api.NewsletterVersandauftrag;
import de.egladil.web.mk_gateway.domain.mail.api.VersandinfoAPIModel;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandFailed;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandFinished;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandProgress;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterMailinfoService;

/**
 * NewsletterService
 */
@RequestScoped
public class NewsletterService {

	@Inject
	NewsletterRepository newsletterRepositiory;

	@Inject
	VersandinfoService versandinfoService;

	@Inject
	ScheduleNewsletterDelegate scheduleNewsletterDelegate;

	@Inject
	VeranstalterMailinfoService veranstalterMailinfoService;

	@Inject
	AdminMailService mailService;

	@Inject
	AdminEmailsConfiguration mailConfiguration;

	@Inject
	Event<NewsletterversandFailed> versandFailedEvent;

	@Inject
	Event<NewsletterversandFinished> versandFinished;

	@Inject
	Event<NewsletterversandProgress> versandProgress;

	public static NewsletterService createForTest(final NewsletterRepository newsletterRepository, final VersandinfoService versandinfoService, final VeranstalterMailinfoService veranstalterMailinfoService, final AdminMailService mailService) {

		NewsletterService result = new NewsletterService();
		result.newsletterRepositiory = newsletterRepository;
		result.versandinfoService = versandinfoService;
		result.scheduleNewsletterDelegate = ScheduleNewsletterDelegate.createForTest(versandinfoService);
		result.veranstalterMailinfoService = veranstalterMailinfoService;
		result.mailConfiguration = AdminEmailsConfiguration.createForTest("hdwinkel@egladil.de", 3);
		result.mailService = mailService;
		return result;

	}

	NewsletterService withMockedScheduleNewsletterDelegate(final ScheduleNewsletterDelegate scheduleNewsletterDelegate) {

		this.scheduleNewsletterDelegate = scheduleNewsletterDelegate;
		return this;
	}

	/**
	 * @return
	 */
	public List<NewsletterAPIModel> getAllNewsletters() {

		List<Newsletter> newsletters = newsletterRepositiory.loadAll();

		final List<NewsletterAPIModel> result = new ArrayList<>(newsletters.size());

		newsletters.stream().forEach(nl -> {

			addTheVersantinfosNewsletter(nl);
			result.add(NewsletterAPIModel.createFromNewsletter(nl));

		});

		return result;
	}

	private void addTheVersantinfosNewsletter(final Newsletter newsletter) {

		List<Versandinformation> versandinfos = versandinfoService.getVersandinformationenZuNewsletter(newsletter.identifier());

		versandinfos.stream().forEach(i -> newsletter.addIdVersandinformation(i.identifier()));

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
			this.addTheVersantinfosNewsletter(persistierter);
		}

		return NewsletterAPIModel.createFromNewsletter(persistierter);
	}

	public VersandinfoAPIModel scheduleAndStartMailversand(final NewsletterVersandauftrag auftrag) {

		Versandinformation versandinformation = this.scheduleNewsletterDelegate.scheduleMailversand(auftrag);

		if (versandinformation.bereitsVersendet()) {

			return VersandinfoAPIModel.createFromVersandinfo(versandinformation);
		}

		Optional<Newsletter> optNewsletter = this.newsletterRepositiory.ofId(new Identifier(auftrag.newsletterID()));

		if (optNewsletter.isEmpty()) {

			throw new MkGatewayRuntimeException("kein Newsletter mit UUID=" + auftrag.newsletterID() + " vorhanden");
		}

		Newsletter newsletter = optNewsletter.get();

		List<List<String>> mailempfaengerGruppen = this.veranstalterMailinfoService
			.getMailempfaengerGroups(versandinformation.empfaengertyp());

		NewsletterTask task = new NewsletterTask(this, newsletter, versandinformation, mailempfaengerGruppen);

		// Das läuft dann hoffentlich wirklich in einem eigenen Thread.
		ConcurrentSendMailDelegate sendMailDelegate = new ConcurrentSendMailDelegate(versandinformation, versandFinished,
			versandFailedEvent);

		sendMailDelegate.mailsVersenden(task);

		return VersandinfoAPIModel.createFromVersandinfo(versandinformation);
	}
}
