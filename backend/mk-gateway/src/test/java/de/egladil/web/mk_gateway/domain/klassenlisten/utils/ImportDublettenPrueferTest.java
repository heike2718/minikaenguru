// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindRequestData;
import de.egladil.web.mk_gateway.domain.klassenlisten.KindImportVO;
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenimportZeile;
import de.egladil.web.mk_gateway.domain.klassenlisten.impl.KindImportDaten;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;

/**
 * ImportDublettenPrueferTest
 */
public class ImportDublettenPrueferTest {

	@Test
	void should_pruefeUndMarkiereDublettenImportDatenWork() {

		// Arrange
		List<KindImportVO> daten = createTestdaten();

		ImportDublettenPruefer dublettenPruefer = new ImportDublettenPruefer();

		// Act
		List<KindImportVO> result = dublettenPruefer.pruefeUndMarkiereDublettenImportDaten(daten);

		// Assert
		assertEquals(10, result.size());

		List<KindImportVO> zeilenMitDublette = result.stream().filter(z -> z.getWarnungDublette() != null)
			.collect(Collectors.toList());

		assertEquals(1, zeilenMitDublette.size());

		int indexDublette = zeilenMitDublette.get(0).getImportZeile().getIndex();
		assertEquals(8, indexDublette);
		assertEquals("Zeile 8: Amira;Kaled;2a;2: In Klasse 2a gibt es bereits ein Kind mit diesem Namen und dieser Klassenstufe",
			zeilenMitDublette.get(0).getWarnungDublette());

	}

	/**
	 * @return
	 */
	private List<KindImportVO> createTestdaten() {

		Sprache sprache = Sprache.de;

		String uuid1a = "uuid-1a";
		String uuid2a = "uuid-2a";
		String uuid2b = "uuid-2b";

		List<KindImportVO> result = new ArrayList<>();

		{

			KlassenimportZeile zeile = new KlassenimportZeile().withIndex(1)
				.withKlasse("1a").withKlassenstufe("1")
				.withNachname("Freytag").withVorname("Ralph");
			zeile.setImportRohdaten("Ralph;Freytag;1a;1");

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.EINS, sprache).withNachname("Freytag")
				.withVorname("Ralph").withKlasseUuid(uuid1a);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);

			result.add(new KindImportVO(zeile, daten));
		}

		{

			KlassenimportZeile zeile = new KlassenimportZeile().withIndex(2)
				.withKlasse("1a").withKlassenstufe("1")
				.withNachname("Bakir").withVorname("Özcan");
			zeile.setImportRohdaten("Özcan;Bakir;1a;1");

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.EINS, sprache).withVorname("Bakir")
				.withVorname("Özcan").withKlasseUuid(uuid1a);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);

			result.add(new KindImportVO(zeile, daten));
		}

		{

			KlassenimportZeile zeile = new KlassenimportZeile().withIndex(3)
				.withKlasse("1a").withKlassenstufe("2")
				.withNachname("Fassbinder").withVorname("Katja");
			zeile.setImportRohdaten("Katja;Fassbinder;1a;1");

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, sprache).withVorname("Katja")
				.withNachname("Fassbinder").withKlasseUuid(uuid1a);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);

			result.add(new KindImportVO(zeile, daten));
		}

		{

			KlassenimportZeile zeile = new KlassenimportZeile().withIndex(4)
				.withKlasse("2a").withKlassenstufe("0")
				.withNachname("Schönack").withVorname("Benedikt");
			zeile.setImportRohdaten("Benedikt;Schönack;2a;0");

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.IKID, sprache).withVorname("Benedikt")
				.withNachname("Schönack").withKlasseUuid(uuid2a);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);

			result.add(new KindImportVO(zeile, daten));
		}

		{

			KlassenimportZeile zeile = new KlassenimportZeile().withIndex(5)
				.withKlasse("2b").withKlassenstufe("2")
				.withNachname("Bakir").withVorname("Özcan");
			zeile.setImportRohdaten("Özcan;Bakir;2b;2");

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, sprache).withNachname("Bakir")
				.withVorname("Özcan").withKlasseUuid(uuid2b);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);

			result.add(new KindImportVO(zeile, daten));
		}

		{

			KlassenimportZeile zeile = new KlassenimportZeile().withIndex(6)
				.withKlasse("2a").withKlassenstufe("2")
				.withNachname("Kaled").withVorname("Amira");
			zeile.setImportRohdaten("Amira;Kaled;2a;2");

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, sprache).withNachname("Kaled")
				.withVorname("Amira").withKlasseUuid(uuid2a);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);

			result.add(new KindImportVO(zeile, daten));
		}

		{

			KlassenimportZeile zeile = new KlassenimportZeile().withIndex(7)
				.withKlasse("2b").withKlassenstufe("2")
				.withNachname("Grüter").withVorname("Thomas");
			zeile.setImportRohdaten("Thomas;Grüter;2b;2");

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, sprache).withVorname("Thomas")
				.withNachname("Grüter").withKlasseUuid(uuid2b);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);

			result.add(new KindImportVO(zeile, daten));
		}

		{

			KlassenimportZeile zeile = new KlassenimportZeile().withIndex(8)
				.withKlasse("2a").withKlassenstufe("2")
				.withNachname("Kaled").withVorname("Amira");
			zeile.setImportRohdaten("Amira;Kaled;2a;2");

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, sprache).withNachname("Kaled")
				.withVorname("Amira").withKlasseUuid(uuid2a);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);

			result.add(new KindImportVO(zeile, daten));
		}

		{

			KlassenimportZeile zeile = new KlassenimportZeile().withIndex(9)
				.withKlasse("2b").withKlassenstufe("2")
				.withNachname("Log").withVorname("Analena");
			zeile.setImportRohdaten("Analena;Log;2b;2");

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, sprache).withVorname("Analena")
				.withNachname("Log").withKlasseUuid(uuid2b);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);

			result.add(new KindImportVO(zeile, daten));
		}

		{

			KlassenimportZeile zeile = new KlassenimportZeile().withIndex(10)
				.withKlasse("2b").withKlassenstufe("2")
				.withNachname("Admin").withVorname("Ruth");
			zeile.setImportRohdaten("Ruth;Admin;2b;2");

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, sprache).withVorname("Ruth")
				.withNachname("Admin").withKlasseUuid(uuid2b);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);

			result.add(new KindImportVO(zeile, daten));
		}

		return result;
	}

}
