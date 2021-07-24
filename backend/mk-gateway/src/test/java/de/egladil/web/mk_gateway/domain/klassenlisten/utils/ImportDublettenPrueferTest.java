// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten.utils;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindRequestData;
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
		List<KindImportDaten> importDaten = this.createKindImportDaten();
		ImportDublettenPruefer dublettenPruefer = new ImportDublettenPruefer();

		// Act
		int anzahlDubletten = dublettenPruefer.pruefeUndMarkiereDublettenImportDaten(importDaten);

		// Assert
		assertEquals(2, anzahlDubletten);

		for (KindImportDaten id : importDaten) {

			if ("Amira".equals(id.getKindRequestData().kind().vorname())) {

				assertEquals("hatten TRUE erwartet bei " + id.getKindRequestData().toString(), Boolean.TRUE,
					Boolean.valueOf(id.isDublettePruefen()));
			} else {

				assertEquals("hatten FALSE erwartet bei " + id.getKindRequestData().toString(), Boolean.FALSE,
					Boolean.valueOf(id.isDublettePruefen()));
			}

		}

	}

	private List<KindImportDaten> createKindImportDaten() {

		Sprache sprache = Sprache.de;

		String uuid1a = "uuid-1a";
		String uuid2a = "uuid-2a";
		String uuid2b = "uuid-2b";

		List<KindImportDaten> importDaten = new ArrayList<>();

		{

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.EINS, sprache).withNachname("Freytag")
				.withVorname("Ralph").withKlasseUuid(uuid1a);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);
			importDaten.add(daten);
		}

		{

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.EINS, sprache).withNachname("Bakir")
				.withVorname("Özcan").withKlasseUuid(uuid1a);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);
			importDaten.add(daten);
		}

		{

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, sprache).withNachname("Katja")
				.withVorname("Fassbinder").withKlasseUuid(uuid1a);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);
			importDaten.add(daten);
		}

		{

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, sprache).withNachname("Kaled")
				.withVorname("Amira").withKlasseUuid(uuid2a);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);
			importDaten.add(daten);
		}

		{

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.IKID, sprache).withNachname("Benedikt")
				.withVorname("Schönack").withKlasseUuid(uuid2a);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);
			importDaten.add(daten);
		}

		{

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, sprache).withNachname("Bakir")
				.withVorname("Özcan").withKlasseUuid(uuid2b);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);
			importDaten.add(daten);
		}

		{

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, sprache).withNachname("Kaled")
				.withVorname("Amira").withKlasseUuid(uuid2a);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);
			importDaten.add(daten);
		}

		{

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, sprache).withNachname("Thomas")
				.withVorname("Grüter").withKlasseUuid(uuid2b);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);
			importDaten.add(daten);
		}

		{

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, sprache).withNachname("Kaled")
				.withVorname("Amira").withKlasseUuid(uuid2a);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);
			importDaten.add(daten);
		}

		{

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, sprache).withNachname("Analena")
				.withVorname("Log").withKlasseUuid(uuid2b);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);
			importDaten.add(daten);
		}

		{

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, sprache).withNachname("Ruth")
				.withVorname("Admin").withKlasseUuid(uuid2b);
			KindRequestData kindRequestData = new KindRequestData().withUuid(KindRequestData.KEINE_UUID).withKind(kindEditorModel);
			KindImportDaten daten = new KindImportDaten(kindRequestData);
			importDaten.add(daten);
		}

		return importDaten;
	}

}
