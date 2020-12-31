// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.auswertungen.StatistikTestUtils;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Privatteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.urkunden.Farbschema;
import de.egladil.web.mk_gateway.domain.urkunden.Urkundenmotiv;
import de.egladil.web.mk_gateway.domain.urkunden.daten.TeilnahmeurkundePrivatDaten;
import de.egladil.web.mk_gateway.domain.urkunden.daten.TeilnahmeurkundeSchuleDaten;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * TeilnahmeurkundeGeneratorDeutschTest
 */
public class TeilnahmeurkundeGeneratorDeutschTest {

	private static final String UUID = "jlsdhhl";

	@Nested
	class TeilnahmeurkundeSchulenTests {

		@Test
		void eineUrkundeMitSchuleEinzeilig() throws Exception {

			// Arrange
			Urkundenmotiv urkundenmotiv = Urkundenmotiv.createFromFarbschema(Farbschema.BLUE);

			Schulteilnahme schulteilnahme = new Schulteilnahme(new WettbewerbID(2020), new Identifier("GHZTZZIT"),
				"Grundschule \"Johann Wolfgang von Goethe\"", new Identifier("jkasdkjq"));
			Klasse klasse = new Klasse(new Identifier("sdhuqwo")).withName("Klasse 2b");

			FontSizeAndLines fontSizeAndLinesSchulname = new SplitSchulnameStrategie()
				.getFontSizeAndLines(schulteilnahme.nameSchule());

			TeilnahmeIdentifier teilnahmeIdentifier = TeilnahmeIdentifier.createFromTeilnahme(schulteilnahme);

			Loesungszettel loesungszettel = new Loesungszettel()
				.withTeilnahmeIdentifier(teilnahmeIdentifier)
				.withIdentifier(new Identifier("L-1"))
				.withKindID(new Identifier(UUID))
				.withKlassenstufe(Klassenstufe.EINS)
				.withSprache(Sprache.de)
				.withPunkte(4247);

			TeilnahmeurkundeSchuleDaten datenUrkunde = new TeilnahmeurkundeSchuleDaten(loesungszettel, klasse)
				.withDatum("26.12.2020")
				.withFullName("Anna Logika")
				.withUrkundenmotiv(urkundenmotiv)
				.withFontsizeAndLinesSchulname(fontSizeAndLinesSchulname);

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

			FontSizeAndLines fontSizeAndLinesSchulname = new SplitSchulnameStrategie()
				.getFontSizeAndLines(schulteilnahme.nameSchule());

			TeilnahmeIdentifier teilnahmeIdentifier = TeilnahmeIdentifier.createFromTeilnahme(schulteilnahme);

			Loesungszettel loesungszettel = new Loesungszettel()
				.withTeilnahmeIdentifier(teilnahmeIdentifier)
				.withIdentifier(new Identifier("L-1"))
				.withKindID(new Identifier(UUID))
				.withKlassenstufe(Klassenstufe.EINS)
				.withSprache(Sprache.de)
				.withPunkte(3875);

			TeilnahmeurkundeSchuleDaten datenUrkunde = new TeilnahmeurkundeSchuleDaten(loesungszettel, klasse)
				.withDatum("26.12.2020")
				.withFullName("Karl mit einem Nachnamen der gerade noch so passt indem umgebrochen wird")
				.withUrkundenmotiv(urkundenmotiv)
				.withFontsizeAndLinesSchulname(fontSizeAndLinesSchulname);

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

			FontSizeAndLines fontSizeAndLinesSchulname = new SplitSchulnameStrategie()
				.getFontSizeAndLines(schulteilnahme.nameSchule());

			TeilnahmeIdentifier teilnahmeIdentifier = TeilnahmeIdentifier.createFromTeilnahme(schulteilnahme);

			Loesungszettel loesungszettel = new Loesungszettel()
				.withTeilnahmeIdentifier(teilnahmeIdentifier)
				.withIdentifier(new Identifier("L-1"))
				.withKindID(new Identifier(UUID))
				.withKlassenstufe(Klassenstufe.EINS)
				.withSprache(Sprache.de)
				.withPunkte(3875);

			TeilnahmeurkundeSchuleDaten datenUrkunde = new TeilnahmeurkundeSchuleDaten(loesungszettel, klasse)
				.withDatum("26.12.2020")
				.withFullName("Karl Theodor zu Guttenberg Kuckucksheim")
				.withUrkundenmotiv(urkundenmotiv)
				.withFontsizeAndLinesSchulname(fontSizeAndLinesSchulname);

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
				"Grundschule an der Wilhemstraße Gemeinschaftsschule der Primarstufe für mathematisch minderbegante KinderDatenTeilnahmeurkundenMapper",
				new Identifier("jkasdkjq"));
			Klasse klasse = new Klasse(new Identifier("sdhuqwo")).withName("Klasse 2b");

