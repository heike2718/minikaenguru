// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderService;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.kinder.KlassenService;
import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindRequestData;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseRequestData;
import de.egladil.web.mk_gateway.domain.kinder.events.KindCreated;
import de.egladil.web.mk_gateway.domain.kinder.events.KlasseCreated;
import de.egladil.web.mk_gateway.domain.kinder.impl.KinderServiceImpl;
import de.egladil.web.mk_gateway.domain.kinder.impl.KlassenServiceImpl;
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenimportZeile;
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenlisteImportService;
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenlisteUeberschrift;
import de.egladil.web.mk_gateway.domain.klassenlisten.StringKlassenimportZeileMapper;
import de.egladil.web.mk_gateway.domain.klassenlisten.UploadKlassenlisteContext;
import de.egladil.web.mk_gateway.domain.klassenlisten.utils.KinderDublettenPruefer;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;

/**
 * KlassenlisteCSVImportService importiert die Kinder aus einer CSV-Datei.
 */
@ApplicationScoped
public class KlassenlisteCSVImportService implements KlassenlisteImportService {

	private static final Logger LOGGER = LoggerFactory.getLogger(KlassenlisteCSVImportService.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@ConfigProperty(name = "upload.folder.path")
	String pathUploadDir;

	@Inject
	KlassenService klassenService;

	@Inject
	KinderService kinderService;

	@Inject
	Event<KlasseCreated> klasseCreatedEvent;

	@Inject
	Event<KindCreated> kindCreatedEvent;

	public static KlassenlisteImportService createForIntegrationTests(final EntityManager em) {

		KlassenlisteCSVImportService result = new KlassenlisteCSVImportService();
		result.klassenService = KlassenServiceImpl.createForIntegrationTest(em);
		result.kinderService = KinderServiceImpl.createForIntegrationTest(em);
		return result;
	}

	@Override
	public ResponsePayload importiereKinder(final UploadKlassenlisteContext uploadKlassenlisteContext, final PersistenterUpload uploadMetadata) {

		String path = pathUploadDir + File.separator + uploadMetadata.getUuid() + ".csv";

		List<String> lines = getContents(path);

		KlassenlisteUeberschrift ueberschrift = new KlassenlisteUeberschrift(lines.get(0));

		StringKlassenimportZeileMapper zeilenMapper = new StringKlassenimportZeileMapper(ueberschrift);

		List<KlassenimportZeile> klassenimportZeilen = lines.stream().map(z -> zeilenMapper.apply(z)).collect(Collectors.toList());

		Identifier veranstalterID = new Identifier(uploadMetadata.getVeranstalterUuid());
		String schulkuerzel = uploadMetadata.getTeilnahmenummer();

		try {

			KlassenImportErgebnis importErgebnis = this.openTransactionAndImport(veranstalterID, schulkuerzel,
				uploadKlassenlisteContext, klassenimportZeilen);

			long anzahlDubletten = importErgebnis.getKinder().stream().filter(k -> k.isDublettePruefen()).count();
			long anzahlMitUnklarerKlassenstufe = importErgebnis.getKinder().stream().filter(k -> k.isKlassenstufePruefen()).count();

			String msg = getImportMessage(anzahlDubletten, anzahlMitUnklarerKlassenstufe);

			List<Klasse> klassen = importErgebnis.getKlassen();

			if (klasseCreatedEvent != null) {

				for (Klasse klasse : klassen) {

					KlasseCreated klasseCreated = (KlasseCreated) new KlasseCreated(veranstalterID.identifier())
						.withKlasseID(klasse.identifier().identifier()).withSchulkuerzel(schulkuerzel).withName(klasse.name());
					klasseCreatedEvent.fire(klasseCreated);
				}
			}

			if (kindCreatedEvent != null) {

				for (Kind kind : importErgebnis.getKinder()) {

					KindCreated kindCreated = (KindCreated) new KindCreated(veranstalterID.identifier())
						.withKindID(kind.identifier().identifier())
						.withKlassenstufe(kind.klassenstufe())
						.withSprache(kind.sprache())
						.withTeilnahmenummer(schulkuerzel)
						.withKlasseID(kind.klasseID().identifier());

					kindCreatedEvent.fire(kindCreated);
				}
			}

			List<KlasseAPIModel> payloadData = klassen.stream().map(kl -> KlasseAPIModel.createFromKlasse(kl))
				.collect(Collectors.toList());

			if (anzahlDubletten + anzahlMitUnklarerKlassenstufe > 0) {

				return new ResponsePayload(MessagePayload.warn(msg), payloadData);
			}

			return new ResponsePayload(MessagePayload.info(msg), payloadData);
		} catch (PersistenceException e) {

			String msg = applicationMessages.getString("klassenimport.error");
			LOGGER.error("{}: {}", msg, e.getMessage(), e);

			return ResponsePayload.messageOnly(MessagePayload.error(msg));
		}
	}

	String getImportMessage(final long anzahlDubletten, final long anzahlMitUnklarerKlassenstufe) {

		if (anzahlDubletten > 0 && anzahlMitUnklarerKlassenstufe > 0) {

			return MessageFormat.format(applicationMessages.getString(""),
				new Object[] { "klassenimport.allesPruefen" + anzahlDubletten, "" + anzahlMitUnklarerKlassenstufe });
		}

		if (anzahlDubletten > 0) {

			return MessageFormat.format(applicationMessages.getString(""),
				new Object[] { "klassenimport.dublettenPruefen" + anzahlDubletten, "" + anzahlMitUnklarerKlassenstufe });

		}

		if (anzahlMitUnklarerKlassenstufe > 0) {

			return MessageFormat.format(applicationMessages.getString(""),
				new Object[] { "klassenimport.klassenstufenPruefen" + anzahlDubletten, "" + anzahlMitUnklarerKlassenstufe });
		}

		return applicationMessages.getString("klassenimport.success");
	}

	@Transactional
	KlassenImportErgebnis openTransactionAndImport(final Identifier veranstalterID, final String schulkuerzel, final UploadKlassenlisteContext uploadKlassenlisteContext, final List<KlassenimportZeile> klassenimportZeilen) {

		Map<String, Klasse> klassenMap = this.importiereKlassen(veranstalterID, schulkuerzel, klassenimportZeilen);

		List<Kind> importierteKinder = this.createAndImportKinder(veranstalterID, schulkuerzel, klassenimportZeilen, klassenMap,
			uploadKlassenlisteContext);

		List<Klasse> importierteKlassen = klassenMap.values().stream().collect(Collectors.toList());
		return new KlassenImportErgebnis(importierteKlassen, importierteKinder);
	}

	Map<String, Klasse> importiereKlassen(final Identifier veranstalterID, final String schulkuerzel, final List<KlassenimportZeile> importZeilen) {

		Map<String, KlasseRequestData> klassenMap = new HashMap<>();

		for (KlassenimportZeile zeile : importZeilen) {

			String nameKlasse = zeile.getKlasse();
			KlasseRequestData klasseRequestData = klassenMap.get(nameKlasse);

			if (klasseRequestData == null) {

				KlasseEditorModel klasseEditorModel = new KlasseEditorModel().withName(nameKlasse);
				klasseRequestData = new KlasseRequestData().withKlasse(klasseEditorModel).withSchulkuerzel(schulkuerzel)
					.withUuid(KlasseRequestData.KEINE_UUID);
				klassenMap.put(nameKlasse, klasseRequestData);
			}
		}

		List<KlasseRequestData> klassendaten = klassenMap.values().stream().collect(Collectors.toList());

		List<Klasse> klassen = klassenService.importiereKlassen(veranstalterID, new Identifier(schulkuerzel), klassendaten);

		final Map<String, Klasse> result = new HashMap<>();
		klassen.stream().forEach(kl -> result.put(kl.name(), kl));
		return result;
	}

	List<Kind> createAndImportKinder(final Identifier veranstalterID, final String schulkuerzel, final List<KlassenimportZeile> klassenimportZeilen, final Map<String, Klasse> klassenMap, final UploadKlassenlisteContext uploadKlassenlisteContext) {

		List<Kind> result = new ArrayList<>();

		List<KindImportDaten> importDaten = new ArrayList<>();

		for (int i = 1; i < klassenimportZeilen.size(); i++) {

			KlassenimportZeile zeile = klassenimportZeilen.get(i);

			String nameKlasse = zeile.getKlasse();
			Klasse klasse = klassenMap.get(nameKlasse);

			Optional<Klassenstufe> optKlassenstufe = zeile.mapKlassenstufe();
			boolean klassenstufePruefen = false;

			Klassenstufe klassenstufe = null;

			if (optKlassenstufe.isEmpty()) {

				klassenstufe = Klassenstufe.ZWEI;
				klassenstufePruefen = true;
			} else {

				klassenstufe = optKlassenstufe.get();
			}
			KindEditorModel kindEditorModel = new KindEditorModel(klassenstufe, uploadKlassenlisteContext.getSprache())
				.withKlasseUuid(klasse.identifier().identifier()).withVorname(zeile.getVorname());

			if (uploadKlassenlisteContext.isNachnameAlsZusatz()) {

				kindEditorModel = kindEditorModel.withZusatz(zeile.getNachname());
			} else {

				kindEditorModel = kindEditorModel.withZusatz(zeile.getNachname());
			}

			KindRequestData kindRequestData = new KindRequestData().withKind(kindEditorModel)
				.withKuerzelLand(uploadKlassenlisteContext.getKuerzelLand())
				.withUuid(KindRequestData.KEINE_UUID);

			KindImportDaten kindImportDaten = new KindImportDaten(kindRequestData);
			kindImportDaten.setKlassenstufePruefen(klassenstufePruefen);

			importDaten.add(kindImportDaten);
		}

		int anzahlDublettenInImportdatei = new KinderDublettenPruefer().pruefeUndMarkiereDublettenImportDaten(importDaten);

		if (anzahlDublettenInImportdatei > 0) {

			LOGGER.warn("Im Import von Schule {} wurden Dubletten gefunden. Anzahl = {}", schulkuerzel,
				anzahlDublettenInImportdatei);
		}

		result = this.kinderService.importiereKinder(veranstalterID, schulkuerzel, importDaten);

		return result;
	}

	List<String> getContents(final String path) {

		File file = new File(path);

		try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {

			List<String> lines = new ArrayList<>();
			String line = null;
			int index = 0;

			while ((line = br.readLine()) != null) {

				lines.add(index++, line);
			}

			return lines;

		} catch (IOException e) {

			LOGGER.error("Konnte Klassenliste nicht importieren: path={}: {}", path,
				e.getMessage(), e);
			throw new MkGatewayRuntimeException("Beim Import einer Klassenliste ist ein Fehler aufgetreten", e);

		}
	}

	void setPathUploadDir(final String pathUploadDir) {

		this.pathUploadDir = pathUploadDir;
	}

}
