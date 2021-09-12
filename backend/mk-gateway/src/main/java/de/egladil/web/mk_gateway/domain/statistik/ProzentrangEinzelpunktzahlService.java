// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.weld.exceptions.IllegalArgumentException;

import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.error.StatistikKeineDatenException;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.statistik.api.ProzentrangAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.comparators.RohpunktItemAscendingComparator;
import de.egladil.web.mk_gateway.domain.statistik.comparators.RohpunktItemDescendingComparator;
import de.egladil.web.mk_gateway.domain.statistik.functions.PunkteStringMapper;
import de.egladil.web.mk_gateway.domain.statistik.impl.UbersichtRohpunktItemsRechner;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbRepository;

/**
 * ProzentrangEinzelpunktzahlService berechnet den Prozentrang für eine gegebene Punktzahl.
 */
@ApplicationScoped
public class ProzentrangEinzelpunktzahlService {

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	@Inject
	WettbewerbRepository wettbewerbRepository;

	private final UbersichtRohpunktItemsRechner rohpunktItemsRechner = new UbersichtRohpunktItemsRechner();

	/**
	 * @param  wettbewerbsjahr
	 *                         String
	 * @param  klassenstufe
	 *                         Klassenstufe
	 * @param  punkte
	 *                         int die Pubktzahl als Ganzzahl, also mit 100 multipliziert.
	 * @return                 ProzentrangAPIModel
	 */
	public ProzentrangAPIModel berechneProzentrang(final Integer wettbewerbsjahr, final Klassenstufe klassenstufe, final int punkte) {

		checkParameters(wettbewerbsjahr, klassenstufe, punkte);

		WettbewerbID wettbewerbID = new WettbewerbID(wettbewerbsjahr);

		Optional<Wettbewerb> optWettbewerb = this.wettbewerbRepository.wettbewerbMitID(wettbewerbID);

		if (optWettbewerb.isEmpty()) {

			int aktuellesJahr = LocalDateTime.now().toLocalDate().getYear();

			throw new InvalidInputException(
				ResponsePayload.messageOnly(MessagePayload.warn("Es liegen nur Daten ab 2010 bis " + aktuellesJahr + " vor")));
		}

		Wettbewerb wettbewerb = optWettbewerb.get();

		if (!wettbewerb.isBeendet()) {

			throw new StatistikKeineDatenException();

		}

		List<Loesungszettel> loesungszettelKlassenstufe = this.loesungszettelRepository
			.loadAllForWettbewerbAndKlassenstufe(wettbewerbID, klassenstufe);

		if (loesungszettelKlassenstufe.isEmpty()) {

			throw new StatistikKeineDatenException();
		}

		int anzahlLoesungszettel = loesungszettelKlassenstufe.size();

		List<RohpunktItem> rohpunktItems = this.rohpunktItemsRechner.berechneRohpunktItems(loesungszettelKlassenstufe);

		Integer anzahlKinderBesser = rohpunktItems.stream().filter(ri -> ri.getPunkte() > punkte)
			.map(ri -> ri.getAnzahl()).collect(Collectors.summingInt(Integer::intValue));

		new VerteilungRechner().addProzentraengeToRohpunktItems(rohpunktItems, anzahlLoesungszettel);

		String punkteText = new PunkteStringMapper().apply(punkte);
		String punkteMax = new PunkteStringMapper().apply(klassenstufe.getMaximalpunktzahlMal100());

		ProzentrangAPIModel result = new ProzentrangAPIModel().withKlassenstufe(klassenstufe.getLabel())
			.withWettbewerbsjahr(wettbewerbsjahr.toString())
			.withAnzahlLoesungszettel(anzahlLoesungszettel)
			.withAnzahlKinderBesser(anzahlKinderBesser)
			.withPunkteText(punkteText + " von " + punkteMax);

		Optional<RohpunktItem> optMitGleicherPunktzahl = rohpunktItems.stream().filter(ri -> ri.getPunkte() == punkte).findFirst();

		if (optMitGleicherPunktzahl.isPresent()) {

			RohpunktItem treffer = optMitGleicherPunktzahl.get();
			String text = MessageFormat.format(applicationMessages.getString("statistik.prozentrang.exakterTreffer.text"),
				new Object[] { String.valueOf(wettbewerbsjahr), treffer.getProzentrangText() });

			if (anzahlLoesungszettel > 1) {

				if (treffer.equals(rohpunktItems.get(0))) {

					if (treffer.getAnzahl() == 1) {

						text = MessageFormat.format(
							applicationMessages.getString("statistik.prozentrang.exakterTreffer.maximalSingular.text"),
							new Object[] { String.valueOf(String.valueOf(wettbewerbsjahr)) });
					} else {

						text = MessageFormat.format(
							applicationMessages.getString("statistik.prozentrang.exakterTreffer.maximalPlural.text"),
							new Object[] { String.valueOf(wettbewerbsjahr), "" + treffer.getAnzahl() });
					}

					return result.withProzentrang("100").withText(text);
				}
			}

			return result.withProzentrang(treffer.getProzentrangText()).withText(text);

		}

		Collections.sort(rohpunktItems, new RohpunktItemAscendingComparator());

		Optional<RohpunktItem> optErstesMitMehrPunkten = rohpunktItems.stream().filter(ri -> ri.getPunkte() > punkte).findFirst();

		if (optErstesMitMehrPunkten.isEmpty()) {

			String text = MessageFormat.format(applicationMessages.getString("statistik.prozentrang.besserAlsAlle.text"),
				new Object[] { String.valueOf(wettbewerbsjahr) });

			return result.withProzentrang("100").withText(text);

		}

		Collections.sort(rohpunktItems, new RohpunktItemDescendingComparator());
		Optional<RohpunktItem> optErstesMitWenigerPunkten = rohpunktItems.stream().filter(ri -> ri.getPunkte() < punkte)
			.findFirst();

		if (optErstesMitWenigerPunkten.isEmpty()) {

			String text = MessageFormat.format(applicationMessages.getString("statistik.prozentrang.schlechterAlsAlle.text"),
				new Object[] { "" + anzahlLoesungszettel, punkteText });

			return result.withProzentrang("0").withText(text);
		}

		RohpunktItem infimum = optErstesMitWenigerPunkten.get();

		String text = MessageFormat.format(applicationMessages.getString("statistik.prozentrang.innerhalbIntervall.text"),
			new Object[] { "" + anzahlLoesungszettel, infimum.getProzentrangText() });

		return result.withProzentrang(infimum.getProzentrangText()).withText(text);
	}

