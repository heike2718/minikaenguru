// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.cdi;

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

	private static final Logger LOGGER = LoggerFactory.getLogger(AppLifecycleBean.class);

	@ConfigProperty(name = "quarkus.http.root-path")
	String rootPath;

	@ConfigProperty(name = "quarkus.http.port")
	String port;

	@ConfigProperty(name = "quarkus.application.version")
	String version;

	void onStartup(@Observes final StartupEvent ev) {

		LOGGER.info(" ===========> Version {} of the application is starting with profiles {}", version,
			StringUtils.join(ConfigUtils.getProfiles()));

		LOGGER.info(" ===========>  quarkus.http.root-path={}", rootPath);
		LOGGER.info(" ===========>  quarkus.http.port={}", port);

	}

}
