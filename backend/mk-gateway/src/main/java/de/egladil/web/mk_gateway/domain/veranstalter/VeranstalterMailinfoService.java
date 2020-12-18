// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.egladil.web.mk_gateway.domain.mail.AdminEmailsConfiguration;
import de.egladil.web.mk_gateway.domain.mail.Empfaengertyp;

/**
 * VeranstalterMailinfoService
 */
@ApplicationScoped
public class VeranstalterMailinfoService {

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

	/**
	 * @param  empfaengertyp
	 * @return
	 */
	public List<List<String>> getMailempfaengerGroups(final Empfaengertyp empfaengertyp) {

		List<String> alleMailempfaenger = veranstalterRepository.findEmailsNewsletterAbonnenten(empfaengertyp);

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

}
