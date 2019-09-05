//=====================================================
// Project: quarkus-extconfig
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mkadmin_server.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

/**
 * SecretPropertiesSource is connected with a properties file in the file system.<br>
 * <br>
 * The location of this file must be configured by setting the property 'secret.config.path'. This property is resolved
 * by the MP configuration mechanism.<br>
 * <br>
 * The purpose of redirecting to an external file is to avoid secret properties being checked into version control.
 */
@ApplicationScoped
public class SecretPropertiesSource {

	private Properties properties;

	@Inject
	Logger log;

	@ConfigProperty(name = "secret.config.path")
	String pathSecretProperties;

	public Properties getProperties() {

		if (this.properties == null) {
			this.properties = loadProperties();
		}

		return this.properties;
	}

	private Properties loadProperties() {
		Properties result = new Properties();

		try (InputStream in = new FileInputStream(new File(pathSecretProperties))) {

			result.load(in);

		} catch (IOException e) {
			log.error("error reading external configuration: {}", e.getMessage(), e);

		}

		return result;
	}

}
