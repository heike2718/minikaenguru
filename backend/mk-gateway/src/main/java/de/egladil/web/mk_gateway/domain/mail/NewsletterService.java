// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

/**
 * NewsletterService
 */
@RequestScoped
public class NewsletterService {

	private static final Logger LOGGER = LoggerFactory.getLogger(NewsletterService.class);

	@Inject
	NewsletterRepository newsletterRepositiory;

	@Inject
	NewsletterAuftraegeService versandinfoService;

	@Inject
	ScheduleNewsletterDelegate scheduleNewsletterDelegate;

	@Inject
	VeranstalterMailinfoService veranstalterMailinfoService;

	@Inject
	AdminMailService mailService;

	@Inject
	Event<NewsletterversandFailed> versandFailedEvent;

	@Inject
	Event<NewsletterversandFinished> versandFinished;

	@Inject
	Event<NewsletterversandProgress> versandProgress;

	public static NewsletterService createForTest(final NewsletterRepository newsletterRepository, final NewsletterAuftraegeService versandinfoService, final VeranstalterMailinfoService veranstalterMailinfoService, final AdminMailService mailService) {

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
		result.versandinfoService = NewsletterAuftraegeService.createForIntegrationTest(entityManager);
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

			addTheVersandinfosNewsletter(nl);
			result.add(NewsletterAPIModel.createFromNewsletter(nl));

		});

		return result;
	}

	// FIXME: keine Ahnung, was das soll
	private void addTheVersandinfosNewsletter(final Newsletter newsletter) {

		List<Versandauftrag> versandinfos = versandinfoService.getVersandinformationenZuNewsletter(newsletter.identifier());

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
			this.addTheVersandinfosNewsletter(persistierter);
		}

		return NewsletterAPIModel.createFromNewsletter(persistierter);
	}

	/**
	 * Erzeugt einen neuen Versandauftrag mit zugehörigen Auslieferungen.
	 *
	 * @param  auftrag
	 * @return         ResponsePayload
	 */
	public ResponsePayload createVersandauftrag(final NewsletterVersandauftrag auftrag) {

		Optional<Newsletter> optNewsletter = this.newsletterRepositiory.ofId(new Identifier(auftrag.newsletterID()));

		if (optNewsletter.isEmpty()) {

			String fehlermeldung = "kein Newsletter mit UUID=" + auftrag.newsletterID() + " vorhanden";

			Versandauftrag finished = this.createFinishedVersandinfo(auftrag).withFehlermeldung(fehlermeldung);

			return new ResponsePayload(MessagePayload.error(
				fehlermeldung),
				VersandinfoAPIModel.createFromVersandinfo(finished));
		}

		List<List<String>> mailempfaengerGruppen = this.veranstalterMailinfoService
			.getMailempfaengerGroups(auftrag.emfaengertyp());

		if (mailempfaengerGruppen.isEmpty()) {

			Versandauftrag finished = this.createFinishedVersandinfo(auftrag);

			return new ResponsePayload(MessagePayload.warn(
				"Keine Empfängeradressen für " + auftrag.emfaengertyp() + " gefunden."),
				VersandinfoAPIModel.createFromVersandinfo(finished));
		}

		Versandauftrag versandinformation = null;

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

		// sendMailDelegate.mailsVersenden(task, versandinformation);

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
	 *                            Versandauftrag
	 * @return                    Versandauftrag
	 */
	Versandauftrag createFinishedVersandinfo(final NewsletterVersandauftrag auftrag) {

		String jetzt = CommonTimeUtils.format(CommonTimeUtils.now());

		Versandauftrag result = new Versandauftrag().withIdentifier(new Identifier("neu"))
			.withNewsletterID(new Identifier(auftrag.newsletterID())).withAnzahlEmpaenger(0).withAnzahlAktuellVersendet(0)
			.withVersandBegonnenAm(jetzt)
			.withVersandBeendetAm(jetzt);

		return result;

	}
}
