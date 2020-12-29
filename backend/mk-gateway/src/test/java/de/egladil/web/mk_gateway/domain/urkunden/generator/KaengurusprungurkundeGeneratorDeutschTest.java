// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.auswertungen.StatistikTestUtils;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.urkunden.Farbschema;
import de.egladil.web.mk_gateway.domain.urkunden.Urkundenmotiv;
import de.egladil.web.mk_gateway.domain.urkunden.daten.TeilnahmeurkundeSchuleDaten;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * KaengurusprungurkundeGeneratorDeutschTest
 */
public class KaengurusprungurkundeGeneratorDeutschTest {

	@Test
	void eineUrkundeMitSchuleEinzeilig() throws Exception {

		// Arrange
		Urkundenmotiv urkundenmotiv = Urkundenmotiv.createFromFarbschema(Farbschema.BLUE);

		Schulteilnahme schulteilnahme = new Schulteilnahme(new WettbewerbID(2020), new Identifier("GHZTZZIT"),
			"Grundschule \"Johann Wolfgang von Goethe\"", new Identifier("jkasdkjq"));
		Klasse klasse = new Klasse(new Identifier("sdhuqwo")).withName("Klasse 2b");

		FontSizeAndLines fontSizeAndLinesSchulname = new SplitSchulnameStrategie().getFontSizeAndLines(schulteilnahme.nameSchule());

		TeilnahmeurkundeSchuleDaten datenUrkunde = new TeilnahmeurkundeSchuleDaten("7", klasse)
			.withDatum("26.12.2020")
			.withFullName("Anna Logika")
			.withUrkundenmotiv(urkundenmotiv)
			.withWettbewerbsjahr("2020")
			.withFontsizeAndLinesSchulname(fontSizeAndLinesSchulname);

		// Act
		byte[] daten = new KaengurusprungurkundeGeneratorDeutsch().generiereUrkunde(datenUrkunde);

		// jetzt in Datei schreiben
		DownloadData downloadData = new DownloadData("name.pdf", daten);

		StatistikTestUtils.print(downloadData, true);
	}

}
