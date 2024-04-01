// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.aufgaben;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.egladil.web.mkbiza_api.domain.Klassenstufe;
import de.egladil.web.mkbiza_api.domain.auth.BaseAuthHeaderUtils;
import de.egladil.web.mkbiza_api.domain.auth.MkBiZaAuthConfig;
import de.egladil.web.mkbiza_api.domain.exeptions.MkBiZaCommunicationExcepion;
import de.egladil.web.mkbiza_api.infrastructure.restclient.MkGatewayRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

/**
 * AufgabenService
 */
@ApplicationScoped
public class AufgabenService {

	@Inject
	MkBiZaAuthConfig authConfig;

	@Inject
	@RestClient
	MkGatewayRestClient mkGatewayRestClient;

	public StatistikAufgabe getStatistikZuAufgabe(final Integer jahr, final Klassenstufe klassenstufe, final String nummer) {

		try {

			Response response = mkGatewayRestClient.getStatistikAufgabe(jahr, klassenstufe, nummer, authConfig.client(),
				getSecretBase64());

			StatistikAufgabe result = response.readEntity(StatistikAufgabe.class);

			return result;
		} catch (Exception e) {

			String message = "Beim Aufruf von mk-gateway/mkbiza/wettbewerbe/" + jahr + "/" + klassenstufe + "/aufgaben/"
				+ nummer + " ist ein Fehler aufgetreten";

			throw new MkBiZaCommunicationExcepion(message, e);
		}

	}

	/**
	 * @return
	 */
	private String getSecretBase64() {

		return BaseAuthHeaderUtils.getSecretBase64(authConfig.header());
	}
}
