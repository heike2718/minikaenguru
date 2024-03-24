// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.wettbewerbe;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mkbiza_api.domain.auth.MkBiZaAuthConfig;
import de.egladil.web.mkbiza_api.infrastructure.restclient.MkGatewayRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

/**
 * WettbewerbService
 */
@ApplicationScoped
public class WettbewerbService {

	private static final Logger LOGGER = LoggerFactory.getLogger(WettbewerbService.class);

	@Inject
	MkBiZaAuthConfig authConfig;

	@Inject
	@RestClient
	MkGatewayRestClient mkGaetwayRestClient;

	public List<Integer> loadWettbewerbsjahre() {

		Response response = mkGaetwayRestClient.loadWettbewerbsjahre(authConfig.client(),
			new String(Base64.getEncoder().encode(authConfig.header().getBytes())));

		Integer[] result = response.readEntity(new GenericType<Integer[]>() {
		});

		LOGGER.info("Anzahl beendete Wettbewerbe={}", result.length);

		return Arrays.asList(result);
	}

}
