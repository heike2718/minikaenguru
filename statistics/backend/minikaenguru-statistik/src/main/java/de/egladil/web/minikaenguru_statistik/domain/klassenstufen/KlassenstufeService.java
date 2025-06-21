// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.domain.klassenstufen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.minikaenguru_statistik.domain.Aufgabenkategorie;
import de.egladil.web.minikaenguru_statistik.domain.FormattingUtils;
import de.egladil.web.minikaenguru_statistik.domain.Klassenstufe;
import de.egladil.web.minikaenguru_statistik.domain.StatistikUtils;
import de.egladil.web.minikaenguru_statistik.domain.aufgaben.AufgabeDetails;
import de.egladil.web.minikaenguru_statistik.domain.aufgaben.AufgabeDetailsComparator;
import de.egladil.web.minikaenguru_statistik.domain.aufgaben.MjaAufgabeDetails;
import de.egladil.web.minikaenguru_statistik.domain.aufgaben.MkGatewayStatistikAufgabe;
import de.egladil.web.minikaenguru_statistik.domain.auth.BaseAuthHeaderUtils;
import de.egladil.web.minikaenguru_statistik.domain.auth.MinikaenguruStatistikAuthConfig;
import de.egladil.web.minikaenguru_statistik.domain.dto.Gruppierungsitem;
import de.egladil.web.minikaenguru_statistik.domain.exeptions.MinikaenguruStatistikCommunicationExcepion;
import de.egladil.web.minikaenguru_statistik.infrastructure.restclient.RaetselbaukastenRestClient;
import de.egladil.web.minikaenguru_statistik.infrastructure.restclient.MkGatewayRestClient;
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
	MinikaenguruStatistikAuthConfig authConfig;

	@Inject
	@RestClient
	MkGatewayRestClient mkGatewayRestClient;

	@Inject
	@RestClient
	RaetselbaukastenRestClient raetselbaukastenRestClient;

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

		int anzahlKinderGesamt = mkGatewayStatistikKlassenstufe.getAnzahlKinderGesamt();

		result.setAnzahlKinderGesamt(anzahlKinderGesamt);
		result.setKinderJeSprache(mkGatewayStatistikKlassenstufe.getKinderJeSprache());
		result.setKinderJeTeilnahmeart(mkGatewayStatistikKlassenstufe.getKinderJeTeilnahmeart());
		result.setKlassenstufe(mkGatewayStatistikKlassenstufe.getKlassenstufe());
		result.setKinderJeLand(mkGatewayStatistikKlassenstufe.getKinderJeLand());
		result.setWettbewerbsjahr(mkGatewayStatistikKlassenstufe.getWettbewerbsjahr());
		result.setStartguthaben(klassenstufe.getStartguthaben());
		result.setBeendet(mkGatewayStatistikKlassenstufe.isBeendet());

		if (mkGatewayStatistikKlassenstufe.isBeendet()) {

			result.setKinderJePunktintervall(
				StatistikUtils.listReverse(mkGatewayStatistikKlassenstufe.getKinderJePunktintervall()));
			result.setRohpunkte(mkGatewayStatistikKlassenstufe.getRohpunkte());

			int anzahlKindermitVollerPunktzahl = StatistikUtils.findAnzahlKindermitVollerPunktzahl(
				mkGatewayStatistikKlassenstufe.getRohpunkte(), klassenstufe, Integer.valueOf(jahr));

			result.setAnzahlKinderMitVollerPunktzahl(anzahlKindermitVollerPunktzahl);
			result.setMedianUndGesamtpunkte(mkGatewayStatistikKlassenstufe.getMedianUndGesamtpunkte());

			for (MkGatewayStatistikAufgabe statistik : mkGatewayStatistikKlassenstufe.getAufgabenstatistiken()) {

				AufgabeDetails aufgabeDetails = new AufgabeDetails();
				Aufgabenkategorie aufgabenkategorie = Aufgabenkategorie.valueOfNummer(statistik.getNummer());

				Optional<Gruppierungsitem> optRichtig = statistik.getAnzahlenJeWertungscode().stream()
					.filter(g -> "richtig gelöst".equals(g.getName())).findFirst();

				int anzahlRichtig = optRichtig.isEmpty() ? 0 : Long.valueOf(optRichtig.get().getAnzahl()).intValue();
				double prozentRichtig = StatistikUtils.calculatePercentRoundedUpTo2Digits(anzahlRichtig, anzahlKinderGesamt);

				aufgabeDetails.setProzentRichtigerLoesungen(FormattingUtils.doubleAsString(prozentRichtig));
				aufgabeDetails.setPassung(StatistikUtils.estimatePassung(aufgabenkategorie, prozentRichtig));

				aufgabeDetails.setGradZugehoerigkeitZuAufgabenkategorie(
					FormattingUtils.doubleAsString(
						StatistikUtils.calculateMembershipDegree(aufgabenkategorie, anzahlRichtig, anzahlKinderGesamt)));

				List<Gruppierungsitem> anzahlenJeLoesungsbuchstabe = statistik.getAnzahlenJeLoesungsbuchstabe();

				if (StatistikUtils.isDatenVorhanden(anzahlenJeLoesungsbuchstabe)) {

					if (klassenstufe == Klassenstufe.IKID) {

						// hier liefert mk-gateway leider A-E und N, obwohl es nur von A bis C geht.
						List<Gruppierungsitem> gruppierungsitemsIKID = anzahlenJeLoesungsbuchstabe.stream()
							.filter(g -> !"D".equals(g.getName()) && !"E".equals(g.getName())).toList();

						aufgabeDetails.setAnzahlenJeLoesungsbuchstabe(gruppierungsitemsIKID);
					} else {

						aufgabeDetails.setAnzahlenJeLoesungsbuchstabe(anzahlenJeLoesungsbuchstabe);
					}

				}

				aufgabeDetails
					.setAnzahlenJeWertungscode(StatistikUtils.sortTheWertungscodes(statistik.getAnzahlenJeWertungscode()));
				aufgabeDetails.setNummer(statistik.getNummer());
				String strafpunkte = statistik.getStrafpunkte();

				if (strafpunkte.startsWith(",")) {

					strafpunkte = "0" + strafpunkte;
				}

				aufgabeDetails.setStrafpunkte(strafpunkte);
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

					Aufgabenkategorie aufgabenkategorie = Aufgabenkategorie.valueOfNummer(aufgabeDetails.getNummer());
					aufgabeDetails.setPunkte(aufgabenkategorie.getPunkte());
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

			Klassenstufe theKlassenstufe = klassenstufe;
			int theJahr = Integer.valueOf(jahr);

			// https://github.com/heike2718/minikaenguru/issues/464: 2014, 2015 und 2016 lösten Erstklässler die Aufgaben der Klasse
			// 2
			if (Klassenstufe.EINS == klassenstufe && theJahr >= 2014 && theJahr <= 2016) {

				theKlassenstufe = Klassenstufe.ZWEI;
			}

			Response response = raetselbaukastenRestClient.getAufgabenMinikaenguruwettbewerb(authConfig.client(), jahr, theKlassenstufe);

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

			throw new MinikaenguruStatistikCommunicationExcepion(
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

			throw new MinikaenguruStatistikCommunicationExcepion(
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
