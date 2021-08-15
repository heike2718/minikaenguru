// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.cdi;

import java.io.File;
import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;

/**
 * AppLifecycleBean
 */
@ApplicationScoped
public class AppLifecycleBean {

	private static final String NAME_UPLOAD_DIR = "upload";

	private static final Logger LOGGER = LoggerFactory.getLogger(AppLifecycleBean.class);

	@ConfigProperty(name = "path.external.files")
	String pathExternalFiles;

	void onStartup(@Observes final StartupEvent ev) {

		LOGGER.info(" ===========>  the upload dir is {}", getPathUploadDir());

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
