// =====================================================
// Project: mja-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.infrastructure.cdi;

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

	@ConfigProperty(name = "quarkus.rest-client.mjaapi.url")
	String mjaapiRestClientUrl;

	@ConfigProperty(name = "quarkus.rest-client.mkgateway.url")
	String mkgatewayRestClientUrl;

	@ConfigProperty(name = "quarkus.http.root-path")
	String quarkusRootPath;

	@ConfigProperty(name = "quarkus.http.port")
	String port;

	@ConfigProperty(name = "quarkus.http.cors.origins")
	String corsAllowedOrigins;

	@ConfigProperty(name = "quarkus.application.version")
	String version;

	void onStartup(@Observes final StartupEvent ev) {

		LOGGER.info(" ===========> Version {} of the application is starting with profiles {}", version,
			StringUtils.join(ConfigUtils.getProfiles()));

		LOGGER.info(" ===========> quarkus.http.cors.origins={}", corsAllowedOrigins);
		LOGGER.info(" ===========> mjaapiRestClientUrl={}", mjaapiRestClientUrl);
		LOGGER.info(" ===========> mkgatewayRestClientUrl={}", mkgatewayRestClientUrl);
		LOGGER.info(" ===========> quarkusRootPath={}", quarkusRootPath);
		LOGGER.info(" ===========> port={}", port);
	}

}
