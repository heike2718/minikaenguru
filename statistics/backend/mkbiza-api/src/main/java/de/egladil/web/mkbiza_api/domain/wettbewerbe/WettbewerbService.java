// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.wettbewerbe;

import java.util.Arrays;
import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mkbiza_api.domain.Klassenstufe;
import de.egladil.web.mkbiza_api.domain.aufgaben.MinikaenguruAufgabenDto;
import de.egladil.web.mkbiza_api.domain.auth.BaseAuthHeaderUtils;
import de.egladil.web.mkbiza_api.domain.auth.MkBiZaAuthConfig;
import de.egladil.web.mkbiza_api.domain.exeptions.MkBiZaCommunicationExcepion;
import de.egladil.web.mkbiza_api.infrastructure.restclient.MjaApiRestClient;
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
	MkGatewayRestClient mkGatewayRestClient;

	@Inject
	@RestClient
	MjaApiRestClient mjaApiRestClient;

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

			throw new MkBiZaCommunicationExcepion(
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

			throw new MkBiZaCommunicationExcepion(
				"Beim Aufruf von mk-gateway/mkbiza/wettbewerbe/" + jahr + " ist ein Fehler aufgetreten: " + e.getMessage(), e);

		}

	}

	/**
	 * @return
	 */
	private String getSecretBase64() {

		return BaseAuthHeaderUtils.getSecretBase64(authConfig.header());
	}

	/**
	 * @param  jahr
	 * @param  klassenstufe
	 * @return              MinikaenguruAufgabenDto
	 */
	public MinikaenguruAufgabenDto getAufgabenWettbewerb(final String jahr, final Klassenstufe klassenstufe) {

		try {

			Response response = mjaApiRestClient.getAufgabenMinikaenguruwettbewerb(authConfig.client(), jahr, klassenstufe);

			MinikaenguruAufgabenDto result = response.readEntity(MinikaenguruAufgabenDto.class);

			return result;
		} catch (Exception e) {

			throw new MkBiZaCommunicationExcepion(
				"Beim Aufruf von mja-api/public/minikaenguru/" + jahr + "/" + klassenstufe + " ist ein Fehler aufgetreten: "
					+ e.getMessage(),
				e);

		}

	}

}
