// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.mail.AdminEmailsConfiguration;
import de.egladil.web.mk_gateway.domain.mail.Empfaengertyp;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.VeranstalterHibernateRepository;

/**
 * VeranstalterMailinfoService
 */
@ApplicationScoped
public class VeranstalterMailinfoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(VeranstalterMailinfoService.class);

	@Inject
	AdminEmailsConfiguration mailConfiguration;

	@Inject
	VeranstalterRepository veranstalterRepository;

	public static VeranstalterMailinfoService createForTest(final VeranstalterRepository veranstalterRepository, final AdminEmailsConfiguration mailConfiguration) {

		VeranstalterMailinfoService result = new VeranstalterMailinfoService();
		result.veranstalterRepository = veranstalterRepository;
		result.mailConfiguration = mailConfiguration;
		return result;

	}

	public static VeranstalterMailinfoService createForIntegrationTest(final EntityManager entityManager) {

		VeranstalterMailinfoService result = new VeranstalterMailinfoService();
		result.veranstalterRepository = VeranstalterHibernateRepository.createForIntegrationTest(entityManager);
		result.mailConfiguration = AdminEmailsConfiguration.createForTest("hdwinkel@egladil.de", 150);
		return result;
	}

	/**
	 * @param  empfaengertyp
	 * @return
	 */
	public List<List<String>> getMailempfaengerGroups(final Empfaengertyp empfaengertyp) {

		List<String> alleMailempfaenger = veranstalterRepository.findEmailsNewsletterAbonnenten(empfaengertyp);

		LOGGER.info("Mailversand an Empfaengertyp={}, Anzahl Empfaenger={}", empfaengertyp.toString(), alleMailempfaenger.size());

		if (alleMailempfaenger.isEmpty()) {

			LOGGER.warn("keine Mailempfaenger fuer Empfaengertyp={} vorhanden", empfaengertyp);
		}

		List<List<String>> groups = new ArrayList<>();

		if (alleMailempfaenger.size() <= mailConfiguration.groupsize()) {

			groups.add(alleMailempfaenger);
		} else {

			int count = 0;
			List<String> gruppe = new ArrayList<>();

			for (String email : alleMailempfaenger) {

				gruppe.add(email);
				count++;

				if (count == mailConfiguration.groupsize()) {

					groups.add(gruppe);
					count = 0;
					gruppe = new ArrayList<>();
				}
			}
		}

		return groups;
	}

	public AdminEmailsConfiguration getMailConfiguration() {

		return mailConfiguration;
	}

}
