// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.online;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRohdaten;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.LoesungszettelToAPIModelMapper;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.OnlineLoesungszetteleingabe;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.api.LoesungszettelZeileAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;

/**
 * LoesungszettelToAPIModelMapperTest
 */
public class LoesungszettelToAPIModelMapperTest {

	@Test
	void should_mapWork() {

		// Arrange
		Identifier loesungszettelID = new Identifier("haehdq");
		Identifier kindID = new Identifier("sdhafih");
		TeilnahmeIdentifier teilnahmeIdentifier = null;
		LoesungszettelRohdaten rohdaten = new LoesungszettelRohdaten().withNutzereingabe("EBCACCBDBNBN")
			.withAntwortcode("EBCACCBDBNBN");

		Loesungszettel loesungszettel = new Loesungszettel().withAuswertungsquelle(Auswertungsquelle.ONLINE)
			.withIdentifier(loesungszettelID).withKindID(kindID).withKlassenstufe(Klassenstufe.EINS)
			.withLaengeKaengurusprung(4).withPunkte(5525).withRohdaten(rohdaten).withSprache(Sprache.de)
			.withTeilnahmeIdentifier(teilnahmeIdentifier);

		// Act
		LoesungszettelAPIModel result = new LoesungszettelToAPIModelMapper().apply(loesungszettel);

		// Assert
		assertEquals("EBCACCBDBNBN", result.antwortcode());
		assertEquals(kindID.identifier(), result.kindID());
		assertEquals(Klassenstufe.EINS, result.klassenstufe());
		assertEquals(loesungszettelID.identifier(), result.uuid());

		List<LoesungszettelZeileAPIModel> zeilen = result.zeilen();
		assertEquals(12, zeilen.size());

		{

			LoesungszettelZeileAPIModel zeile = zeilen.get(0);
			assertEquals(5, zeile.anzahlSpalten());
			assertEquals(OnlineLoesungszetteleingabe.E, zeile.eingabe());
			assertEquals(0, zeile.index());
			assertEquals("A-1", zeile.name());
		}

		{

			LoesungszettelZeileAPIModel zeile = zeilen.get(1);
			assertEquals(5, zeile.anzahlSpalten());
			assertEquals(OnlineLoesungszetteleingabe.B, zeile.eingabe());
			assertEquals(1, zeile.index());
			assertEquals("A-2", zeile.name());
		}

		{

			LoesungszettelZeileAPIModel zeile = zeilen.get(2);
			assertEquals(5, zeile.anzahlSpalten());
			assertEquals(OnlineLoesungszetteleingabe.C, zeile.eingabe());
			assertEquals(2, zeile.index());
			assertEquals("A-3", zeile.name());
		}

		{

			LoesungszettelZeileAPIModel zeile = zeilen.get(3);
			assertEquals(5, zeile.anzahlSpalten());
			assertEquals(OnlineLoesungszetteleingabe.A, zeile.eingabe());
			assertEquals(3, zeile.index());
			assertEquals("A-4", zeile.name());
		}

		{

			LoesungszettelZeileAPIModel zeile = zeilen.get(4);
			assertEquals(5, zeile.anzahlSpalten());
			assertEquals(OnlineLoesungszetteleingabe.C, zeile.eingabe());
			assertEquals(4, zeile.index());
			assertEquals("B-1", zeile.name());
		}

		{

			LoesungszettelZeileAPIModel zeile = zeilen.get(5);
			assertEquals(5, zeile.anzahlSpalten());
			assertEquals(OnlineLoesungszetteleingabe.C, zeile.eingabe());
			assertEquals(5, zeile.index());
			assertEquals("B-2", zeile.name());
		}

		{

			LoesungszettelZeileAPIModel zeile = zeilen.get(6);
			assertEquals(5, zeile.anzahlSpalten());
			assertEquals(OnlineLoesungszetteleingabe.B, zeile.eingabe());
			assertEquals(6, zeile.index());
			assertEquals("B-3", zeile.name());
		}

		{

			LoesungszettelZeileAPIModel zeile = zeilen.get(7);
			assertEquals(5, zeile.anzahlSpalten());
			assertEquals(OnlineLoesungszetteleingabe.D, zeile.eingabe());
			assertEquals(7, zeile.index());
			assertEquals("B-4", zeile.name());
		}

		{

			LoesungszettelZeileAPIModel zeile = zeilen.get(8);
			assertEquals(5, zeile.anzahlSpalten());
			assertEquals(OnlineLoesungszetteleingabe.B, zeile.eingabe());
			assertEquals(8, zeile.index());
			assertEquals("C-1", zeile.name());
		}

		{

			LoesungszettelZeileAPIModel zeile = zeilen.get(9);
			assertEquals(5, zeile.anzahlSpalten());
			assertEquals(OnlineLoesungszetteleingabe.N, zeile.eingabe());
			assertEquals(9, zeile.index());
			assertEquals("C-2", zeile.name());
		}

		{

			LoesungszettelZeileAPIModel zeile = zeilen.get(10);
			assertEquals(5, zeile.anzahlSpalten());
			assertEquals(OnlineLoesungszetteleingabe.B, zeile.eingabe());
			assertEquals(10, zeile.index());
			assertEquals("C-3", zeile.name());
		}

		{

			LoesungszettelZeileAPIModel zeile = zeilen.get(11);
			assertEquals(5, zeile.anzahlSpalten());
			assertEquals(OnlineLoesungszetteleingabe.N, zeile.eingabe());
			assertEquals(11, zeile.index());
			assertEquals("C-4", zeile.name());
		}
	}

}
