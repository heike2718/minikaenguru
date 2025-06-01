//=====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MailAddressFilterTest
 */
public class MailAddressFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailAddressFilter.class);

	/**
	 * Gibt die Differenzmenge empfaenger/bannedEmails zurück, also alle Elementa aus empfaenger, die nicht in
	 * bannedEmails enthalten ist.
	 *
	 * @param empfaenger
	 * @param bannedEmails
	 * @return
	 */
	public List<String> getSetDifference(List<String> empfaenger, List<String> bannedEmails) {
		List<String> notBannedEmpfaenger = new ArrayList<>();
		for (String mailaddress : empfaenger) {
			if (bannedEmails.stream().filter(m -> mailaddress.equals(m)).count() == 0) {
				notBannedEmpfaenger.add(mailaddress);
			} else {
				String logContext = getDomainOrNull(mailaddress);
				if (logContext == null) {
					logContext = StringUtils.abbreviate(mailaddress, 11);
				}
				LOGGER.warn("Empfaenger mit gebannter Mailadresse {} wird vom Versand ausgeschlossen", "..." + logContext);
			}
		}

		return notBannedEmpfaenger;
	}

	private String getDomainOrNull(String mailaddress) {
		String[] split = StringUtils.split(mailaddress, "@");
		if (split.length > 1) {
			return "@" + split[1];
		}
		return null;
	}
}
