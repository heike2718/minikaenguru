// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.klassenstufen;

import java.util.Optional;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.egladil.web.mkbiza_api.domain.Klassenstufe;
import de.egladil.web.mkbiza_api.domain.aufgaben.AufgabeDetails;
import de.egladil.web.mkbiza_api.domain.aufgaben.MjaAufgabeDetails;
import de.egladil.web.mkbiza_api.domain.aufgaben.MkGatewayStatistikAufgabe;
import de.egladil.web.mkbiza_api.domain.auth.BaseAuthHeaderUtils;
import de.egladil.web.mkbiza_api.domain.auth.MkBiZaAuthConfig;
import de.egladil.web.mkbiza_api.domain.exeptions.MkBiZaCommunicationExcepion;
import de.egladil.web.mkbiza_api.infrastructure.restclient.MjaApiRestClient;
import de.egladil.web.mkbiza_api.infrastructure.restclient.MkGatewayRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

/**
 * KlassenstufeService
 */
@ApplicationScoped
public class KlassenstufeService {

	@Inject
	MkBiZaAuthConfig authConfig;

	@Inject
	@RestClient
	MkGatewayRestClient mkGatewayRestClient;

	@Inject
	@RestClient
	MjaApiRestClient mjaApiRestClient;

	/**
	 * Läd alle Aufgaben und statistischen Daten für eine Klassenstufe eines Wettbewerbs.
	 *
	 * @param  jahr
	 * @param  klassenstufe
	 * @return              KlassenstufeDetails
	 */
	public KlassenstufeDetails loadKlassenstufenDetails(final String jahr, final Klassenstufe klassenstufe) {

		MkGatewayStatistikKlassenstufe mkGatewayStatistikKlassenstufe = loadStatistikZuKlassenstufe(jahr, klassenstufe);

		KlassenstufeDetails result = new KlassenstufeDetails();
		result.setAnzahlKinderGesamt(mkGatewayStatistikKlassenstufe.getAnzahlKinderGesamt());
		result.setKinderJeSprache(mkGatewayStatistikKlassenstufe.getKinderJeSprache());
		result.setKinderJeTeilnahmeart(mkGatewayStatistikKlassenstufe.getKinderJeTeilnahmeart());
		result.setKlassenstufe(mkGatewayStatistikKlassenstufe.getKlassenstufe());
		result.setKinderJeLand(mkGatewayStatistikKlassenstufe.getKinderJeLand());
		result.setWettbewerbsjahr(mkGatewayStatistikKlassenstufe.getWettbewerbsjahr());

		if (mkGatewayStatistikKlassenstufe.isBeendet()) {

			result.setKinderJePunktintervall(mkGatewayStatistikKlassenstufe.getKinderJePunktintervall());
			result.setRohpunkte(mkGatewayStatistikKlassenstufe.getRohpunkte());
			result.setMedianUndGesamtpunkte(mkGatewayStatistikKlassenstufe.getMedianUndGesamtpunkte());

			MjaAufgabenKlassenstufeDto mjaAufgabenKlassenstufeDto = loadAufgabenZuKlassenstufe(jahr, klassenstufe);

			for (MjaAufgabeDetails mjaAufgabeDetials : mjaAufgabenKlassenstufeDto.getAufgaben()) {

				AufgabeDetails aufgabeDetails = new AufgabeDetails();
				aufgabeDetails.setImages(mjaAufgabeDetials.getImages());
				aufgabeDetails.setLoesungsbuchstabe(mjaAufgabeDetials.getLoesungsbuchstabe());
				aufgabeDetails.setNummer(mjaAufgabeDetials.getNummer());
				aufgabeDetails.setPunkte(mjaAufgabeDetials.getPunkte());
				aufgabeDetails.setQuelle(mjaAufgabeDetials.getQuelle());

				Optional<MkGatewayStatistikAufgabe> optStatistik = mkGatewayStatistikKlassenstufe.getAufgabenstatistiken().stream()
					.filter(a -> mjaAufgabeDetials.getNummer().equals(a.getNummer())).findFirst();

				if (optStatistik.isPresent()) {

					MkGatewayStatistikAufgabe statistik = optStatistik.get();

					aufgabeDetails.setAnzahlenJeLoesungsbuchstabe(statistik.getAnzahlenJeLoesungsbuchstabe());
					aufgabeDetails.setAnzahlenJeWertungscode(statistik.getAnzahlenJeWertungscode());
					aufgabeDetails.setNummer(statistik.getNummer());
					aufgabeDetails.setStrafpunkte(statistik.getStrafpunkte());

				}

				result.addAufgaben(aufgabeDetails);

			}
		}

		return result;

	}

	/**
	 * @param  jahr
	 * @param  klassenstufe
	 * @return              MjaAufgabenKlassenstufeDto
	 */
	private MjaAufgabenKlassenstufeDto loadAufgabenZuKlassenstufe(final String jahr, final Klassenstufe klassenstufe) {

		try {

			Response response = mjaApiRestClient.getAufgabenMinikaenguruwettbewerb(authConfig.client(), jahr, klassenstufe);

			MjaAufgabenKlassenstufeDto result = response.readEntity(MjaAufgabenKlassenstufeDto.class);

			return result;
		} catch (Exception e) {

			throw new MkBiZaCommunicationExcepion(
				"Beim Aufruf von mja-api/public/minikaenguru/" + jahr + "/" + klassenstufe + " ist ein Fehler aufgetreten: "
					+ e.getMessage(),
				e);

		}
	}

	/**
	 * @param  jahr
	 * @param  klassenstufe
	 * @return
	 */
	private MkGatewayStatistikKlassenstufe loadStatistikZuKlassenstufe(final String jahr, final Klassenstufe klassenstufe) {

		try {

			Response response = mkGatewayRestClient.getStatistikKlassenstufe(jahr, klassenstufe, authConfig.client(),
				getSecretBase64());

			MkGatewayStatistikKlassenstufe result = response.readEntity(MkGatewayStatistikKlassenstufe.class);

			return result;
		} catch (Exception e) {

			throw new MkBiZaCommunicationExcepion(
				"Beim Aufruf von mja-api/public/minikaenguru/" + jahr + "/" + klassenstufe + " ist ein Fehler aufgetreten: "
					+ e.getMessage(),
				e);

		}
	}

	/**
	 * @return
	 */
	private String getSecretBase64() {

		return BaseAuthHeaderUtils.getSecretBase64(authConfig.header());
	}
}
