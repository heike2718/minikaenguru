// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * MkGatewayApp<br>
 * <br>
 * Aus irgendeinem Grund wird die content-root im reverse proxy nicht gefunden, wenn Klasse von Application erbt. Daher analog zu
 * quarkus-hello keine Application-Klasse mehr.
 */
@ApplicationPath("/mk-gateway")
public class MkGatewayApp extends Application {

	public static final String CLIENT_COOKIE_PREFIX = "mkv";

	public static final String STAGE_DEV = "dev";
}
