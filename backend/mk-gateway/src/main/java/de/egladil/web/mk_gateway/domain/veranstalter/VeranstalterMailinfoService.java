// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
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
	 * @return               List von Lists, kann auch empty sein.
	 */
	public List<List<String>> getMailempfaengerGroups(final Empfaengertyp empfaengertyp) {

		List<String> alleMailempfaenger = new ArrayList<>();

		if (Empfaengertyp.TEST == empfaengertyp) {

			alleMailempfaenger = Arrays.asList(StringUtils.split(mailConfiguration.getTestempfaenger(), ","));
		} else {

			alleMailempfaenger = veranstalterRepository.findEmailsNewsletterAbonnenten(empfaengertyp);
		}

		LOGGER.info("Mailversand an Empfaengertyp={}, Anzahl Empfaenger={}", empfaengertyp.toString(), alleMailempfaenger.size());

		if (alleMailempfaenger.isEmpty()) {

			LOGGER.warn("keine Mailempfaenger fuer Empfaengertyp={} vorhanden", empfaengertyp);
			return Collections.emptyList();
		}

		List<String> trimmedMailempfaenger = alleMailempfaenger.stream().map(e -> e.trim()).collect(Collectors.toList());

		List<List<String>> groups = group(trimmedMailempfaenger);

		return groups;
	}

	/**
	 * @param  alleMailempfaenger
	 * @return
	 */
	List<List<String>> group(final List<String> alleMailempfaenger) {

		List<List<String>> groups = new ArrayList<>();

		int groupsize = mailConfiguration.groupsize();

		List<String> gruppe = new ArrayList<>();

		for (String email : alleMailempfaenger) {

			gruppe.add(email);

			if (gruppe.size() == groupsize) {

				groups.add(gruppe);
				gruppe = new ArrayList<>();
			}
		}

		if (!gruppe.isEmpty() && gruppe.size() < groupsize) {

			groups.add(gruppe);
		}

		return groups;
	}

	public AdminEmailsConfiguration getMailConfiguration() {

		return mailConfiguration;
	}

}
