// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.auswertungen.StatistikTestUtils;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.statistik.impl.LoesungszettelMedianMapper;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * LoesungszettelMedianMapperTest
 */
public class LoesungszettelMedianMapperTest {

	private List<Loesungszettel> alleLoesungszettel;

	@BeforeEach
	void setUp() throws Exception {

		alleLoesungszettel = StatistikTestUtils.loadTheLoesungszettel();
	}

	@Test
	void should_apply_when_anzahlUngerade() {

		// Arrange
		List<Loesungszettel> ungeradeLoesungszettel = alleLoesungszettel.stream()
			.filter(lo -> Klassenstufe.EINS == lo.klassenstufe()).collect(Collectors.toList());

		Double expected = Double.valueOf(14.75);

		// Act
		Double actual = new LoesungszettelMedianMapper().apply(ungeradeLoesungszettel);

		// Assert
		assertEquals(expected, actual);

	}

	@Test
	void should_apply_when_anzahlGerade() {

		// Arrange
		List<Loesungszettel> geradeLoesungszettel = alleLoesungszettel.stream()
			.filter(lo -> Klassenstufe.ZWEI == lo.klassenstufe()).collect(Collectors.toList());

		Double expected = Double.valueOf(32.625);

		// Act
		Double actual = new LoesungszettelMedianMapper().apply(geradeLoesungszettel);

		// Assert
		assertEquals(expected, actual);

	}

	@Test
	void should_applyGetTheCorrectResult_when_twoEqual() {

		// Arrange
		List<Loesungszettel> zweiLoesungszettel = Arrays
			.asList(new Loesungszettel[] { new Loesungszettel().withPunkte(2000), new Loesungszettel().withPunkte(2000) });

		Double expected = Double.valueOf(20.0);

		// Act
		Double actual = new LoesungszettelMedianMapper().apply(zweiLoesungszettel);

		// Assert
		assertEquals(expected, actual);

	}

	@Test
	void should_applyGetTheCorrectResult_when_twoEqualAndThirdGreater() {

		// Arrange
		List<Loesungszettel> zweiLoesungszettel = Arrays
			.asList(new Loesungszettel[] { new Loesungszettel().withPunkte(2000), new Loesungszettel().withPunkte(2000),
				new Loesungszettel().withPunkte(3000) });

		Double expected = Double.valueOf(20.0);

		// Act
		Double actual = new LoesungszettelMedianMapper().apply(zweiLoesungszettel);

		// Assert
		assertEquals(expected, actual);
	}
}
