// =====================================================
// Project: mkadmin-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkadmin_server;

/**
 * MkadminServerApp<br>
 * <br>
 * Aus irgendeinem Grund wird die content-root im reverse proxy nicht gefunden, wenn Klasse von Application erbt und
 * mit @ApplicationPath("/mkadmin-server") annotiert ist. Daher analog zu quarkus-hello keine Application-Klasse mehr.
 */
public class MkadminServerApp {

	public static final String CLIENT_COOKIE_PREFIX = "MKA";
}
