// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.profiles;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

/**
 * FullDatabaseTestProfile
 */
public class FullDatabaseTestProfile implements QuarkusTestProfile {

	@Override
	public Map<String, String> getConfigOverrides() {
		Map<String, String> configOverrides = new HashMap<>();
		configOverrides.put("quarkus.datasource.wettbewerb.jdbc.url", "jdbc:mariadb://172.23.0.2:3306/mk_wettbewerb");
		configOverrides.put("quarkus.datasource.kataloge.jdbc.url", "jdbc:mariadb://172.22.0.2:3306/mk_kataloge");
		configOverrides.put("quarkus.http.port", "9512");
		configOverrides.put("quarkus.log.file.enable", "false");
		configOverrides.put("quarkus.http.access-log.log-to-file", "false");
		configOverrides.put("newsletterversand.cron.expr", "0 0/2 * 1/1 * ? *");
		configOverrides.put("path.external.files", "/home/heike/git/testdaten/minikaenguru");

		System.out.println(">>>> configOverrides aus  FullDatabaseTestProfile gelesen <<<<");
		return configOverrides;
	}

}
