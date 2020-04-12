// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.clientauth;

/**
 * IClientAccessTokenService
 */
public interface IClientAccessTokenService {

	/**
	 * Holt ein clientAccessToken vom authprovider
	 *
	 * @param  mkvAppClientId
	 * @param  mkvAppClientSecret
	 * @return              String das accessToken oder null!
	 */
	String orderAccessToken(final String clientId, final String clientSecret);

}
