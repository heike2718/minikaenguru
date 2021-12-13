// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;

/**
 * SchulanmeldungenLandAggregatorTest
 */
public class SchulanmeldungenLandAggregatorTest {

	@Test
	void should_returnEmptyMap_when_SchulenEmpty() {

		// Arrange
		List<SchuleAPIModel> schulen = new ArrayList<>();

		// Act
		Map<String, List<SchuleAPIModel>> result = new SchulanmeldungenLandAggregator().apply(schulen);

		// Assert
		assertEquals(0, result.size());
	}

	@Test
	void should_returnMap_when_SchulenNotEmpty() {

		// Arrange
		List<SchuleAPIModel> schulen = new ArrayList<>();

		schulen.add(SchuleAPIModel.withKuerzel("A1").withLand("Hessen"));
		schulen.add(SchuleAPIModel.withKuerzel("B1").withLand("Brandenburg"));
		schulen.add(SchuleAPIModel.withKuerzel("A2").withLand("Hessen"));
		schulen.add(SchuleAPIModel.withKuerzel("A3").withLand("Hessen"));
		schulen.add(SchuleAPIModel.withKuerzel("B2").withLand("Brandenburg"));
		schulen.add(SchuleAPIModel.withKuerzel("T1").withLand("Türkei"));

		// Act
		Map<String, List<SchuleAPIModel>> result = new SchulanmeldungenLandAggregator().apply(schulen);

		// Assert
		assertEquals(3, result.size());

		List<String> laender = result.keySet().stream().collect(Collectors.toList());

		{

			String land = laender.stream().filter(l -> "Hessen".equals(l)).findFirst().get();
			assertEquals(3, result.get(land).size());
		}

		{

			String land = laender.stream().filter(l -> "Brandenburg".equals(l)).findFirst().get();
			assertEquals(2, result.get(land).size());
		}

		{

			String land = laender.stream().filter(l -> "Türkei".equals(l)).findFirst().get();
			assertEquals(1, result.get(land).size());
		}
	}

	@Test
	void should_applyThrowIllegalArgumentException_when_schulenNull() {

		try {

			new SchulanmeldungenLandAggregator().apply(null);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("schulen null", e.getMessage());
		}

	}
}
