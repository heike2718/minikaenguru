// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.egladil.web.minikaenguru_statistik.domain.dto.Gruppierungsitem;
import de.egladil.web.minikaenguru_statistik.domain.exeptions.MkBiZaRuntimeException;
import de.egladil.web.minikaenguru_statistik.domain.klassenstufen.RohpunktItem;

/**
 * StatistikUtils
 */
public class StatistikUtils {

	/**
	 * @param  gruppierungsitems
	 * @return                   boolean
	 */
	public static boolean isDatenVorhanden(final List<Gruppierungsitem> gruppierungsitems) {

		return gruppierungsitems.stream().map(g -> g.getAnzahl()).count() > 0;
	}

	/**
	 * Sortiert die Gruppierungsitems nach dem Namen, so dass richtig blau, nicht orange und falsch rot gefärbt werden.
	 *
	 * @param  gruppierungsitems
	 * @return
	 */
	public static List<Gruppierungsitem> sortTheWertungscodes(final List<Gruppierungsitem> gruppierungsitems) {

		List<Gruppierungsitem> result = new ArrayList<>(gruppierungsitems.size());

		{

			Optional<Gruppierungsitem> optItem = gruppierungsitems.stream().filter(g -> "richtig gelöst".equals(g.getName()))
				.findFirst();

			if (optItem.isPresent()) {

				result.add(optItem.get());
			}
		}

		{

			Optional<Gruppierungsitem> optItem = gruppierungsitems.stream().filter(g -> "falsch gelöst".equals(g.getName()))
				.findFirst();

			if (optItem.isPresent()) {

				result.add(optItem.get());
			}
		}

		{

			Optional<Gruppierungsitem> optItem = gruppierungsitems.stream().filter(g -> "nicht gelöst".equals(g.getName()))
				.findFirst();

			if (optItem.isPresent()) {

				result.add(optItem.get());
			}
		}

		return result;

	}

	/**
	 * Liste wird andersherum sortiert.
	 *
	 * @param  gruppierungsitems
	 * @return
	 */
	public static List<Gruppierungsitem> listReverse(final List<Gruppierungsitem> gruppierungsitems) {

		int anzahl = gruppierungsitems.size();
		List<Gruppierungsitem> result = new ArrayList<>(anzahl);

		for (int i = anzahl - 1; i >= 0; i--) {

			result.add(gruppierungsitems.get(i));

		}
		return result;
	}

	/**
	 * Ermittelt aus den RohpunktItems die Kinder mit voller Punktzahl
	 *
	 * @param  rohpunktItems
	 * @param  klassenstufe
	 * @return               int
	 */
	public static int findAnzahlKindermitVollerPunktzahl(final List<RohpunktItem> rohpunktItems, final Klassenstufe klassenstufe, final Integer wettbewerbsjahr) {

		String vollePunktzahlString = getVollePunktzahlString(klassenstufe, wettbewerbsjahr);

		Optional<RohpunktItem> optItem = rohpunktItems.stream().filter(i -> vollePunktzahlString.equals(i.getPunkte())).findFirst();

		return optItem.isEmpty() ? 0 : Integer.valueOf(optItem.get().getAnzahl()).intValue();
	}

	private static String getVollePunktzahlString(final Klassenstufe klassenstufe, final Integer wettbewerbsjahr) {

		switch (klassenstufe) {

		case IKID:

			return "36,00";

		case EINS: {

			return wettbewerbsjahr >= 2017 ? "60,00" : "75,00";
		}

		default:
			return "75,00";
		}
	}

	/**
	 * Berechnet den Grad der Zugehörigkeit einer Aufgabe zur gegebenen Kategorie.
	 *
	 * @param  aufgabenkategorie
	 * @param  prozentRichtigerLoesungen
	 * @return
	 */
	public static double calculateMembershipDegree(final Aufgabenkategorie aufgabenkategorie, final int anzahlRichtig, final int anzahlGesamt) {

		if (anzahlGesamt == 0) {

			return 0;
		}

		double percentage = (double) anzahlRichtig * 100 / anzahlGesamt;

		return calculateMembershipDegree(aufgabenkategorie, percentage);
	}

