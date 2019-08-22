//=====================================================
// Projekt: mkadmin-server
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.mkadmin_server;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.auth.LoginConfig;

/**
 * MkadminServerApplication
 *
 */
@LoginConfig(authMethod = "MP-JWT")
@ApplicationPath("/mkadmin-api")
public class MkadminServerApplication extends Application {

	/**
	 *
	 */
	public MkadminServerApplication() {
		System.out.println("==== MkadminServerApplication created ====");
	}

}
