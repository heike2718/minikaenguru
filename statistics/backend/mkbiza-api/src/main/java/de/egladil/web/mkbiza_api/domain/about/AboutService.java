// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.about;

import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * AboutService
 */
@ApplicationScoped
public class AboutService {

	@ConfigProperty(name = "quarkus.application.version")
	String version;

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	/**
	 * Gibt die Infos für die About-URL zurück.
	 *
	 * @return AboutDto
	 */
	public AboutDto getAboutInfo() {

		AboutDto result = new AboutDto();

		String[] urls = new String[] { applicationMessages.getString("about.mathe-jung-alt.url"),
			applicationMessages.getString("about.mja-app.url"),
			applicationMessages.getString("about.minikaenguru.url") };

		result.setBeschreibung(applicationMessages.getString("about.description.de"));
		result.setDescription(applicationMessages.getString("about.description.en"));
		result.setKeywords(applicationMessages.getString("about.keywords.en"));
		result.setSchluesselwoerter(applicationMessages.getString("about.keywords.de"));
		result.setTitle(applicationMessages.getString("about.title"));
		result.setUrls(urls);
		result.setVersion(version);
		return result;

	}

}