	/**
	 * @param wettbewerbsjahr
	 * @param klassenstufe
	 * @param punkte
	 */
	void checkParameters(final Integer wettbewerbsjahr, final Klassenstufe klassenstufe, final int punkte) {

		if (wettbewerbsjahr == null) {

			throw new IllegalArgumentException("wettbewerbsjahr null");
		}

		if (klassenstufe == null) {

			throw new IllegalArgumentException("klassenstufe null");
		}

		if (punkte < 0) {

			throw new InvalidInputException(
				ResponsePayload
					.messageOnly(MessagePayload.error(applicationMessages.getString("statistik.prozentrang.punkteNegativ"))));
		}

		if (wettbewerbsjahr.intValue() < klassenstufe.getMinimalesWettbewerbsjahrMitStatistik()) {

			throw new StatistikKeineDatenException();
		}

		int maximalpunktzahlMal100 = klassenstufe.getMaximalpunktzahlMal100();

		if (punkte > maximalpunktzahlMal100) {

			String text = MessageFormat.format(applicationMessages.getString("statistik.prozentrang.punkteZuHoch"),
				new Object[] { klassenstufe, "" + maximalpunktzahlMal100 });

			throw new InvalidInputException(
				ResponsePayload.messageOnly(MessagePayload.error(text)));
		}
	}
}
