// =====================================================
// Project: mkadmin-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkadmin_server;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * MkadminServerApp
 */
@ApplicationPath("/mkadmin-server")
public class MkadminServerApp extends Application {

	public static final String CLIENT_COOKIE_PREFIX = "MKA";
}
