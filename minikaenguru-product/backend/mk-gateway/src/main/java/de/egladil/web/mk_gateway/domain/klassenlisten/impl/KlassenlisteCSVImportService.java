// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
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
import de.egladil.web.mk_gateway.domain.klassenlisten.KindImportVO;
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenimportZeile;
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenlisteImportService;
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenlisteUeberschrift;
import de.egladil.web.mk_gateway.domain.klassenlisten.StringKlassenimportZeileMapper;
import de.egladil.web.mk_gateway.domain.klassenlisten.UploadKlassenlisteContext;
import de.egladil.web.mk_gateway.domain.klassenlisten.api.KlassenlisteImportReport;
import de.egladil.web.mk_gateway.domain.klassenlisten.utils.ImportDublettenPruefer;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.uploads.UploadRepository;
import de.egladil.web.mk_gateway.domain.uploads.UploadStatus;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.UploadHibernateRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

/**
 * KlassenlisteCSVImportService importiert die Kinder aus einer CSV-Datei.
 */
@RequestScoped
public class KlassenlisteCSVImportService implements KlassenlisteImportService {

	private static final String NAME_UPLOAD_DIR = "upload";

	private static final Logger LOGGER = LoggerFactory.getLogger(KlassenlisteCSVImportService.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@ConfigProperty(name = "path.external.files")
	String pathExternalFiles;

	@Inject
	AuthorizationService authService;

	@Inject
	KlassenService klassenService;

	@Inject
	KinderService kinderService;

	@Inject
	UploadRepository uploadRepository;

	private List<KindCreated> kindCreatedEventPayloads;

	public static KlassenlisteImportService createForIntegrationTests(final EntityManager em) {

		KlassenlisteCSVImportService result = new KlassenlisteCSVImportService();
		result.authService = AuthorizationService.createForIntegrationTest(em);
		result.klassenService = KlassenServiceImpl.createForIntegrationTest(em);
		result.kinderService = KinderServiceImpl.createForIntegrationTest(em);
		result.uploadRepository = UploadHibernateRepository.createForIntegrationTests(em);
		result.pathExternalFiles = "/home/heike/git/testdaten/minikaenguru/integrationtests";
		return result;
	}

	@Override
	public ResponsePayload importiereKinder(final UploadKlassenlisteContext uploadKlassenlisteContext, final PersistenterUpload uploadMetadata) {

		String path = getUploadDir() + File.separator + uploadMetadata.getUuid() + ".csv";

		String encoding = uploadMetadata.getEncoding();
		List<String> lines = MkGatewayFileUtils.readLines(path, encoding);

		return importiereKinder(uploadKlassenlisteContext, uploadMetadata, encoding, lines);
	}

	@Override
	public ResponsePayload importiereKinder(final UploadKlassenlisteContext uploadKlassenlisteContext, final PersistenterUpload uploadMetadata, final String encoding, final List<String> lines) {

		if (lines.isEmpty()) {

			updateUploadstatusQuietly(uploadMetadata, UploadStatus.LEER);
			String msg = applicationMessages.getString("klassenimport.dateiLeer");
			throw new UploadFormatException(msg);
		}

		KlassenlisteUeberschrift ueberschrift = new KlassenlisteUeberschrift(lines.get(0));

		if (lines.size() == 1) {

			updateUploadstatusQuietly(uploadMetadata, UploadStatus.LEER);
			String msg = applicationMessages.getString("klassenimport.dateiLeer");
			throw new UploadFormatException(msg);
		}

		StringKlassenimportZeileMapper zeilenMapper = new StringKlassenimportZeileMapper(ueberschrift);

		List<Pair<Integer, String>> zeilenMitIndex = new ArrayList<>();

		for (int i = 1; i < lines.size(); i++) {

			String line = lines.get(i);
			zeilenMitIndex.add(Pair.of(Integer.valueOf(i), line));
		}

		List<KlassenimportZeile> klassenimportZeilen = zeilenMitIndex.stream().map(p -> zeilenMapper.apply(p))
			.filter(opt -> opt.isPresent()).map(opt -> opt.get()).collect(Collectors.toList());

		List<String> nichtImportierteZeilen = klassenimportZeilen.stream()
			.filter(z -> z.getFehlermeldung() != null).map(z -> z.getFehlermeldung()).collect(Collectors.toList());

		long anzahlMitFehlern = nichtImportierteZeilen.size();

		if (anzahlMitFehlern == klassenimportZeilen.size()) {

			LOGGER.warn("Datei, bei der alle Zeilen fehlerhaft sind: ");
			throw new UploadFormatException(applicationMessages.getString("upload.klassenliste.alleZeilenFehlerhaft"));
		}

		Identifier veranstalterID = new Identifier(uploadMetadata.getBenutzerUuid());
		String schulkuerzel = uploadMetadata.getTeilnahmenummer();

		try {

			List<Kind> vorhandeneKinder = kinderService.findWithSchulteilname(schulkuerzel);

			KlassenImportErgebnis importErgebnis = this.doTheImport(veranstalterID, schulkuerzel,
				uploadKlassenlisteContext, klassenimportZeilen, vorhandeneKinder);

			long anzahlDubletten = importErgebnis.getKindImportDaten().stream().filter(k -> k.isDublettePruefen()).count();
			long anzahlMitUnklarerKlassenstufe = importErgebnis.getKindImportDaten().stream().filter(k -> k.isKlassenstufePruefen())
				.count();

			List<KlasseAPIModel> klasseAPIModels = klassenService.klassenZuSchuleLaden(schulkuerzel, veranstalterID.identifier());

			String msg = getImportMessage(klasseAPIModels.size(), anzahlMitFehlern, anzahlMitUnklarerKlassenstufe, anzahlDubletten,
				encoding);

			KlassenlisteImportReport payloadData = new KlassenlisteImportReport()
				.withKlassen(klasseAPIModels).withAnzahlDubletten(anzahlDubletten)
				.withAnzahlKlassenstufeUnklar(anzahlMitUnklarerKlassenstufe).withAnzahlNichtImportiert(anzahlMitFehlern)
				.withAnzahlKlassenImportiert(importErgebnis.getKlassen().size())
				.withAnzahlKinderImportiert(importErgebnis.getKinder().size());

			payloadData.setFehler(nichtImportierteZeilen);

			importErgebnis.getKindImportDaten().forEach(d -> {

				if (d.getWarnmeldungDublette() != null) {

					payloadData.addWarnung(d.getWarnmeldungDublette());
				}

				if (d.getWarnmeldungKlassenstufe() != null) {

					payloadData.addWarnung(d.getWarnmeldungKlassenstufe());
				}
			});

			if (!payloadData.getFehlerUndWarnungen().isEmpty()) {

				String pathFehlerreport = getUploadDir() + File.separator + uploadMetadata.getUuid() + "-fehlerreport.csv";
				MkGatewayFileUtils.writeLines(payloadData.getFehlerUndWarnungen(), pathFehlerreport);
				payloadData.setUuidImportReport(uploadMetadata.getUuid());
			}

			ResponsePayload responsePayload = null;

			if (klasseAPIModels.isEmpty() || anzahlMitFehlern + anzahlDubletten + anzahlMitUnklarerKlassenstufe > 0) {

				responsePayload = new ResponsePayload(MessagePayload.warn(msg), payloadData);
				updateUploadstatusQuietly(uploadMetadata, UploadStatus.DATENFEHLER);
			} else {

				responsePayload = new ResponsePayload(MessagePayload.info(msg), payloadData);
				updateUploadstatusQuietly(uploadMetadata, UploadStatus.IMPORTIERT);
			}

			LOGGER.info("Benutzer {}: Upload-UUID {} für Wettbewerb {} importiert. Schulkürzel={}",
				StringUtils.abbreviate(uploadMetadata.getBenutzerUuid(), 11),
				StringUtils.abbreviate(uploadMetadata.getUuid(), 12), uploadKlassenlisteContext.getWettbewerb().id(),
				uploadMetadata.getTeilnahmenummer());

			return responsePayload;
		} catch (PersistenceException e) {

			// klassenimport.error
			String msg = applicationMessages.getString("klassenimport.error");
			LOGGER.error("{}: {}", msg, e.getMessage(), e);

			updateUploadstatusQuietly(uploadMetadata, UploadStatus.EXCEPTION);

			return ResponsePayload.messageOnly(MessagePayload.error(msg));
		}
	}

	/**
	 * @param uploadMetadata
	 * @param leer
	 */
	@Override
	public void updateUploadstatusQuietly(final PersistenterUpload uploadMetadata, final UploadStatus leer) {

		try {

			doUpdateTheUploadStatus(uploadMetadata, leer);
		} catch (PersistenceException e) {

			String msg = applicationMessages.getString("klassenimport.error");
			LOGGER.error("{}: {}", msg, e.getMessage(), e);

			// TODO: hier eine Telegram-Message senden?

		}

	}

	String getImportMessage(final int anzahlImportierteKlassen, final long anzahlMitFehlern, final long anzahlMitUnklarerKlassenstufe, final long anzahlDubletten, final String encoding) {

		if (anzahlImportierteKlassen == 0) {

			return applicationMessages.getString("klassenimport.keineKlasseImportiert");
		}

		if (anzahlMitFehlern > 0) {

			String message = applicationMessages.getString("klassenimport.nichtVollstaendig");

			if (!MkGatewayFileUtils.DEFAULT_ENCODING.equals(encoding)) {

				message += " " + applicationMessages.getString("klassenimport.additionallyCheckUmlaute.text");
			}
			return message;
		}

		if (anzahlDubletten > 0 && anzahlMitUnklarerKlassenstufe > 0) {

			String message = applicationMessages.getString("klassenimport.vollstaendigMitWarnung");

			if (!MkGatewayFileUtils.DEFAULT_ENCODING.equals(encoding)) {

				message += " " + applicationMessages.getString("klassenimport.additionallyCheckUmlaute.text");
			}
			return message;
		}

		if (anzahlDubletten > 0) {

			String message = applicationMessages.getString("klassenimport.vollstaendigMitWarnungNurDublette");

			if (!MkGatewayFileUtils.DEFAULT_ENCODING.equals(encoding)) {

				message += " " + applicationMessages.getString("klassenimport.additionallyCheckUmlaute.text");
			}
			return message;

		}

		if (anzahlMitUnklarerKlassenstufe > 0) {

			String message = applicationMessages.getString("klassenimport.vollstaendigMitWarnungNurKlassenstufe");

			if (!MkGatewayFileUtils.DEFAULT_ENCODING.equals(encoding)) {

				message += " " + applicationMessages.getString("klassenimport.additionallyCheckUmlaute.text");
			}
			return message;
		}

		String message = applicationMessages.getString("klassenimport.success");

		if (!MkGatewayFileUtils.DEFAULT_ENCODING.equals(encoding)) {

			message += " " + applicationMessages.getString("klassenimport.checkUmlaute.text");
		}
		return message;
	}

	@Transactional
	KlassenImportErgebnis doTheImport(final Identifier veranstalterID, final String schulkuerzel, final UploadKlassenlisteContext uploadKlassenlisteContext, final List<KlassenimportZeile> klassenimportZeilen, final List<Kind> vorhandeneKinder) {

		vorhandeneKinder.stream().forEach(k -> LOGGER.debug(k.toString()));

		Map<String, Klasse> klassenMap = this.createAndImportKlassen(veranstalterID, schulkuerzel, klassenimportZeilen);

		KlassenImportErgebnis importErgebnis = this.createAndImportKinder(veranstalterID, schulkuerzel, klassenimportZeilen,
			klassenMap, uploadKlassenlisteContext, vorhandeneKinder);

		List<Klasse> importierteKlassen = klassenMap.values().stream().collect(Collectors.toList());
		importErgebnis.setKlassen(importierteKlassen);
		return importErgebnis;
	}

	@Transactional
	PersistenterUpload doUpdateTheUploadStatus(final PersistenterUpload persistenterUpload, final UploadStatus uploadStatus) {

		persistenterUpload.setStatus(uploadStatus);
		return this.uploadRepository.updateUpload(persistenterUpload);

	}

	Map<String, Klasse> createAndImportKlassen(final Identifier veranstalterID, final String schulkuerzel, final List<KlassenimportZeile> importZeilen) {

		Map<String, KlasseRequestData> klassenMap = new HashMap<>();

		// Hier ist die Überschrift bereits ausgeschlossen
		for (int i = 0; i < importZeilen.size(); i++) {

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

		List<KindImportVO> importDaten = new ArrayList<>();

		for (int i = 0; i < klassenimportZeilen.size(); i++) {

			KlassenimportZeile zeile = klassenimportZeilen.get(i);

			if (!zeile.ok()) {

				KindImportDaten kindImportDaten = KindImportDaten.createWithFehlermeldung(zeile.getFehlermeldung());
				KindImportVO kindImportVO = new KindImportVO(zeile, kindImportDaten);
				importDaten.add(kindImportVO);
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

			KindImportVO kindImportVO = new KindImportVO(zeile, kindImportDaten);

			if (klassenstufePruefen) {

				kindImportVO.setWarnungKlassenstufe("Zeile " + zeile.getIndex() + ": " + zeile.getImportRohdaten()
					+ ": diese Klassenstufe gibt es nicht. Die Klassenstufe wurde auf \"2\" gesetzt.");
				// kindImportVO.setKlassenstufePruefen(true);
			}

			importDaten.add(kindImportVO);
		}

		List<KindImportVO> dublettenErgebnis = new ImportDublettenPruefer()
			.pruefeUndMarkiereDublettenImportDaten(importDaten, vorhandeneKinder);

		long anzahlDubletten = dublettenErgebnis.stream().filter(z -> z.isDublettePruefen()).count();

		if (anzahlDubletten > 0) {

			LOGGER.warn("Im Import von Schule {} wurden Dubletten gefunden. Anzahl = {}", schulkuerzel,
				anzahlDubletten);
		}

		List<KindImportDaten> kinderImportDaten = new ArrayList<>();

		importDaten.stream().forEach(p -> {

			KindImportDaten kindDaten = p.getKindImportDaten();
			kindDaten.setWarnmeldungDublette(p.getWarnungDublette());
			kindDaten.setWarnmeldungKlassenstufe(p.getWarnungKlassenstufe());
			kinderImportDaten.add(kindDaten);
		});

		List<Kind> kinder = this.kinderService.importiereKinder(veranstalterID, schulkuerzel, importDaten);

		return new KlassenImportErgebnis(kinderImportDaten, kinder);
	}

	@Override
	public DownloadData getImportReport(final String lehrerUuid, final String reportUuid) {

		Optional<PersistenterUpload> optUpload = uploadRepository.findByUuid(reportUuid);

		if (optUpload.isEmpty()) {

			throw new NotFoundException();
		}

		PersistenterUpload uploadMetadata = optUpload.get();

		Identifier schuleIdentifier = new Identifier(uploadMetadata.getTeilnahmenummer());

		authService.checkPermissionForTeilnahmenummerAndReturnRolle(new Identifier(lehrerUuid),
			schuleIdentifier, "[getVertragAuftragsdatenverarbeitung - " + uploadMetadata.getTeilnahmenummer() + "]");

		String pathFehlerreport = getUploadDir() + File.separator + uploadMetadata.getUuid() + "-fehlerreport.csv";

		byte[] data = MkGatewayFileUtils.readBytesFromFile(pathFehlerreport);

		return new DownloadData(uploadMetadata.getUuid() + "-fehlerreport.csv", data);
	}

	List<KindCreated> getKindCreatedEventPayloads() {

		return kindCreatedEventPayloads;
	}

	String getUploadDir() {

		return pathExternalFiles + File.separator + NAME_UPLOAD_DIR;
	}
}
