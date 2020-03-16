// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.clientauth;

/**
 * IClientAccessTokenService
 */
public interface IClientAccessTokenService {

	/**
	 * Holt ein clientAccessToken vom authprovider
	 *
	 * @return String das accessToken oder null!
	 */
	String orderAccessToken();

}
