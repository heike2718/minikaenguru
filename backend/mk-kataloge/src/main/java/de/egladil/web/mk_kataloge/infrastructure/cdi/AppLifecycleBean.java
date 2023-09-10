// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.cdi;

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

	private static final Logger LOGGER = LoggerFactory.getLogger(AppLifecycleBean.class);

	@ConfigProperty(name = "quarkus.http.root-path")
	String rootPath;

	@ConfigProperty(name = "quarkus.http.port")
	String port;

	void onStartup(@Observes final StartupEvent ev) {

		LOGGER.info("mk-kataloge is starting...");

		LOGGER.info(" ===========>  quarkus.http.root-path={}", rootPath);
		LOGGER.info(" ===========>  quarkus.http.port={}", port);

	}

}
