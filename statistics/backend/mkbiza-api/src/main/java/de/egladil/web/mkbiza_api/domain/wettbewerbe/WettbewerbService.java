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
import de.egladil.web.mkbiza_api.domain.dto.MessagePayload;
import de.egladil.web.mkbiza_api.domain.exeptions.MkBiZaCommunicationExcepion;
import de.egladil.web.mkbiza_api.infrastructure.restclient.MkGatewayRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
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

			throw new MkBiZaCommunicationExcepion("Beim Aufruf von mk-gateway/mkbiza/wettbewerbe ist ein Fehler aufgetreten", e);

		}
	}

	public WettbewerbDetails getWettbewerbDetails(final String jahr) {

		try {

			Integer wettbewerbsjahr = Integer.valueOf(jahr);

			Response response = mkGatewayRestClient.getStatistikWettbewerb(wettbewerbsjahr, authConfig.client(),
				getSecretBase64());

			WettbewerbDetails result = response.readEntity(WettbewerbDetails.class);

			return result;
		} catch (NumberFormatException e) {

			throw new MkBiZaCommunicationExcepion("Bad Request: jahr ist nicht numerisch",
				new WebApplicationException(Response.status(400).entity(MessagePayload.error("jahr ist nicht numerisch")).build()));
		} catch (Exception e) {

			throw new MkBiZaCommunicationExcepion(
				"Beim Aufruf von mk-gateway/mkbiza/wettbewerbe/" + jahr + " ist ein Fehler aufgetreten", e);

		}

	}

	/**
	 * @return
	 */
	private String getSecretBase64() {

		return new String(Base64.getEncoder().encode(authConfig.header().getBytes()));
	}

}
