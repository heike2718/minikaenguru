// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * MkWettbewerbAdminApp
 */
@ApplicationPath("/mk-wettbewerb-admin")
public class MkWettbewerbAdminApp extends Application {

	public static final String CLIENT_COOKIE_PREFIX = "mkadmin";

	public static final String STAGE_DEV = "dev";

	public static final String UUID_HEADER_NAME = "X-UUID";

}
