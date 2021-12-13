// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * LoesungszettelLandAggregatorTest
 */
public class LoesungszettelLandAggregatorTest {

	@Test
	void should_applyThrowIllegalArgumentException_when_loesungszettelNull() {

		// Arrange
		List<Loesungszettel> loesungszettel = null;
		List<SchuleAPIModel> schulen = new ArrayList<>();

		// Act
		try {

			new LoesungszettelLandAggregator().apply(loesungszettel, schulen);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("loesungszettel null", e.getMessage());
		}

	}

	@Test
	void should_applyThrowIllegalArgumentException_when_schulenNull() {

		// Arrange
		List<Loesungszettel> loesungszettel = new ArrayList<>();
		List<SchuleAPIModel> schulen = null;

		// Act
		try {

			new LoesungszettelLandAggregator().apply(loesungszettel, schulen);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("schulen null", e.getMessage());
		}

	}

	@Test
	void should_apply_returnMapWithLoeusngszettelnLaendernFromSchulen() {

		// Arrange
		WettbewerbID wettbewerbID = new WettbewerbID(2020);
		Teilnahmeart teilnahmeart = Teilnahmeart.SCHULE;

		List<Loesungszettel> loesungszettel = new ArrayList<>();

		loesungszettel.add(new Loesungszettel().withIdentifier(new Identifier("UUID-A1-1"))
			.withTeilnahmeIdentifier(new TeilnahmeIdentifier().withTeilnahmeart(teilnahmeart).withWettbewerbID(wettbewerbID)
				.withTeilnahmenummer("A1")));
		loesungszettel.add(new Loesungszettel().withIdentifier(new Identifier("UUID-A1-2"))
			.withTeilnahmeIdentifier(new TeilnahmeIdentifier().withTeilnahmeart(teilnahmeart).withWettbewerbID(wettbewerbID)
				.withTeilnahmenummer("A1")));

		loesungszettel.add(new Loesungszettel().withIdentifier(new Identifier("UUID-A3-1"))
			.withTeilnahmeIdentifier(new TeilnahmeIdentifier().withTeilnahmeart(teilnahmeart).withWettbewerbID(wettbewerbID)
				.withTeilnahmenummer("A3")));

		loesungszettel.add(new Loesungszettel().withIdentifier(new Identifier("UUID-B1-1"))
			.withTeilnahmeIdentifier(new TeilnahmeIdentifier().withTeilnahmeart(teilnahmeart).withWettbewerbID(wettbewerbID)
				.withTeilnahmenummer("B1")));

		loesungszettel.add(new Loesungszettel().withIdentifier(new Identifier("UUID-B1-2"))
			.withTeilnahmeIdentifier(new TeilnahmeIdentifier().withTeilnahmeart(teilnahmeart).withWettbewerbID(wettbewerbID)
				.withTeilnahmenummer("B1")));

		loesungszettel.add(new Loesungszettel().withIdentifier(new Identifier("UUID-B2-1"))
			.withTeilnahmeIdentifier(new TeilnahmeIdentifier().withTeilnahmeart(teilnahmeart).withWettbewerbID(wettbewerbID)
				.withTeilnahmenummer("B2")));

		loesungszettel.add(new Loesungszettel().withIdentifier(new Identifier("UUID-B2-2"))
			.withTeilnahmeIdentifier(new TeilnahmeIdentifier().withTeilnahmeart(teilnahmeart).withWettbewerbID(wettbewerbID)
				.withTeilnahmenummer("B2")));

		List<SchuleAPIModel> schulen = new ArrayList<>();

		schulen.add(SchuleAPIModel.withKuerzel("A1").withLand("Hessen"));
		schulen.add(SchuleAPIModel.withKuerzel("B1").withLand("Brandenburg"));
		schulen.add(SchuleAPIModel.withKuerzel("A2").withLand("Hessen"));
		schulen.add(SchuleAPIModel.withKuerzel("A3").withLand("Hessen"));
		schulen.add(SchuleAPIModel.withKuerzel("B2").withLand("Brandenburg"));
		schulen.add(SchuleAPIModel.withKuerzel("T1").withLand("Türkei"));

		// Act
		Map<String, List<Loesungszettel>> result = new LoesungszettelLandAggregator().apply(loesungszettel, schulen);

		// Assert
		assertEquals(2, result.size());
		assertEquals(3, result.get("Hessen").size());
		assertEquals(4, result.get("Brandenburg").size());
		assertNull(result.get("Türkei"));
	}

}
