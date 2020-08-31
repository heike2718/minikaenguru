// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.permissions;

/**
 * TokenizablePath
 */
public interface TokenizablePath {

	/**
	 * @return String value of the path attribute
	 */
	String[] tokens();

	/**
	 * @return String value of the method attribute
	 */
	String path();

	/**
	 * Disposes the tail of the path starting from the first '*'
	 *
	 * @return String
	 */
	String nonWildcardPrefix();

}
