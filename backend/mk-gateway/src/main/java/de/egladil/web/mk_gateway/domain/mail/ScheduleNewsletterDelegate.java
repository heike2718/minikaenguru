// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.api.NewsletterVersandauftrag;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

/**
 * ScheduleNewsletterDelegate
 *
 * @deprecated wird durch NewsletterAuftraegeService übernommen.
 */
@ApplicationScoped
@Deprecated
public class ScheduleNewsletterDelegate {

	@Inject
	NewsletterAuftraegeService versandinfoService;

	public static ScheduleNewsletterDelegate createForTest(final NewsletterAuftraegeService versandinfoService) {

		ScheduleNewsletterDelegate result = new ScheduleNewsletterDelegate();
		result.versandinfoService = versandinfoService;
		return result;
	}

	public static ScheduleNewsletterDelegate createForIntegrationTest(final EntityManager etityManager) {

		ScheduleNewsletterDelegate result = new ScheduleNewsletterDelegate();
		result.versandinfoService = NewsletterAuftraegeService.createForIntegrationTest(etityManager);
		return result;
	}

	@Transactional
	public Versandauftrag scheduleMailversand(final NewsletterVersandauftrag auftrag) {

		Identifier newsletterID = new Identifier(auftrag.newsletterID());
		List<Versandauftrag> vorhandene = versandinfoService
			.getVersandinformationenZuNewsletter(newsletterID);

		List<Versandauftrag> nichtZumTestGesendete = vorhandene.stream().filter(v -> Empfaengertyp.TEST != v.empfaengertyp())
			.collect(Collectors.toList());

		Optional<Versandauftrag> optDiejenige = nichtZumTestGesendete.stream()
			.filter(v -> auftrag.emfaengertyp() == v.empfaengertyp())
			.findFirst();

		if (optDiejenige.isPresent()) {

			return optDiejenige.get();

		}

		if (Empfaengertyp.ALLE == auftrag.emfaengertyp() && !nichtZumTestGesendete.isEmpty()) {

			return nichtZumTestGesendete.get(0);
		}

		if (auftrag.emfaengertyp() == Empfaengertyp.LEHRER || auftrag.emfaengertyp() == Empfaengertyp.PRIVATVERANSTALTER) {

			Optional<Versandauftrag> optAnAlleGesendete = nichtZumTestGesendete.stream()
				.filter(v -> Empfaengertyp.ALLE == v.empfaengertyp())
				.findFirst();

			if (optAnAlleGesendete.isPresent()) {

				return optAnAlleGesendete.get();

			}

		}

		Versandauftrag versandinformation = new Versandauftrag()
			.withEmpfaengertyp(auftrag.emfaengertyp())
			.withNewsletterID(newsletterID);

		if (auftrag.emfaengertyp() == Empfaengertyp.TEST) {

			Optional<Versandauftrag> optTest = vorhandene.stream()
				.filter(v -> auftrag.emfaengertyp() == v.empfaengertyp())
				.findFirst();

			if (optTest.isPresent()) {

				versandinformation = optTest.get();
			}
		}

		return versandinfoService.versandauftragSpeichern(versandinformation);
	}

}
