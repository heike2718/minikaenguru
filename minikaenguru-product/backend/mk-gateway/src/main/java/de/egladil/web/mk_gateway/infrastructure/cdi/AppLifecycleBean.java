// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.cdi;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ConfigUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

/**
 * AppLifecycleBean
 */
@ApplicationScoped
public class AppLifecycleBean {

	private static final String NAME_DOWNLOAD_DIR = "unterlagen";

	private static final Logger LOGGER = LoggerFactory.getLogger(AppLifecycleBean.class);

	@ConfigProperty(name = "quarkus.log.min-level")
	String logMinLevel;

	@ConfigProperty(name = "quarkus.log.category.\"org.hibernate.orm\".level")
	String orgHibernateOrmLogLevel;

	@ConfigProperty(name = "path.external.files")
	String pathExternalFiles;

	@ConfigProperty(name = "quarkus.http.body-handler.uploads-directory")
	String quarkusUploadsDir;

	@ConfigProperty(name = "quarkus.http.cors.origins", defaultValue = "")
	String corsAllowedOrigins;

	@ConfigProperty(name = "quarkus.http.root-path")
	String rootPath;

	@ConfigProperty(name = "quarkus.http.port")
	String port;

	@ConfigProperty(name = "quarkus.datasource.wettbewerb.jdbc.url")
	String wettbewerbJdbcUrl;

	@ConfigProperty(name = "quarkus.datasource.kataloge.jdbc.url")
	String katalogeJdbcUrl;

	@ConfigProperty(name = "quarkus.rest-client.filescanner.url")
	String filescannerUrl;

	@ConfigProperty(name = "quarkus.rest-client.authprovider.url")
	String authproviderUrl;

	@ConfigProperty(name = "auth-app.url")
	String authAppUrl;

	@ConfigProperty(name = "quarkus.rest-client.mk-kataloge.url")
	String katalogeUrl;

	@ConfigProperty(name = "newsletterversand.cron.expr")
	String newsletterversandCronExpression;

	@ConfigProperty(name = "quarkus.application.version")
	String version;

	void onStartup(@Observes
	final StartupEvent ev) {

		LOGGER.info(" ===========> Version {} of the application is starting with profiles {}", version,
			StringUtils.join(ConfigUtils.getProfiles()));

		LOGGER.info(" ===========>  logMinLevel={}", logMinLevel);
		LOGGER.info(" ===========>  orgHibernateOrmLogLevel={}", orgHibernateOrmLogLevel);
		LOGGER.info(" ===========>  newsletterversandCron={}", newsletterversandCronExpression);
		LOGGER.info(" ===========>  filescannerUrl={}", filescannerUrl);
		LOGGER.info(" ===========>  authproviderUrl={}", authproviderUrl);
		LOGGER.info(" ===========>  authAppUrl={}", authAppUrl);
		LOGGER.info(" ===========>  katalogeUrl={}", katalogeUrl);
		LOGGER.info(" ===========>  wettbewerbJdbcUrl={}", wettbewerbJdbcUrl);
		LOGGER.info(" ===========>  katalogeJdbcUrl={}", katalogeJdbcUrl);
		LOGGER.info(" ===========>  the download dir is {}", getPathDownloadDir());
		LOGGER.info(" ===========>  the upload dir is {}", quarkusUploadsDir);
		LOGGER.info(" ===========>  quarkus.http.cors.origins={}", corsAllowedOrigins);
		LOGGER.info(" ===========>  quarkus.http.root-path={}", rootPath);
		LOGGER.info(" ===========>  quarkus.http.port={}", port);

	}

	private String getPathDownloadDir() {

		String result = pathExternalFiles + File.separator + NAME_DOWNLOAD_DIR;

		File uploadDir = new File(result);

		if (!uploadDir.exists()) {

			try {

				FileUtils.forceMkdir(uploadDir);
			} catch (IOException e) {

				LOGGER.error("Verzeichnis {} konnte nicht ereugt werden: {}", e.getMessage());
			}
		}

		return result;
	}

	// private String getPathUploadDir() {
	//
	// String result = pathExternalFiles + File.separator + NAME_UPLOAD_DIR;
	//
	// File uploadDir = new File(result);
	//
	// if (!uploadDir.exists()) {
	//
	// try {
	//
	// FileUtils.forceMkdir(uploadDir);
	// } catch (IOException e) {
	//
	// LOGGER.error("Verzeichnis {} konnte nicht ereugt werden: {}", e.getMessage());
	// }
	// }
	//
	// return result;
	// }

}
