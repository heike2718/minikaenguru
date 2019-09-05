//=====================================================
// Project: quarkus-extconfig
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mkadmin_server.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

/**
 * ChangeablePropertiesSource is connected with a properties file in the file system that eventually changes.<br>
 * <br>
 * The location of this file must be configured by setting the property 'changeable.config.path'. This property is
 * resolved by the MP configuration mechanism. <br>
 * <br>
 * Change detection is turned on by default and can be turned off by setting the property changeable.config.scan in
 * application.properties to true. The properties will be reloaded if and only if the underlying file changed
 * (lastModified).
 */
@ApplicationScoped
public class ChangeablePropertiesSource {

	private LocalDateTime lastReadTime = LocalDateTime.now();

	@ConfigProperty(name = "changeable.config.path")
	String pathExternalConfigFile;

	@ConfigProperty(name = "changeable.config.scan", defaultValue = "true")
	String checkChanges;

	@Inject
	Logger log;

	private Properties changeableProperties;

	/**
	 * Returns the property with the given key.
	 *
	 * @param key String
	 * @return String oder null
	 */
	public String getProperty(final String key) {
		return getProperties().getProperty(key);
	}

	private Properties getProperties() {

		boolean extFileChanged = externalPropertiesChanged();
		if (extFileChanged) {
			log.info("properties file null or changed and is going to be loaded");
		}

		if (extFileChanged) {
			changeableProperties = loadExternalProperties();
		}

		return this.changeableProperties;
	}

	private boolean externalPropertiesChanged() {

		if (this.changeableProperties == null) {
			return true;
		}

		if (!checkChanges()) {
			return false;
		}

		File file = new File(pathExternalConfigFile);

		if (file.isFile()) {
			long lastModified = file.lastModified();
			LocalDateTime modDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastModified), ZoneId.systemDefault());
			return modDateTime.isAfter(lastReadTime);
		}

		return false;
	}

	private boolean checkChanges() {
		return Boolean.parseBoolean(checkChanges);
	}

	private Properties loadExternalProperties() {
		Properties result = new Properties();

		try (InputStream in = new FileInputStream(new File(pathExternalConfigFile))) {

			result.load(in);

		} catch (IOException e) {
			log.error("error reading external configuration: {}", e.getMessage(), e);

		}

		lastReadTime = LocalDateTime.now();
		return result;
	}
}
