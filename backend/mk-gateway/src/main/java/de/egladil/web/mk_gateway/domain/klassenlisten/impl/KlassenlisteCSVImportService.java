// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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
import de.egladil.web.mk_gateway.domain.kinder.impl.KinderServiceImpl;
import de.egladil.web.mk_gateway.domain.kinder.impl.KlassenServiceImpl;
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenimportZeile;
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenlisteImportService;
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenlisteUeberschrift;
import de.egladil.web.mk_gateway.domain.klassenlisten.StringKlassenimportZeileMapper;
import de.egladil.web.mk_gateway.domain.klassenlisten.UploadKlassenlisteContext;
import de.egladil.web.mk_gateway.domain.klassenlisten.api.KlassenlisteImportReport;
import de.egladil.web.mk_gateway.domain.klassenlisten.utils.ImportDublettenPruefer;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;

/**
 * KlassenlisteCSVImportService importiert die Kinder aus einer CSV-Datei.
 */
@RequestScoped
public class KlassenlisteCSVImportService implements KlassenlisteImportService {

	private static final Logger LOGGER = LoggerFactory.getLogger(KlassenlisteCSVImportService.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@ConfigProperty(name = "upload.folder.path")
	String pathUploadDir;

	@Inject
	KlassenService klassenService;

	@Inject
	KinderService kinderService;

	private List<KindCreated> kindCreatedEventPayloads;

	public static KlassenlisteImportService createForIntegrationTests(final EntityManager em) {

		KlassenlisteCSVImportService result = new KlassenlisteCSVImportService();
		result.klassenService = KlassenServiceImpl.createForIntegrationTest(em);
		result.kinderService = KinderServiceImpl.createForIntegrationTest(em);
		result.pathUploadDir = "/home/heike/mkv/upload";
		return result;
	}

	@Override
	public ResponsePayload importiereKinder(final UploadKlassenlisteContext uploadKlassenlisteContext, final PersistenterUpload uploadMetadata) {

		String path = pathUploadDir + File.separator + uploadMetadata.getUuid() + ".csv";

		List<String> lines = readFileContent(path);

		KlassenlisteUeberschrift ueberschrift = new KlassenlisteUeberschrift(lines.get(0));

		StringKlassenimportZeileMapper zeilenMapper = new StringKlassenimportZeileMapper(ueberschrift);

		List<Pair<Integer, String>> zeilenMitIndex = new ArrayList<>();

		for (int i = 0; i < lines.size(); i++) {

			String line = lines.get(i);
			zeilenMitIndex.add(Pair.of(Integer.valueOf(i), line));
		}

		List<KlassenimportZeile> klassenimportZeilen = zeilenMitIndex.stream().map(p -> zeilenMapper.apply(p))
			.filter(opt -> opt.isPresent()).map(opt -> opt.get()).collect(Collectors.toList());

		List<String> nichtImportierteZeilen = klassenimportZeilen.stream()
			.filter(z -> z.getFehlermeldung() != null).map(z -> z.getFehlermeldung()).collect(Collectors.toList());

		long anzahlMitFehlern = nichtImportierteZeilen.size();

		Identifier veranstalterID = new Identifier(uploadMetadata.getVeranstalterUuid());
		String schulkuerzel = uploadMetadata.getTeilnahmenummer();

		try {

			List<Kind> vorhandeneKinder = kinderService.findWithSchulteilname(schulkuerzel);

			KlassenImportErgebnis importErgebnis = this.doTheImport(veranstalterID, schulkuerzel,
				uploadKlassenlisteContext, klassenimportZeilen, vorhandeneKinder);

			long anzahlDubletten = importErgebnis.getKindImportDaten().stream().filter(k -> k.isDublettePruefen()).count();
			long anzahlMitUnklarerKlassenstufe = importErgebnis.getKindImportDaten().stream().filter(k -> k.isKlassenstufePruefen())
				.count();

			String msg = getImportMessage(anzahlMitFehlern, anzahlMitUnklarerKlassenstufe, anzahlDubletten);

			List<KlasseAPIModel> klasseAPIModels = klassenService.klassenZuSchuleLaden(schulkuerzel, veranstalterID.identifier());

			KlassenlisteImportReport payloadData = new KlassenlisteImportReport()
				.withKlassen(klasseAPIModels).withAnzahlDubletten(anzahlDubletten)
				.withAnzahlKlassenstufeUnklar(anzahlMitUnklarerKlassenstufe).withAnzahlNichtImportiert(anzahlMitFehlern)
				.withAnzahlKlassen(klasseAPIModels.size()).withAnzahlKinderImportiert(importErgebnis.getKinder().size());

			payloadData.setNichtImportierteZeilen(nichtImportierteZeilen);

			if (anzahlMitFehlern > 0) {

				this.writeFehlermeldungen(nichtImportierteZeilen, uploadMetadata.getUuid());
				payloadData.setUuidImportReport(uploadMetadata.getUuid());
			}

			if (anzahlMitFehlern + anzahlDubletten + anzahlMitUnklarerKlassenstufe > 0) {

				return new ResponsePayload(MessagePayload.warn(msg), payloadData);
			}

			return new ResponsePayload(MessagePayload.info(msg), payloadData);
		} catch (PersistenceException e) {

			String msg = applicationMessages.getString("klassenimport.error");
			LOGGER.error("{}: {}", msg, e.getMessage(), e);

			return ResponsePayload.messageOnly(MessagePayload.error(msg));
		}
	}

	String getImportMessage(final long anzahlMitFehlern, final long anzahlMitUnklarerKlassenstufe, final long anzahlDubletten) {

		if (anzahlMitFehlern > 0) {

			return applicationMessages.getString("klassenimport.nichtVollstaendig");
		}

		if (anzahlDubletten > 0 && anzahlMitUnklarerKlassenstufe > 0) {

			return applicationMessages.getString("klassenimport.vollstaendigMitWarnung");
		}

		if (anzahlDubletten > 0) {

			return applicationMessages.getString("klassenimport.vollstaendigMitWarnungNurDublette");

		}

		if (anzahlMitUnklarerKlassenstufe > 0) {

			return applicationMessages.getString("klassenimport.vollstaendigMitWarnungNurKlassenstufe");
		}

		return applicationMessages.getString("klassenimport.success");
	}

	@Transactional
	KlassenImportErgebnis doTheImport(final Identifier veranstalterID, final String schulkuerzel, final UploadKlassenlisteContext uploadKlassenlisteContext, final List<KlassenimportZeile> klassenimportZeilen, final List<Kind> vorhandeneKinder) {

		Map<String, Klasse> klassenMap = this.createAndImportKlassen(veranstalterID, schulkuerzel, klassenimportZeilen);

		KlassenImportErgebnis importErgebnis = this.createAndImportKinder(veranstalterID, schulkuerzel, klassenimportZeilen,
			klassenMap, uploadKlassenlisteContext, vorhandeneKinder);

		List<Klasse> importierteKlassen = klassenMap.values().stream().collect(Collectors.toList());
		importErgebnis.setKlassen(importierteKlassen);
		return importErgebnis;
	}

	Map<String, Klasse> createAndImportKlassen(final Identifier veranstalterID, final String schulkuerzel, final List<KlassenimportZeile> importZeilen) {

		Map<String, KlasseRequestData> klassenMap = new HashMap<>();

		for (int i = 1; i < importZeilen.size(); i++) {

			KlassenimportZeile zeile = importZeilen.get(i);

			if (zeile.ok()) {

				String nameKlasse = zeile.getKlasse();
				KlasseRequestData klasseRequestData = klassenMap.get(nameKlasse);

				if (klasseRequestData == null) {

					KlasseEditorModel klasseEditorModel = new KlasseEditorModel().withName(nameKlasse);
					klasseRequestData = new KlasseRequestData().withKlasse(klasseEditorModel).withSchulkuerzel(schulkuerzel)
						.withUuid(KlasseRequestData.KEINE_UUID);
					klassenMap.put(nameKlasse, klasseRequestData);
				}
			}
		}

		List<KlasseRequestData> klassendaten = klassenMap.values().stream().collect(Collectors.toList());

		List<Klasse> klassen = klassenService.importiereKlassen(veranstalterID, new Identifier(schulkuerzel), klassendaten);

		final Map<String, Klasse> result = new HashMap<>();
		klassen.stream().forEach(kl -> result.put(kl.name(), kl));
		return result;
	}

	KlassenImportErgebnis createAndImportKinder(final Identifier veranstalterID, final String schulkuerzel, final List<KlassenimportZeile> klassenimportZeilen, final Map<String, Klasse> klassenMap, final UploadKlassenlisteContext uploadKlassenlisteContext, final List<Kind> vorhandeneKinder) {

		List<KindImportDaten> importDaten = new ArrayList<>();

		for (int i = 1; i < klassenimportZeilen.size(); i++) {

			KlassenimportZeile zeile = klassenimportZeilen.get(i);

			if (!zeile.ok()) {

				importDaten.add(KindImportDaten.createWithFehlermeldung(zeile.getFehlermeldung()));
				continue;
			}

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

				kindEditorModel = kindEditorModel.withNachname(zeile.getNachname());
			}

			KindRequestData kindRequestData = new KindRequestData().withKind(kindEditorModel)
				.withKuerzelLand(uploadKlassenlisteContext.getKuerzelLand())
				.withUuid(KindRequestData.KEINE_UUID);

			KindImportDaten kindImportDaten = new KindImportDaten(kindRequestData);
			kindImportDaten.setKlassenstufePruefen(klassenstufePruefen);

			importDaten.add(kindImportDaten);
		}

		int anzahlDublettenInImportdatei = new ImportDublettenPruefer().pruefeUndMarkiereDublettenImportDaten(importDaten);

		if (anzahlDublettenInImportdatei > 0) {

			LOGGER.warn("Im Import von Schule {} wurden Dubletten gefunden. Anzahl = {}", schulkuerzel,
				anzahlDublettenInImportdatei);
		}

		List<Kind> kinder = this.kinderService.importiereKinder(veranstalterID, schulkuerzel, importDaten, vorhandeneKinder);

		return new KlassenImportErgebnis(importDaten, kinder);
	}

