// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

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

	@Transactional(value = TxType.REQUIRES_NEW)
	public Versandinformation scheduleMailversand(final NewsletterVersandauftrag auftrag) {

		Identifier newsletterID = new Identifier(auftrag.newsletterID());
		List<Versandinformation> vorhandene = versandinfoService
			.getVersandinformationenZuNewsletter(newsletterID);

		Optional<Versandinformation> optDiejenige = vorhandene.stream().filter(v -> auftrag.emfaengertyp() == v.empfaengertyp())
			.findFirst();

		if (optDiejenige.isPresent()) {

			return optDiejenige.get();

		}

		Versandinformation versandinformation = new Versandinformation()
			.withEmpfaengertyp(auftrag.emfaengertyp())
			.withNewsletterID(newsletterID);

		return versandinfoService.versandinformationSpeichern(versandinformation);
	}

}
