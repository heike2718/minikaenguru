// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * MkGatewayApp
 */
@ApplicationPath("/mk-gateway")
public class MkGatewayApp extends Application {

	public static final String CLIENT_COOKIE_PREFIX = "mk";

	public static final String STAGE_DEV = "dev";

	public static final String UUID_HEADER_NAME = "X-UUID";

	public static final String SECRET_HEADER_NAME = "X-SECRET";

}