	/**
	 * Berechnet den Grad der Zugehörigkeit einer Aufgabe zur gegebenen Kategorie.
	 *
	 * @param  aufgabenkategorie
	 * @param  prozentRichtigerLoesungen
	 * @return
	 */
	static double calculateMembershipDegree(final Aufgabenkategorie aufgabenkategorie, final double prozentRichtigerLoesungen) {

		double membershipDegree = calculateMembershipDegree(aufgabenkategorie.getLowerBoundOfCorrectAnswers(),
			aufgabenkategorie.getUpperOfCorrectAnswers(), prozentRichtigerLoesungen);

		return new BigDecimal(membershipDegree).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * Berechnet, wie gut eine bestimmte Aufgabe in ihre Kategorie passt.
	 *
	 * @param  lowerBound
	 * @param  upperBound
	 * @param  percentageSolved
	 * @return                  double
	 */
	private static double calculateMembershipDegree(final double lowerBound, final double upperBound, final double percentageSolved) {

		if (percentageSolved >= lowerBound && percentageSolved <= upperBound) {

			// Inside the difficulty level
			return 1.0;
		} else if (percentageSolved < lowerBound - 10 || percentageSolved > upperBound + 10) {

			// Far outside the difficulty level
			return 0.0;
		} else if (percentageSolved < lowerBound) {

			// Gradually decrease membership as percentageSolved approaches lowerBound
			return 1.0 - (lowerBound - percentageSolved) / 10.0;
		} else {

			// Gradually decrease membership as percentageSolved moves away from upperBound
			return 1.0 - (percentageSolved - upperBound) / 10.0;
		}
	}

	/**
	 * Gibt eine Schätzung für die Passung zwischen Anteil korrekter Lösungen und Aufgabenkategorie.
	 *
	 * @param  aufgabenkategorie
	 * @param  prozentRichtigerLoesungen
	 * @return
	 */
	public static Passung estimatePassung(final Aufgabenkategorie aufgabenkategorie, final double prozentRichtigerLoesungen) {

		double membershipDegree = calculateMembershipDegree(aufgabenkategorie, prozentRichtigerLoesungen);

		switch (aufgabenkategorie) {

		case A:
			if (membershipDegree < 0.7) {

				return Passung.ZU_SCHWER;
			}
			return Passung.RICHTIG;

		case B:
			if (membershipDegree >= 0.6) {

				return Passung.RICHTIG;
			}
			if (prozentRichtigerLoesungen < aufgabenkategorie.getLowerBoundOfCorrectAnswers()) {

				return Passung.ZU_SCHWER;
			}
			if (prozentRichtigerLoesungen > aufgabenkategorie.getUpperOfCorrectAnswers()) {

				return Passung.ZU_LEICHT;
			}
			throw new MkBiZaRuntimeException("Da gibt es eine selzsame Lücke: aufgabenkategorie=" + aufgabenkategorie
				+ ", membershipDegree=" + prozentRichtigerLoesungen);

		case C:
			if (membershipDegree < 0.7) {

				return Passung.ZU_LEICHT;
			}
			return Passung.RICHTIG;

		default:
			throw new IllegalArgumentException("unerwartete aufgabenkategorie " + aufgabenkategorie);
		}

	}

	/**
	 * Berechnet den Prozentsatz auf 2 Nachkommastellen gerundet.
	 *
	 * @param  anteil
	 * @param  gesamt
	 * @return
	 */
	public static double calculatePercentRoundedUpTo2Digits(final int anteil, final int gesamt) {

		double prozent = gesamt > 0 ? (double) anteil * 100 / gesamt : 0;

		return new BigDecimal(prozent).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}
}
