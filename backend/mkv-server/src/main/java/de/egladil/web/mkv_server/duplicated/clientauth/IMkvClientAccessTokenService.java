// =====================================================
// Project: mkv-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server.duplicated.clientauth;

/**
 * IMkvClientAccessTokenService nach Fix von https://github.com/quarkusio/quarkus/issues/5015 ersetzen durch
 * IClientAccessTokenService in mk-commons
 */
@Deprecated(forRemoval = true)
public interface IMkvClientAccessTokenService {

	/**
	 * Holt ein clientAccessToken vom authprovider
	 *
	 * @return String das accessToken oder null!
	 */
	String orderAccessToken();

}
