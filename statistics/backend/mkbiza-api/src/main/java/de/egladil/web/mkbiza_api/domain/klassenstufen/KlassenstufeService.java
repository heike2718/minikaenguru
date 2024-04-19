// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.klassenstufen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mkbiza_api.domain.Klassenstufe;
import de.egladil.web.mkbiza_api.domain.aufgaben.AufgabeDetails;
import de.egladil.web.mkbiza_api.domain.aufgaben.AufgabeDetailsComparator;
import de.egladil.web.mkbiza_api.domain.aufgaben.MjaAufgabeDetails;
import de.egladil.web.mkbiza_api.domain.aufgaben.MkGatewayStatistikAufgabe;
import de.egladil.web.mkbiza_api.domain.auth.BaseAuthHeaderUtils;
import de.egladil.web.mkbiza_api.domain.auth.MkBiZaAuthConfig;
import de.egladil.web.mkbiza_api.domain.dto.Gruppierungsitem;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(KlassenstufeService.class);

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

		List<AufgabeDetails> aufgabendetailsList = new ArrayList<>();

		KlassenstufeDetails result = new KlassenstufeDetails();
		result.setAnzahlKinderGesamt(mkGatewayStatistikKlassenstufe.getAnzahlKinderGesamt());
		result.setKinderJeSprache(mkGatewayStatistikKlassenstufe.getKinderJeSprache());
		result.setKinderJeTeilnahmeart(mkGatewayStatistikKlassenstufe.getKinderJeTeilnahmeart());
		result.setKlassenstufe(mkGatewayStatistikKlassenstufe.getKlassenstufe());
		result.setKinderJeLand(mkGatewayStatistikKlassenstufe.getKinderJeLand());
		result.setWettbewerbsjahr(mkGatewayStatistikKlassenstufe.getWettbewerbsjahr());
		result.setStartguthaben(klassenstufe.getStartguthaben());
		result.setBeendet(mkGatewayStatistikKlassenstufe.isBeendet());

		if (mkGatewayStatistikKlassenstufe.isBeendet()) {

			result.setKinderJePunktintervall(reverseTheList(mkGatewayStatistikKlassenstufe.getKinderJePunktintervall()));
			result.setRohpunkte(mkGatewayStatistikKlassenstufe.getRohpunkte());
			result.setMedianUndGesamtpunkte(mkGatewayStatistikKlassenstufe.getMedianUndGesamtpunkte());

			for (MkGatewayStatistikAufgabe statistik : mkGatewayStatistikKlassenstufe.getAufgabenstatistiken()) {

				AufgabeDetails aufgabeDetails = new AufgabeDetails();
				aufgabeDetails.setAnzahlenJeLoesungsbuchstabe(statistik.getAnzahlenJeLoesungsbuchstabe());
				aufgabeDetails.setAnzahlenJeWertungscode(statistik.getAnzahlenJeWertungscode());
				aufgabeDetails.setNummer(statistik.getNummer());
				aufgabeDetails.setStrafpunkte(statistik.getStrafpunkte());
				aufgabendetailsList.add(aufgabeDetails);
			}

			MjaAufgabenKlassenstufeDto mjaAufgabenKlassenstufeDto = loadAufgabenZuKlassenstufe(jahr, klassenstufe);

			if (mjaAufgabenKlassenstufeDto != null) {

				for (MjaAufgabeDetails mjaAufgabeDetials : mjaAufgabenKlassenstufeDto.getAufgaben()) {

					Optional<AufgabeDetails> optAufgabeDetails = aufgabendetailsList.stream()
						.filter(a -> a.getNummer().equals(mjaAufgabeDetials.getNummer())).findFirst();

					if (optAufgabeDetails.isPresent()) {

						AufgabeDetails aufgabeDetails = optAufgabeDetails.get();
						aufgabeDetails.setImages(mjaAufgabeDetials.getImages());
						aufgabeDetails.setLoesungsbuchstabe(mjaAufgabeDetials.getLoesungsbuchstabe());
						aufgabeDetails.setNummer(mjaAufgabeDetials.getNummer());
						aufgabeDetails.setPunkte(mjaAufgabeDetials.getPunkte());
						aufgabeDetails.setQuelle(mjaAufgabeDetials.getQuelle());

					}
				}
			} else {

				for (AufgabeDetails aufgabeDetails : aufgabendetailsList) {

					String kategorie = aufgabeDetails.getNummer().substring(0, 1);

					switch (kategorie) {

					case "A":
						aufgabeDetails.setPunkte(3);
						break;

					case "B":
						aufgabeDetails.setPunkte(4);
						break;

					case "C":
						aufgabeDetails.setPunkte(5);
						break;

					default:
						break;
					}

				}

			}
		}

		AufgabeDetailsComparator comparator = new AufgabeDetailsComparator();
		aufgabendetailsList.sort(comparator);
		result.setAufgaben(aufgabendetailsList);

		LOGGER.info(result.toString());
		return result;

	}

	/**
	 * @param  jahr
	 * @param  klassenstufe
	 * @return              MjaAufgabenKlassenstufeDto oder null, falls die Aufgaben noch nicht im Aufgabenarchiv vorhanden sind.
	 */
	private MjaAufgabenKlassenstufeDto loadAufgabenZuKlassenstufe(final String jahr, final Klassenstufe klassenstufe) {

		try {

			Response response = mjaApiRestClient.getAufgabenMinikaenguruwettbewerb(authConfig.client(), jahr, klassenstufe);

			MjaAufgabenKlassenstufeDto result = response.readEntity(MjaAufgabenKlassenstufeDto.class);

			return result;
		} catch (Exception e) {

			if (e instanceof ClientWebApplicationException) {

				ClientWebApplicationException cwae = (ClientWebApplicationException) e;

				if (cwae.getResponse().getStatus() == 404) {

					LOGGER.warn("Aufgaben fuer {} - {} noch nicht im Aufgabenarchiv. Statistiken koennen trotzdem geladen werden",
						jahr, klassenstufe);
					return null;
				}
			}

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

	private List<Gruppierungsitem> reverseTheList(final List<Gruppierungsitem> gruppierungsitems) {

		int anzahl = gruppierungsitems.size();
		List<Gruppierungsitem> result = new ArrayList<>(anzahl);

		for (int i = anzahl - 1; i >= 0; i--) {

			result.add(gruppierungsitems.get(i));

		}
		return result;
	}
}
