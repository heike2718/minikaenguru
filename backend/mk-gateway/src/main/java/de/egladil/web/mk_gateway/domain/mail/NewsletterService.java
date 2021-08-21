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
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.api.NewsletterAPIModel;
import de.egladil.web.mk_gateway.domain.mail.api.NewsletterVersandauftrag;
import de.egladil.web.mk_gateway.domain.mail.api.VersandinfoAPIModel;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandFailed;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandFinished;
import de.egladil.web.mk_gateway.domain.mail.events.NewsletterversandProgress;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterMailinfoService;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.NewsletterHibernateRepository;

/**
 * NewsletterService
 */
@RequestScoped
public class NewsletterService {

	private static final Logger LOGGER = LoggerFactory.getLogger(NewsletterService.class);

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
	ConcurrentSendMailDelegate sendMailDelegate;

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
		result.mailService = mailService;
		return result;

	}

	public static NewsletterService createForIntegrationTest(final EntityManager entityManager) {

		NewsletterService result = new NewsletterService();
		result.newsletterRepositiory = NewsletterHibernateRepository.createForIntegrationTest(entityManager);
		result.versandinfoService = VersandinfoService.createForIntegrationTest(entityManager);
		result.scheduleNewsletterDelegate = ScheduleNewsletterDelegate.createForIntegrationTest(entityManager);
		result.veranstalterMailinfoService = VeranstalterMailinfoService.createForIntegrationTest(entityManager);
		result.mailService = AdminMailService.createForTest(result.veranstalterMailinfoService.getMailConfiguration());
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

	/**
	 * Startet den Mailversand asynchron.
	 *
	 * @param  auftrag
	 * @return         ResponsePayload
	 */
	public ResponsePayload scheduleAndStartMailversand(final NewsletterVersandauftrag auftrag) {

		Optional<Newsletter> optNewsletter = this.newsletterRepositiory.ofId(new Identifier(auftrag.newsletterID()));

		if (optNewsletter.isEmpty()) {

			String fehlermeldung = "kein Newsletter mit UUID=" + auftrag.newsletterID() + " vorhanden";

			Versandinformation finished = this.createFinishedVersandinfo(auftrag).withFehlermeldung(fehlermeldung);

			return new ResponsePayload(MessagePayload.error(
				fehlermeldung),
				VersandinfoAPIModel.createFromVersandinfo(finished));
		}

		List<List<String>> mailempfaengerGruppen = this.veranstalterMailinfoService
			.getMailempfaengerGroups(auftrag.emfaengertyp());

		if (mailempfaengerGruppen.isEmpty()) {

			Versandinformation finished = this.createFinishedVersandinfo(auftrag);

			return new ResponsePayload(MessagePayload.warn(
				"Keine Empfängeradressen für " + auftrag.emfaengertyp() + " gefunden."),
				VersandinfoAPIModel.createFromVersandinfo(finished));
		}

		Versandinformation versandinformation = null;

		try {

			versandinformation = this.scheduleNewsletterDelegate.scheduleMailversand(auftrag);
		} catch (Exception e) {

			String message = "Beim Anlegen des Versandauftrags ist ein Fehler aufgetreten: " + e.getMessage();

			LOGGER.error("Exception beim Anlegen des Versandauftrags {}: {}", auftrag, e.getMessage(), e);

			int anzahlEmpfaenger = new Mailempfaengerzaehler().apply(mailempfaengerGruppen);

			versandinformation = this.createFinishedVersandinfo(auftrag).withAnzahlEmpaenger(anzahlEmpfaenger)
				.withFehlermeldung(message);
			return new ResponsePayload(MessagePayload.error(
				message),
				VersandinfoAPIModel.createFromVersandinfo(versandinformation));
		}

		if (versandinformation.bereitsVersendet() && Empfaengertyp.TEST != versandinformation.empfaengertyp()) {

			return new ResponsePayload(
				MessagePayload.warn("Newsletter wurde bereits am " + versandinformation.versandBeendetAm()
					+ " an " + versandinformation.empfaengertyp() + " versendet"),
				VersandinfoAPIModel.createFromVersandinfo(versandinformation));
		}

		Newsletter newsletter = optNewsletter.get();
		NewsletterTask task = new NewsletterTask(this, newsletter, versandinformation, mailempfaengerGruppen);

		sendMailDelegate.mailsVersenden(task, versandinformation);

		return new ResponsePayload(MessagePayload.info(
			"Versand an " + versandinformation.empfaengertyp() + " begonnen "),
			VersandinfoAPIModel.createFromVersandinfo(versandinformation));
	}

	@Transactional
	public void newsletterLoeschen(final Identifier identifier) {

		this.newsletterRepositiory.removeNewsletter(identifier);

	}

	/**
	 * Erzeugt eine transiente beendete Versandiformation bei leerer Empfängerliste.
	 *
	 * @param  versandinformation
	 *                            Versandinformation
	 * @return                    Versandinformation
	 */
	Versandinformation createFinishedVersandinfo(final NewsletterVersandauftrag auftrag) {

		String jetzt = CommonTimeUtils.format(CommonTimeUtils.now());

		Versandinformation result = new Versandinformation().withIdentifier(new Identifier("neu"))
			.withNewsletterID(new Identifier(auftrag.newsletterID())).withAnzahlEmpaenger(0).withAnzahlAktuellVersendet(0)
			.withVersandBegonnenAm(jetzt)
			.withVersandBeendetAm(jetzt);

		return result;

	}
}
