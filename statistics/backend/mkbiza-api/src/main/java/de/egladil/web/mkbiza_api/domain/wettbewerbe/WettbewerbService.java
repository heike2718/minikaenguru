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
import de.egladil.web.mkbiza_api.domain.aufgaben.Aufgabe;
import de.egladil.web.mkbiza_api.domain.aufgaben.AufgabenService;
import de.egladil.web.mkbiza_api.domain.aufgaben.MinikaenguruKlassenstufeDto;
import de.egladil.web.mkbiza_api.domain.aufgaben.StatistikAufgabe;
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
	AufgabenService aufgabenService;

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
	 * @return              MinikaenguruKlassenstufeDto
	 */
	public MinikaenguruKlassenstufeDto getAufgabenWettbewerb(final String jahr, final Klassenstufe klassenstufe) {

		MinikaenguruKlassenstufeDto klassenstufeDto = loadTheKlassenstufeDto(jahr, klassenstufe);

		List<Aufgabe> aufgaben = klassenstufeDto.getAufgaben();

		for (Aufgabe aufgabe : aufgaben) {

			StatistikAufgabe statistikZuAufgabe = aufgabenService
				.getStatistikZuAufgabe(Integer.valueOf(klassenstufeDto.getWettbewerbsjahr()), klassenstufe, aufgabe.getNummer());
			aufgabe.setStatistik(statistikZuAufgabe);

			switch (aufgabe.getPunkte()) {

			case 3:
				aufgabe.setStrafpunkte("0,75");
				break;

			case 4:
				aufgabe.setStrafpunkte("1");
				break;

			case 5:
				aufgabe.setStrafpunkte("1,25");
				break;

			default:
				aufgabe.setStrafpunkte("huch, eine neue Aufgabenkategorie");
				break;
			}
		}

		return klassenstufeDto;

	}

	/**
	 * @param  jahr
	 * @param  klassenstufe
	 * @return
	 */
	private MinikaenguruKlassenstufeDto loadTheKlassenstufeDto(final String jahr, final Klassenstufe klassenstufe) {

		try {

			Response response = mjaApiRestClient.getAufgabenMinikaenguruwettbewerb(authConfig.client(), jahr, klassenstufe);

			MinikaenguruKlassenstufeDto result = response.readEntity(MinikaenguruKlassenstufeDto.class);

			return result;
		} catch (Exception e) {

			throw new MkBiZaCommunicationExcepion(
				"Beim Aufruf von mja-api/public/minikaenguru/" + jahr + "/" + klassenstufe + " ist ein Fehler aufgetreten: "
					+ e.getMessage(),
				e);

		}
	}

}
