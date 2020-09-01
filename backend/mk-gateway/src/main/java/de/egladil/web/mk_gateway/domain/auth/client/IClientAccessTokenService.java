// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.client;

/**
 * IClientAccessTokenService
 */
public interface IClientAccessTokenService {

	/**
	 * Holt ein clientAccessToken vom authprovider
	 *
	 * @param  clientId
	 * @param  clientSecret
	 * @return              String das accessToken oder null!
	 */
	String orderAccessToken(final String clientId, final String clientSecret);

}
