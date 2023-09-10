// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.cdi;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

/**
 * AppLifecycleBean
 */
@ApplicationScoped
public class AppLifecycleBean {

	private static final String NAME_DOWNLOAD_DIR = "unterlagen";

	private static final String NAME_UPLOAD_DIR = "upload";

	private static final Logger LOGGER = LoggerFactory.getLogger(AppLifecycleBean.class);

	@ConfigProperty(name = "path.external.files")
	String pathExternalFiles;

	@ConfigProperty(name = "quarkus.http.cors.origins", defaultValue = "")
	String corsAllowedOrigins;

	@ConfigProperty(name = "quarkus.http.root-path")
	String rootPath;

	@ConfigProperty(name = "quarkus.http.port")
	String port;

	@ConfigProperty(name = "clamav.host")
	String clamAVHost;

	void onStartup(@Observes final StartupEvent ev) {

		LOGGER.info("mk-gateway is starting...");
		LOGGER.info(" ===========>  the download dir is {}", getPathDownloadDir());
		LOGGER.info(" ===========>  the upload dir is {}", getPathUploadDir());
		LOGGER.info(" ===========>  quarkus.http.cors.origins={}", corsAllowedOrigins);
		LOGGER.info(" ===========>  quarkus.http.root-path={}", rootPath);
		LOGGER.info(" ===========>  quarkus.http.port={}", port);
		LOGGER.info(" ===========>  clamav.host={}", clamAVHost);

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

	private String getPathUploadDir() {

		String result = pathExternalFiles + File.separator + NAME_UPLOAD_DIR;

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

}
