// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.profiles;

import io.quarkus.test.junit.QuarkusTestProfile;

/**
 * FullDatabaseTestProfile
 */
public class FullDatabaseTestProfile implements QuarkusTestProfile {

	@Override
	public String getConfigProfile() {

		// aus application.properties
		return "full-db-test";
	}

}
