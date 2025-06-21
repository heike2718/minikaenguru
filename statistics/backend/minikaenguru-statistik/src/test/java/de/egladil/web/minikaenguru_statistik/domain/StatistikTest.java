// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.minikaenguru_statistik.domain.aufgaben.MkGatewayStatistikAufgabe;
import de.egladil.web.minikaenguru_statistik.domain.dto.Gruppierungsitem;
import de.egladil.web.minikaenguru_statistik.domain.klassenstufen.MkGatewayStatistikKlassenstufe;

/**
 * StatistikTest testet die korrektheit der Statistischen Daten, die ans Frontend gesendet werden.
 */
public class StatistikTest {

	private static MkGatewayStatistikKlassenstufe statistikKlassenstufe;

	@BeforeAll
	static void setUp() throws Exception {

		try (InputStream in = StatistikTest.class.getResourceAsStream("/2020-EINS-mk-gateway.json")) {

			statistikKlassenstufe = new ObjectMapper().readValue(in, MkGatewayStatistikKlassenstufe.class);
		}

	}

	@Test
	void testMedian() {

		List<Integer> anzahlen = statistikKlassenstufe.getRohpunkte().stream().map(r -> Integer.valueOf(r.getAnzahl())).toList();

		int totalStudents = 0;

		for (Integer anzahl : anzahlen) {

			totalStudents += anzahl.intValue();
		}

		double median = this.calculateTheMedian();
		double expectedMedian = statistikKlassenstufe.getMedianUndGesamtpunkte().getMedianMalTausend() / 1000;

		assertEquals(statistikKlassenstufe.getAnzahlKinderGesamt(), totalStudents);
		assertEquals(expectedMedian, median);

	}

	private double calculateTheMedian() {

		List<Integer> anzahlen = statistikKlassenstufe.getRohpunkte().stream().map(r -> Integer.valueOf(r.getAnzahl())).toList();
		List<String> punkteAsDoubleStrings = statistikKlassenstufe.getRohpunkte().stream().map(r -> r.getPunkte().replace(",", "."))
			.toList();
		List<Double> allPunkte = punkteAsDoubleStrings.stream().map(r -> Double.valueOf(r)).toList();

		List<Double> punkteMitHaufigkeit = new ArrayList<>();

		for (int index = 0; index < allPunkte.size(); index++) {

			int anzahl = anzahlen.get(index);
			Double punkte = allPunkte.get(index);

			for (int i = 0; i < anzahl; i++) {

				punkteMitHaufigkeit.add(punkte);
			}
		}

		Collections.sort(punkteMitHaufigkeit);

		double result = 0.0;

		int anzahl = punkteMitHaufigkeit.size();

		if (anzahl % 2 == 0) {

			double erstes = punkteMitHaufigkeit.get(punkteMitHaufigkeit.size() / 2 - 1);
			double zweites = punkteMitHaufigkeit.get(punkteMitHaufigkeit.size() / 2);

			result = Double.valueOf((erstes + zweites)) / 2;
		} else {

			int index = Math.floorDiv(anzahl, 2);
			result = punkteMitHaufigkeit.get(index);
		}

		return result;
	}

	@Test
	void testAufgabenProzentRichtig() {

		List<MkGatewayStatistikAufgabe> aufgabenstatistiken = statistikKlassenstufe.getAufgabenstatistiken();

		MkGatewayStatistikAufgabe aufgabe = aufgabenstatistiken.get(0);
		Optional<Gruppierungsitem> optRichtig = aufgabe.getAnzahlenJeWertungscode().stream()
			.filter(w -> "richtig gelöst".equals(w.getName())).findFirst();

		assertTrue(optRichtig.isPresent());

		Gruppierungsitem gruppierungsitem = optRichtig.get();

		double prozent = Double.valueOf(gruppierungsitem.getAnzahl()) * 100 / statistikKlassenstufe.getAnzahlKinderGesamt();

		double prozentGerundet = new BigDecimal(prozent).setScale(2, RoundingMode.HALF_UP).doubleValue();

		System.out.println("anzahl richtig: " + gruppierungsitem.getAnzahl() + ", Anzahl gesamt: "
			+ statistikKlassenstufe.getAnzahlKinderGesamt() + ", Anteil richtig = " + prozentGerundet);
	}

}
