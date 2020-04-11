// =====================================================
// Project: mkv-api-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_api_gateway;

import javax.ws.rs.ApplicationPath;

/**
 * MkvApiGatewayApp<br>
 * <br>
 * Aus irgendeinem Grund wird die content-root im reverse proxy nicht gefunden, wenn Klasse von Application erbt. Daher analog zu
 * quarkus-hello keine Application-Klasse mehr.
 */
@ApplicationPath("/mkv-api-gateway")
public class MkvApiGatewayApp {

	public static final String CLIENT_COOKIE_PREFIX = "MKV";

	public static final String STAGE_DEV = "dev";
}
