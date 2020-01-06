// =====================================================
// Project: mkv-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server;

/**
 * MkvServerApp<br>
 * <br>
 * Aus irgendeinem Grund wird die content-root im reverse proxy nicht gefunden, wenn Klasse von Application erbt und
 * mit @ApplicationPath("/mkv-server") annotiert ist. Daher analog zu quarkus-hello keine Application-Klasse mehr.
 */
public class MkvServerApp {

	public static final String CLIENT_COOKIE_PREFIX = "MKV";

	public static final String STAGE_DEV = "dev";
}
