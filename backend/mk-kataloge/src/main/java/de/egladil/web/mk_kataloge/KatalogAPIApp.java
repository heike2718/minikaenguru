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
@ApplicationPath("/mk-kataloge")
public class KatalogAPIApp extends Application {

	public static final String CLIENT_COOKIE_PREFIX = "KAT";

	public static final String STAGE_DEV = "dev";

	public static final String UUID_HEADER_NAME = "X-UUID";

	public static final String SECRET_HEADER_NAME = "X-SECRET";

}
