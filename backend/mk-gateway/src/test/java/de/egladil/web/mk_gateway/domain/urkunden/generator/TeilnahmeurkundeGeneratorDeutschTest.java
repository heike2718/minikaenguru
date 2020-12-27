// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.auswertungen.StatistikTestUtils;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.urkunden.Farbschema;
import de.egladil.web.mk_gateway.domain.urkunden.Urkundenmotiv;
import de.egladil.web.mk_gateway.domain.urkunden.daten.TeilnahmeurkundePrivatDaten;
import de.egladil.web.mk_gateway.domain.urkunden.daten.TeilnahmeurkundeSchuleDaten;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * TeilnahmeurkundeGeneratorDeutschTest
 */
public class TeilnahmeurkundeGeneratorDeutschTest {

	@Nested
	class TeilnahmeurkundeSchulenTests {

		@Test
		void eineUrkundeMitSchuleEinzeilig() throws Exception {

			// Arrange
			Urkundenmotiv urkundenmotiv = Urkundenmotiv.createFromFarbschema(Farbschema.BLUE);

			Schulteilnahme schulteilnahme = new Schulteilnahme(new WettbewerbID(2020), new Identifier("GHZTZZIT"),
				"Grundschule \"Johann Wolfgang von Goethe\"", new Identifier("jkasdkjq"));
			Klasse klasse = new Klasse(new Identifier("sdhuqwo")).withName("Klasse 2b");

			TeilnahmeurkundeSchuleDaten datenUrkunde = new TeilnahmeurkundeSchuleDaten("42,75", schulteilnahme, klasse)
				.withDatum("26.12.2020")
				.withFullName("Anna Logika")
				.withUrkundenmotiv(urkundenmotiv)
				.withWettbewerbsjahr("2020");

			// Act
			byte[] daten = new TeilnahmeurkundeGeneratorDeutsch().generiereUrkunde(datenUrkunde);

			// jetzt in Datei schreiben
			DownloadData downloadData = new DownloadData("name.pdf", daten);

			StatistikTestUtils.print(downloadData, true);
		}

		@Test
		void eineUrkundeMitKindernameUndSchulnameZweizeilig() throws Exception {

			// Arrange
			Urkundenmotiv urkundenmotiv = Urkundenmotiv.createFromFarbschema(Farbschema.BLUE);

			Schulteilnahme schulteilnahme = new Schulteilnahme(new WettbewerbID(2020), new Identifier("GHZTZZIT"),
				"Grundschule an der Georgstraße Städtische Gemeinschaftsschule der Primarstufe", new Identifier("jkasdkjq"));
			Klasse klasse = new Klasse(new Identifier("sdhuqwo")).withName("Klasse 2b");

			TeilnahmeurkundeSchuleDaten datenUrkunde = new TeilnahmeurkundeSchuleDaten("38,75", schulteilnahme, klasse)
				.withDatum("26.12.2020")
				.withFullName("Karl mit einem Nachnamen der gerade noch so passt indem umgebrochen wird")
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
				"Grundschule an der Wilhemstraße Gemeinschaftsschule der Primarstufe für mathematisch minderbegante Kinder",
				new Identifier("jkasdkjq"));
			Klasse klasse = new Klasse(new Identifier("sdhuqwo")).withName("Klasse 2b");

			TeilnahmeurkundeSchuleDaten datenUrkunde = new TeilnahmeurkundeSchuleDaten("13,75", schulteilnahme, klasse)
				.withDatum("26.12.2020")
				.withFullName(
					"Maximilian Konstantin Pfennigfuchser Trostel beim besten Willen nicht nicht auf die Urkunde passen wird")
				.withUrkundenmotiv(urkundenmotiv)
				.withWettbewerbsjahr("2020");

			// Act
			byte[] daten = new TeilnahmeurkundeGeneratorDeutsch().generiereUrkunde(datenUrkunde);

			// jetzt in Datei schreiben
			DownloadData downloadData = new DownloadData("name.pdf", daten);

			StatistikTestUtils.print(downloadData, true);
		}

	}

	@Nested
	class TeilnahmeurkundePrivatTests {

		@Test
		void eineUrkundeMitLangemKindernamenZweizeilig() throws Exception {

			// Arrange
			Urkundenmotiv urkundenmotiv = Urkundenmotiv.createFromFarbschema(Farbschema.ORANGE);

			TeilnahmeurkundePrivatDaten datenUrkunde = new TeilnahmeurkundePrivatDaten("13,75", Klassenstufe.ZWEI)
				.withDatum("26.12.2020")
				.withFullName("Karl mit einem Nachnamen der gerade noch so passt wenn umgebrochen wird")
				.withUrkundenmotiv(urkundenmotiv)
				.withWettbewerbsjahr("2020");

			// Act
			byte[] daten = new TeilnahmeurkundeGeneratorDeutsch().generiereUrkunde(datenUrkunde);

			// jetzt in Datei schreiben
			DownloadData downloadData = new DownloadData("name.pdf", daten);

			StatistikTestUtils.print(downloadData, true);
		}

		@Test
		void eineUrkundeMitLangemKindernamenEinzeilig() throws Exception {

			// Arrange
			Urkundenmotiv urkundenmotiv = Urkundenmotiv.createFromFarbschema(Farbschema.ORANGE);

			TeilnahmeurkundePrivatDaten datenUrkunde = new TeilnahmeurkundePrivatDaten("13,75", Klassenstufe.ZWEI)
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
		void eineUrkundeMitZuLangemKindernamen() throws Exception {

			// Arrange
			Urkundenmotiv urkundenmotiv = Urkundenmotiv.createFromFarbschema(Farbschema.ORANGE);

			TeilnahmeurkundePrivatDaten datenUrkunde = new TeilnahmeurkundePrivatDaten("13,75", Klassenstufe.ZWEI)
				.withDatum("26.12.2020")
				.withFullName(
					"Maximilian Konstantin Pfennigfuchser Trostel beim besten Willen nicht nicht auf die Urkunde passen wird")
				.withUrkundenmotiv(urkundenmotiv)
				.withWettbewerbsjahr("2020");

			// Act
			byte[] daten = new TeilnahmeurkundeGeneratorDeutsch().generiereUrkunde(datenUrkunde);

			// jetzt in Datei schreiben
			DownloadData downloadData = new DownloadData("name.pdf", daten);

			StatistikTestUtils.print(downloadData, true);
		}

	}

}
