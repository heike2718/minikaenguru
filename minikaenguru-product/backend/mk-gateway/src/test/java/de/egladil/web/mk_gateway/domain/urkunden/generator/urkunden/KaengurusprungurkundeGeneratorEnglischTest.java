// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.auswertungen.StatistikTestUtils;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.urkunden.Farbschema;
import de.egladil.web.mk_gateway.domain.urkunden.Urkundenmotiv;
import de.egladil.web.mk_gateway.domain.urkunden.daten.KaengurusprungurkundeSchuleDaten;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * KaengurusprungurkundeGeneratorEnglischTest
 */
public class KaengurusprungurkundeGeneratorEnglischTest {

	private static final String UUID = "jlsdhhl";

	@Test
	void eineUrkundeMitSchuleEinzeilig() throws Exception {

		// Arrange
		Urkundenmotiv urkundenmotiv = Urkundenmotiv.createFromFarbschema(Farbschema.ORANGE);

		Schulteilnahme schulteilnahme = new Schulteilnahme(new WettbewerbID(2020), new Identifier("GHZTZZIT"),
			"Swiss International School Basel", new Identifier("jkasdkjq"));
		Klasse klasse = new Klasse(new Identifier("sdhuqwo")).withName("2b");

		String theDatum = "26.12.2020".replaceAll("\\.", "/");

		FontSizeAndLines fontSizeAndLinesSchulname = new SplitSchulnameStrategie().getFontSizeAndLines(schulteilnahme.nameSchule());

		TeilnahmeIdentifier teilnahmeIdentifier = TeilnahmeIdentifier.createFromTeilnahme(schulteilnahme);

		Loesungszettel loesungszettel = new Loesungszettel()
			.withTeilnahmeIdentifier(teilnahmeIdentifier)
			.withIdentifier(new Identifier("L-1"))
			.withKindID(new Identifier(UUID))
			.withKlassenstufe(Klassenstufe.EINS)
			.withSprache(Sprache.en)
			.withLaengeKaengurusprung(7);

		KaengurusprungurkundeSchuleDaten datenUrkunde = new KaengurusprungurkundeSchuleDaten(loesungszettel, klasse)
			.withDatum(theDatum)
			.withFullName("Anna Logika")
			.withUrkundenmotiv(urkundenmotiv)
			.withFontsizeAndLinesSchulname(fontSizeAndLinesSchulname);

		// Act
		byte[] daten = new KaengurusprungurkundeGeneratorEnglisch().generiereUrkunde(datenUrkunde);

		// jetzt in Datei schreiben
		DownloadData downloadData = new DownloadData("name.pdf", daten);

		StatistikTestUtils.print(downloadData, true);
	}

}
