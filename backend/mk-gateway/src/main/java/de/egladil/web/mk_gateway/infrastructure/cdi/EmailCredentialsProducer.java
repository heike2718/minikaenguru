// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.cdi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_mailer.EmailServiceCredentials;

/**
 * EmailCredentialsProducer
 */
@Singleton
public class EmailCredentialsProducer {

	private static final Logger LOG = LoggerFactory.getLogger(EmailCredentialsProducer.class);

	private Map<String, String> mailCredentials = new HashMap<>();

	@ConfigProperty(name = "email.credentials.source.file", defaultValue = "false")
	boolean readCredentialsFromFile;

	@ConfigProperty(name = "email.credentials.path")
	String credentialsPath;

	@ConfigProperty(name = "email.host")
	String host;

	@ConfigProperty(name = "email.port")
	int port;

	@ConfigProperty(name = "email.user")
	String user;

	@ConfigProperty(name = "email.password")
	String password;

	public static EmailCredentialsProducer createForTest(final String credentialsPath) {

		EmailCredentialsProducer result = new EmailCredentialsProducer();
		result.readCredentialsFromFile = true;
		result.credentialsPath = credentialsPath;
		return result;
	}

	@Produces
	public EmailServiceCredentials produceEmailServiceCredentials() {

		if (readCredentialsFromFile) {

			if (mailCredentials.isEmpty()) {

				initCredentialsProperties();
			}

			if (!mailCredentials.isEmpty()) {

				return EmailServiceCredentials.createInstance(mailCredentials, "noreply@egladil.de");
			}

		}

		return EmailServiceCredentials.createInstance(host, port, user, password.toCharArray(), "noreply@egladil.de");
	}

	private void initCredentialsProperties() {

		try (FileInputStream fis = new FileInputStream(new File(credentialsPath))) {

			Properties credentialsProperties = new Properties();
			credentialsProperties.load(fis);

			Integer.valueOf(credentialsProperties.getProperty(EmailServiceCredentials.KEY_PORT));

			mailCredentials.put(EmailServiceCredentials.KEY_HOST,
				credentialsProperties.getProperty(EmailServiceCredentials.KEY_HOST));
			mailCredentials.put(EmailServiceCredentials.KEY_PORT,
				credentialsProperties.getProperty(EmailServiceCredentials.KEY_PORT));
			mailCredentials.put(EmailServiceCredentials.KEY_USER,
				credentialsProperties.getProperty(EmailServiceCredentials.KEY_USER));
			mailCredentials.put(EmailServiceCredentials.KEY_PASSWORD,
				credentialsProperties.getProperty(EmailServiceCredentials.KEY_PASSWORD));

		} catch (IOException e) {

			LOG.error(e.getMessage());

		} catch (NumberFormatException e) {

			LOG.error("Konfigurationsfehler in File {}: email.port ist keine Zahl", credentialsPath);
		}
	}

}
