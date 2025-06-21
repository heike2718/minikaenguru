// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.domain.wettbewerbe;

import java.util.Arrays;
import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.minikaenguru_statistik.domain.auth.BaseAuthHeaderUtils;
import de.egladil.web.minikaenguru_statistik.domain.auth.MinikaenguruStatistikAuthConfig;
import de.egladil.web.minikaenguru_statistik.domain.exeptions.MinikaenguruStatistikCommunicationExcepion;
import de.egladil.web.minikaenguru_statistik.infrastructure.restclient.MkGatewayRestClient;
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
	MinikaenguruStatistikAuthConfig authConfig;

	@Inject
	@RestClient
	MkGatewayRestClient mkGatewayRestClient;

	/**
	 * Läd die Wettbewerbsjahre aller beendeter Wettbewerbe.
	 *
	 * @return
	 */
	public List<Wettbewerb> loadWettbewerbe() {

		try {

			Response response = mkGatewayRestClient.loadWettbewerbsjahre(authConfig.client(),
				getSecretBase64());

			Wettbewerb[] result = response.readEntity(new GenericType<Wettbewerb[]>() {
			});

			LOGGER.info("Anzahl beendete Wettbewerbe={}", result.length);

			return Arrays.asList(result);
		} catch (Exception e) {

			throw new MinikaenguruStatistikCommunicationExcepion(
				"Beim Aufruf von mk-gateway/mkbiza/wettbewerbe ist ein Fehler aufgetreten: " + e.getMessage(), e);

		}
	}

	public WettbewerbDetails getWettbewerbDetails(final Integer jahr) {

		try {

			Response response = mkGatewayRestClient.getStatistikWettbewerb(jahr, authConfig.client(),
				getSecretBase64());

			WettbewerbDetails result = response.readEntity(WettbewerbDetails.class);

			return result;
		} catch (Exception e) {

			throw new MinikaenguruStatistikCommunicationExcepion(
				"Beim Aufruf von mk-gateway/mkbiza/wettbewerbe/" + jahr + " ist ein Fehler aufgetreten: " + e.getMessage(), e);

		}

	}

	/**
	 * @return
	 */
	private String getSecretBase64() {

		return BaseAuthHeaderUtils.getSecretBase64(authConfig.header());
	}
}
