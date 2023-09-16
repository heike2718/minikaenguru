// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.api.NewsletterVersandauftrag;

/**
 * ScheduleNewsletterDelegate
 */
@ApplicationScoped
public class ScheduleNewsletterDelegate {

	@Inject
	VersandinfoService versandinfoService;

	public static ScheduleNewsletterDelegate createForTest(final VersandinfoService versandinfoService) {

		ScheduleNewsletterDelegate result = new ScheduleNewsletterDelegate();
		result.versandinfoService = versandinfoService;
		return result;
	}

	public static ScheduleNewsletterDelegate createForIntegrationTest(final EntityManager etityManager) {

		ScheduleNewsletterDelegate result = new ScheduleNewsletterDelegate();
		result.versandinfoService = VersandinfoService.createForIntegrationTest(etityManager);
		return result;
	}

	@Transactional
	public Versandinformation scheduleMailversand(final NewsletterVersandauftrag auftrag) {

		Identifier newsletterID = new Identifier(auftrag.newsletterID());
		List<Versandinformation> vorhandene = versandinfoService
			.getVersandinformationenZuNewsletter(newsletterID);

		List<Versandinformation> nichtZumTestGesendete = vorhandene.stream().filter(v -> Empfaengertyp.TEST != v.empfaengertyp())
			.collect(Collectors.toList());

		Optional<Versandinformation> optDiejenige = nichtZumTestGesendete.stream()
			.filter(v -> auftrag.emfaengertyp() == v.empfaengertyp())
			.findFirst();

		if (optDiejenige.isPresent()) {

			return optDiejenige.get();

		}

		if (Empfaengertyp.ALLE == auftrag.emfaengertyp() && !nichtZumTestGesendete.isEmpty()) {

			return nichtZumTestGesendete.get(0);
		}

		if (auftrag.emfaengertyp() == Empfaengertyp.LEHRER || auftrag.emfaengertyp() == Empfaengertyp.PRIVATVERANSTALTER) {

			Optional<Versandinformation> optAnAlleGesendete = nichtZumTestGesendete.stream()
				.filter(v -> Empfaengertyp.ALLE == v.empfaengertyp())
				.findFirst();

			if (optAnAlleGesendete.isPresent()) {

				return optAnAlleGesendete.get();

			}

		}

		Versandinformation versandinformation = new Versandinformation()
			.withEmpfaengertyp(auftrag.emfaengertyp())
			.withNewsletterID(newsletterID);

		if (auftrag.emfaengertyp() == Empfaengertyp.TEST) {

			Optional<Versandinformation> optTest = vorhandene.stream()
				.filter(v -> auftrag.emfaengertyp() == v.empfaengertyp())
				.findFirst();

			if (optTest.isPresent()) {

				versandinformation = optTest.get();
			}
		}

		return versandinfoService.versandinformationSpeichern(versandinformation);
	}

}
