// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * KatalogAPIApp
 */
@ApplicationPath("/mk-kataloge-api")
public class KatalogAPIApp extends Application {

	public static final String CLIENT_COOKIE_PREFIX = "KAT";

	public static final String STAGE_DEV = "dev";

}