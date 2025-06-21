// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.online;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRohdaten;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.LoesungszettelCreator;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;

/**
 * LoesungszettelCreatorTest
 */
public class LoesungszettelCreatorTest extends AbstractLoesungszettelServiceTest {

	@BeforeEach
	void setUp() {

		super.init();
	}

	@Test
	void should_createSetVersionZero_when_neuerLoesungszettel() {

		// Arrange
		LoesungszettelAPIModel loesungszetteldaten = new LoesungszettelAPIModel().withKindID(REQUEST_KIND_ID.identifier())
			.withKlassenstufe(Klassenstufe.EINS).withUuid(LoesungszettelAPIModel.KEINE_UUID).withVersion(-1)
			.withZeilen(createLoesungszettelZeilen());

		Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID);

		// Act
		Loesungszettel result = new LoesungszettelCreator().createLoesungszettel(loesungszetteldaten, aktuellerWettbewerb, kind);

		// Assert
		assertEquals(0, result.version());
		assertNull(result.identifier());
		assertEquals(REQUEST_KIND_ID, result.kindID());
		assertEquals(Auswertungsquelle.ONLINE, result.auswertungsquelle());
		assertEquals(Klassenstufe.EINS, result.klassenstufe());
		assertEquals(1, result.laengeKaengurusprung());
		assertEquals(625, result.punkte());
		assertEquals("DE-HE", result.landkuerzel());
		LoesungszettelRohdaten rohdaten = result.rohdaten();
		assertEquals("AAAABBBBCCCC", rohdaten.antwortcode());
		assertEquals("AAAABBBBCCCC", rohdaten.nutzereingabe());
		assertEquals("fffffffffrff", rohdaten.wertungscode());

		TeilnahmeIdentifier teilnahmeIdentifier = result.teilnahmeIdentifier();
		assertEquals(Teilnahmeart.SCHULE, teilnahmeIdentifier.teilnahmeart());
		assertEquals("FFFUFGFT76", teilnahmeIdentifier.teilnahmenummer());
		assertEquals(2021, teilnahmeIdentifier.jahr());

	}

	@Test
	void should_createCopyVersionFromRquest_when_vorhandenerLoesungszettel() {

		// Arrange
		LoesungszettelAPIModel loesungszetteldaten = new LoesungszettelAPIModel().withKindID(REQUEST_KIND_ID.identifier())
			.withKlassenstufe(Klassenstufe.EINS).withUuid(REQUEST_LOESUNGSZETTEL_ID.identifier()).withVersion(4)
			.withZeilen(createLoesungszettelZeilen());

		int expectedVersion = 4;

		Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID);

		// Act
		Loesungszettel result = new LoesungszettelCreator().createLoesungszettel(loesungszetteldaten, aktuellerWettbewerb, kind);

		// Assert
		assertEquals(expectedVersion, result.version());
		assertNull(result.identifier());
		assertEquals(REQUEST_KIND_ID, result.kindID());
		assertEquals(Auswertungsquelle.ONLINE, result.auswertungsquelle());
		assertEquals(Klassenstufe.EINS, result.klassenstufe());
		assertEquals(1, result.laengeKaengurusprung());
		assertEquals(625, result.punkte());
		assertEquals("DE-HE", result.landkuerzel());
		LoesungszettelRohdaten rohdaten = result.rohdaten();
		assertEquals("AAAABBBBCCCC", rohdaten.antwortcode());
		assertEquals("AAAABBBBCCCC", rohdaten.nutzereingabe());
		assertEquals("fffffffffrff", rohdaten.wertungscode());

		TeilnahmeIdentifier teilnahmeIdentifier = result.teilnahmeIdentifier();
		assertEquals(Teilnahmeart.SCHULE, teilnahmeIdentifier.teilnahmeart());
		assertEquals("FFFUFGFT76", teilnahmeIdentifier.teilnahmenummer());
		assertEquals(2021, teilnahmeIdentifier.jahr());

	}

}