			FontSizeAndLines fontSizeAndLinesSchulname = new SplitSchulnameStrategie()
				.getFontSizeAndLines(schulteilnahme.nameSchule());

			TeilnahmeIdentifier teilnahmeIdentifier = TeilnahmeIdentifier.createFromTeilnahme(schulteilnahme);

			Loesungszettel loesungszettel = new Loesungszettel()
				.withTeilnahmeIdentifier(teilnahmeIdentifier)
				.withIdentifier(new Identifier("L-1"))
				.withKindID(new Identifier(UUID))
				.withKlassenstufe(Klassenstufe.EINS)
				.withSprache(Sprache.de)
				.withPunkte(1375);

			TeilnahmeurkundeSchuleDaten datenUrkunde = new TeilnahmeurkundeSchuleDaten(loesungszettel, klasse)
				.withDatum("26.12.2020")
				.withFullName(
					"Maximilian Konstantin Pfennigfuchser Trostel beim besten Willen nicht nicht auf die Urkunde passen wird")
				.withUrkundenmotiv(urkundenmotiv)
				.withFontsizeAndLinesSchulname(fontSizeAndLinesSchulname);

			// Act
			byte[] daten = new TeilnahmeurkundeGeneratorDeutsch().generiereUrkunde(datenUrkunde);

			// jetzt in Datei schreiben
			DownloadData downloadData = new DownloadData("name.pdf", daten);

			StatistikTestUtils.print(downloadData, true);
		}

	}

	@Nested
	class TeilnahmeurkundePrivatTests {

		private Privatteilnahme privatteilnahme = new Privatteilnahme(new WettbewerbID(2020), new Identifier("ABCDEFGHIJ"));

		@Test
		void eineUrkundeMitLangemKindernamenZweizeilig() throws Exception {

			// Arrange
			Urkundenmotiv urkundenmotiv = Urkundenmotiv.createFromFarbschema(Farbschema.ORANGE);

			TeilnahmeIdentifier teilnahmeIdentifier = TeilnahmeIdentifier.createFromTeilnahme(privatteilnahme);

			Loesungszettel loesungszettel = new Loesungszettel()
				.withTeilnahmeIdentifier(teilnahmeIdentifier)
				.withIdentifier(new Identifier("L-1"))
				.withKindID(new Identifier(UUID))
				.withKlassenstufe(Klassenstufe.ZWEI)
				.withSprache(Sprache.de)
				.withPunkte(1375);

			TeilnahmeurkundePrivatDaten datenUrkunde = new TeilnahmeurkundePrivatDaten(loesungszettel)
				.withDatum("26.12.2020")
				.withFullName("Karl mit einem Nachnamen der gerade noch so passt wenn umgebrochen wird")
				.withUrkundenmotiv(urkundenmotiv);

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

			TeilnahmeIdentifier teilnahmeIdentifier = TeilnahmeIdentifier.createFromTeilnahme(privatteilnahme);

			Loesungszettel loesungszettel = new Loesungszettel()
				.withTeilnahmeIdentifier(teilnahmeIdentifier)
				.withIdentifier(new Identifier("L-1"))
				.withKindID(new Identifier(UUID))
				.withKlassenstufe(Klassenstufe.ZWEI)
				.withSprache(Sprache.de)
				.withPunkte(1375);

			TeilnahmeurkundePrivatDaten datenUrkunde = new TeilnahmeurkundePrivatDaten(loesungszettel)
				.withDatum("26.12.2020")
				.withFullName("Karl Theodor zu Guttenberg Kuckucksheim")
				.withUrkundenmotiv(urkundenmotiv);

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

			TeilnahmeIdentifier teilnahmeIdentifier = TeilnahmeIdentifier.createFromTeilnahme(privatteilnahme);

			Loesungszettel loesungszettel = new Loesungszettel()
				.withTeilnahmeIdentifier(teilnahmeIdentifier)
				.withIdentifier(new Identifier("L-1"))
				.withKindID(new Identifier(UUID))
				.withKlassenstufe(Klassenstufe.ZWEI)
				.withSprache(Sprache.de)
				.withPunkte(1375);

			TeilnahmeurkundePrivatDaten datenUrkunde = new TeilnahmeurkundePrivatDaten(loesungszettel)
				.withDatum("26.12.2020")
				.withFullName(
					"Maximilian Konstantin Pfennigfuchser Trostel beim besten Willen nicht nicht auf die Urkunde passen wird")
				.withUrkundenmotiv(urkundenmotiv);

			// Act
			byte[] daten = new TeilnahmeurkundeGeneratorDeutsch().generiereUrkunde(datenUrkunde);

			// jetzt in Datei schreiben
			DownloadData downloadData = new DownloadData("name.pdf", daten);

			StatistikTestUtils.print(downloadData, true);
		}

	}

}