	List<String> readFileContent(final String path) {

		File file = new File(path);

		try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {

			List<String> lines = new ArrayList<>();
			String line = null;
			int index = 0;

			while ((line = br.readLine()) != null) {

				if (StringUtils.isNotBlank(line)) {

					lines.add(index++, line);
				}
			}

			return lines;

		} catch (IOException e) {

			LOGGER.error("Konnte Klassenliste nicht importieren: path={}: {}", path,
				e.getMessage(), e);
			throw new MkGatewayRuntimeException("IOException beim Import einer Klassenliste", e);

		}
	}

	void setPathUploadDir(final String pathUploadDir) {

		this.pathUploadDir = pathUploadDir;
	}

	List<KindCreated> getKindCreatedEventPayloads() {

		return kindCreatedEventPayloads;
	}

	/**
	 * @param uploadData
	 * @param uuid
	 */
	private void writeFehlermeldungen(final List<String> fehlermeldungen, final String uuid) {

		String path = pathUploadDir + File.separator + uuid + "-fehlerreport.csv";

		File file = new File(path);

		String content = StringUtils.join(fehlermeldungen, "\n");
		content += "\n";

		try (FileOutputStream fos = new FileOutputStream(file); InputStream in = new ByteArrayInputStream(content.getBytes())) {

			IOUtils.copy(in, fos);
			fos.flush();
		} catch (IOException e) {

			LOGGER.error("Fehler beim Speichern im Filesystem: " + e.getMessage(), e);
			throw new MkGatewayRuntimeException("Konnte upload nicht ins Filesystem speichern: " + e.getMessage(), e);
		}
	}

}
