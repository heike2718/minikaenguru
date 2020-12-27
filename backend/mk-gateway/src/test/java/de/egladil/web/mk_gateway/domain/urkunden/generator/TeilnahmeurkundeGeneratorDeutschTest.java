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
 * TeilnahmeurkundeGeneratorDeutschTest
 */
public class TeilnahmeurkundeGeneratorDeutschTest {

	@Test
	void eineUrkundeMitSchuleEinzeilig() throws Exception {

		// Arrange
		Urkundenmotiv urkundenmotiv = Urkundenmotiv.createFromFarbschema(Farbschema.BLUE);

		Schulteilnahme schulteilnahme = new Schulteilnahme(new WettbewerbID(2020), new Identifier("GHZTZZIT"),
			"Grundschule \"Johann Wolfgang von Goethe\"", new Identifier("jkasdkjq"));
		Klasse klasse = new Klasse(new Identifier("sdhuqwo")).withName("Eichelhäher");

		TeilnahmeurkundeSchuleDaten datenUrkunde = new TeilnahmeurkundeSchuleDaten("38,75", schulteilnahme, klasse)
			.withDatum("26.12.2020")
			.withFullName("Anna Bolika")
			.withUrkundenmotiv(urkundenmotiv)
			.withWettbewerbsjahr("2020");

		// Act
		byte[] daten = new TeilnahmeurkundeGeneratorDeutsch().generiereUrkunde(datenUrkunde);

		// jetzt in Datei schreiben
		DownloadData downloadData = new DownloadData("name.pdf", daten);

		StatistikTestUtils.print(downloadData, true);
	}

	@Test
	void eineUrkundeMitSchuleZweizeilig() throws Exception {

		// Arrange
		Urkundenmotiv urkundenmotiv = Urkundenmotiv.createFromFarbschema(Farbschema.BLUE);

		Schulteilnahme schulteilnahme = new Schulteilnahme(new WettbewerbID(2020), new Identifier("GHZTZZIT"),
			"Grundschule an der Georgstraße Städtische Gemeinschaftsschule der Primarstufe", new Identifier("jkasdkjq"));
		Klasse klasse = new Klasse(new Identifier("sdhuqwo")).withName("Eichelhäher");

		TeilnahmeurkundeSchuleDaten datenUrkunde = new TeilnahmeurkundeSchuleDaten("38,75", schulteilnahme, klasse)
			.withDatum("26.12.2020")
			.withFullName("Anna Bolika")
			.withUrkundenmotiv(urkundenmotiv)
			.withWettbewerbsjahr("2020");

		// Act
		byte[] daten = new TeilnahmeurkundeGeneratorDeutsch().generiereUrkunde(datenUrkunde);

		// jetzt in Datei schreiben
		DownloadData downloadData = new DownloadData("name.pdf", daten);

		StatistikTestUtils.print(downloadData, true);
	}

	@Test
	void eineUrkundeMitLangemKindernamenUndSchuleZweizeilig() throws Exception {

		// Arrange
		Urkundenmotiv urkundenmotiv = Urkundenmotiv.createFromFarbschema(Farbschema.BLUE);

		Schulteilnahme schulteilnahme = new Schulteilnahme(new WettbewerbID(2020), new Identifier("GHZTZZIT"),
			"Grundschule an der Georgstraße Städtische Gemeinschaftsschule der Primarstufe", new Identifier("jkasdkjq"));
		Klasse klasse = new Klasse(new Identifier("sdhuqwo")).withName("Eichelhäher");

		TeilnahmeurkundeSchuleDaten datenUrkunde = new TeilnahmeurkundeSchuleDaten("13,75", schulteilnahme, klasse)
			.withDatum("26.12.2020")
			.withFullName("Karl Theodor zu Guttenberg Kuckucksheim")
			.withUrkundenmotiv(urkundenmotiv)
			.withWettbewerbsjahr("2020");

		// Act
		byte[] daten = new TeilnahmeurkundeGeneratorDeutsch().generiereUrkunde(datenUrkunde);

		// jetzt in Datei schreiben
		DownloadData downloadData = new DownloadData("name.pdf", daten);

		StatistikTestUtils.print(downloadData, true);
	}

	@Test
	void eineUrkundeMitZuLangemKindernamenUndZuLangemSchulnamen() throws Exception {

		// Arrange
		Urkundenmotiv urkundenmotiv = Urkundenmotiv.createFromFarbschema(Farbschema.BLUE);

		Schulteilnahme schulteilnahme = new Schulteilnahme(new WettbewerbID(2020), new Identifier("GHZTZZIT"),
			"Grundschule an der Georgstraße Städtische Gemeinschaftsschule der Primarstufe für mathematisch minderbegante Kinder",
			new Identifier("jkasdkjq"));
		Klasse klasse = new Klasse(new Identifier("sdhuqwo")).withName("Eichelhäher");

		TeilnahmeurkundeSchuleDaten datenUrkunde = new TeilnahmeurkundeSchuleDaten("13,75", schulteilnahme, klasse)
			.withDatum("26.12.2020")
			.withFullName("Maximilian Konstantin Pfennigfuchser Trostel beim besten Willen nicht nicht auf die Urkunde passen wird")
			.withUrkundenmotiv(urkundenmotiv)
			.withWettbewerbsjahr("2020");

		// Act
		byte[] daten = new TeilnahmeurkundeGeneratorDeutsch().generiereUrkunde(datenUrkunde);

		// jetzt in Datei schreiben
		DownloadData downloadData = new DownloadData("name.pdf", daten);

		StatistikTestUtils.print(downloadData, false);
	}

}
