// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.s2s;

import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * ClientAuthService
 */
@ApplicationScoped
public class ClientAuthService {

	private static final String AUTH_METHOD_PREFIX = "Basic ";

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientAuthService.class);

	@Inject
	MkBiZaAuthConfig authConfig;

	public Pair<String, Boolean> authorize(final String authorizationHeaderValue) {

		String headerValue = StringUtils.remove(authorizationHeaderValue, AUTH_METHOD_PREFIX);
		LOGGER.debug("expect header={}, headerValue={}", StringUtils.abbreviate(authConfig.header(), 20),
			StringUtils.abbreviate(headerValue, 20));

		try {

			String decodedHeader = new String(Base64.getDecoder().decode(headerValue.getBytes()));
			boolean authenticated = authConfig.header().equals(decodedHeader);
			String clientId = extractClient(decodedHeader);

			if (!authenticated) {

				LOGGER.error("clientId={}, actual header decoded={}, authConfic.header={}", clientId,
					StringUtils.abbreviate(decodedHeader, 20), StringUtils.abbreviate(authConfig.header(), 20));
			}
			return Pair.of(clientId, authenticated);
		} catch (IllegalArgumentException e) {

			LOGGER.error("Base64-Dekodierung des Authorization-Headers fehlgeschlagen: {}", e.getMessage());
			return Pair.of("", false);
		}
	}

	String extractClient(final String decodedHeader) {

		String[] token = StringUtils.split(decodedHeader, ":");

		if (token.length == 2) {

			return token[0];
		}

		return "client laesst sich nicht ermitteln. Trenner : fehlt oder ist zu oft vorhanden";
	}
}
