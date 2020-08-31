// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.permissions;

import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;

/**
 * PathWildcardSum kombiniert die token eines TokenizablePath mit einem gegebenen String-Array der gleichen Länge, indem
 * Wildcards
 * im String-Array durch das token im RestrictedUrlPath an der gleichen Stelle ersetzt wird.
 */
public class PathWildcardSum implements BiFunction<TokenizablePath, String[], String> {

	@Override
	public String apply(final TokenizablePath urlPath, final String[] tokens) {

		if (tokens == null || tokens.length != urlPath.tokens().length) {

			return null;
		}

		String[] urlTokens = urlPath.tokens();
		String[] result = new String[urlTokens.length];

		for (int i = 0; i < urlTokens.length; i++) {

			String token = tokens[i];

			if (token == null) {

				return null;
			}
			token = token.toLowerCase();

			if ("*".equals(token)) {

				result[i] = urlTokens[i].toLowerCase();
			} else {

				result[i] = token;
			}

		}

		return StringUtils.join(result, "/");
	}

}
