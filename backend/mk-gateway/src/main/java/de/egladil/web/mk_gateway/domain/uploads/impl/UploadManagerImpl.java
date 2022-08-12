// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.filescanner_service.clamav.VirusDetection;
import de.egladil.web.filescanner_service.scan.ScanRequestPayload;
import de.egladil.web.filescanner_service.scan.ScanResult;
import de.egladil.web.filescanner_service.scan.ScanService;
import de.egladil.web.filescanner_service.securitychecks.ThreadDetection;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.UploadThreadDetected;
import de.egladil.web.mk_gateway.domain.event.VirusDetected;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenlisteImportService;
import de.egladil.web.mk_gateway.domain.klassenlisten.UploadKlassenlisteContext;
import de.egladil.web.mk_gateway.domain.klassenlisten.impl.KlassenlisteCSVImportService;
import de.egladil.web.mk_gateway.domain.loesungszettel.upload.AuswertungImportService;
import de.egladil.web.mk_gateway.domain.loesungszettel.upload.UploadAuswertungContext;
import de.egladil.web.mk_gateway.domain.statistik.AuswertungsmodusInfoService;
import de.egladil.web.mk_gateway.domain.statistik.impl.AuswertungsmodusInfoServiceImpl;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.uploads.UploadAuthorizationService;
import de.egladil.web.mk_gateway.domain.uploads.UploadData;
import de.egladil.web.mk_gateway.domain.uploads.UploadIdentifier;
import de.egladil.web.mk_gateway.domain.uploads.UploadManager;
import de.egladil.web.mk_gateway.domain.uploads.UploadRepository;
import de.egladil.web.mk_gateway.domain.uploads.UploadRequestPayload;
import de.egladil.web.mk_gateway.domain.uploads.UploadStatus;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.domain.uploads.convert.UploadToCSVConverter;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.api.Auswertungsmodus;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.UploadHibernateRepository;

/**
 * UploadManagerImpl
 */
@ApplicationScoped
public class UploadManagerImpl implements UploadManager {

	private static final String NAME_UPLOAD_DIR = "upload";

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadManagerImpl.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@ConfigProperty(name = "mkv-app.client-id")
	String clientId;

	@ConfigProperty(name = "path.external.files")
	String pathExternalFiles;

	@ConfigProperty(name = "upload.max.bytes")
	String maxFilesizeBytes;

	@Inject
	AuthorizationService authService;

	@Inject
	UploadAuthorizationService uploadAuthService;

	@Inject
	ScanService scanService;

	@Inject
	KlassenlisteImportService klassenlisteImportService;

	@Inject
	AuswertungImportService auswertungImportService;

	@Inject
	UploadRepository uploadRepository;

	@Inject
	AuswertungsmodusInfoService auswertungsmodusInfoService;

	@Inject
	DomainEventHandler domainEventHandler;

	public static UploadManagerImpl createForIntegrationTests(final EntityManager em) {

		UploadManagerImpl result = new UploadManagerImpl();
		result.scanService = ScanService.createForIntegrationTest();
		result.clientId = "integration-test-client";
		result.pathExternalFiles = "/home/heike/git/testdaten/minikaenguru/integrationtests";
		result.authService = AuthorizationService.createForIntegrationTest(em);
		result.uploadAuthService = UploadAuthorizationServiceImpl.createForIntegrationTests(em);
		result.klassenlisteImportService = KlassenlisteCSVImportService.createForIntegrationTests(em);
		result.uploadRepository = UploadHibernateRepository.createForIntegrationTests(em);
		result.domainEventHandler = DomainEventHandler.createForIntegrationTest(em);
		result.auswertungImportService = AuswertungImportService.createForIntegrationTest(em);
		result.auswertungsmodusInfoService = AuswertungsmodusInfoServiceImpl.createForIntegrationTests(em);
		result.maxFilesizeBytes = "2097152";
		return result;

	}

	@Override
	public Pair<Rolle, Wettbewerb> authorizeUpload(final String benutzerUuid, final String teilnahmenummer, final UploadType uploadType, final WettbewerbID wettbewerbID) {

		Identifier benutzerID = new Identifier(benutzerUuid);
		Identifier teilnameID = new Identifier(teilnahmenummer);

		Rolle rolle = authService.checkPermissionForTeilnahmenummerAndReturnRolle(benutzerID, teilnameID, uploadType.toString());

		Wettbewerb wettbewerb = uploadAuthService.authorizeUploadAndReturnWettbewerb(benutzerID, teilnahmenummer, uploadType, rolle,
			wettbewerbID);

		return Pair.of(rolle, wettbewerb);
	}

	ScanResult scanUpload(final UploadRequestPayload uploadPayload) {

		String filename = uploadPayload.getUploadData().getFilename();
		String fileOwnerId = uploadPayload.getBenutzerID().identifier();
		int maxBytes = Integer.valueOf(maxFilesizeBytes);

		int size = uploadPayload.getUploadData().size();

		if (size > maxBytes) {

			String errorMessage = applicationMessages.getString("upload.maxSizeExceeded");

			UploadThreadDetected threadDetected = new UploadThreadDetected().withClientId(clientId)
				.withFileName(filename)
				.withOwnerId(fileOwnerId)
				.withFilescannerMessage(errorMessage + ": size=" + size + " bytes");

			domainEventHandler.handleEvent(threadDetected);

			throw new UploadFormatException(errorMessage);
		}

		ScanRequestPayload scanRequestPayload = new ScanRequestPayload().withClientId(clientId)
			.withFileOwner(fileOwnerId).withUpload(uploadPayload.getUploadData().toUpload());

		ScanResult scanResult = scanService.scanFile(scanRequestPayload);

		VirusDetection virusDetection = scanResult.getVirusDetection();

		if (virusDetection != null && virusDetection.isVirusDetected()) {

			VirusDetected virusDetected = new VirusDetected().withClientId(clientId)
				.withFileName(filename)
				.withOwnerId(fileOwnerId)
				.withVirusScannerMessage(virusDetection.getScannerMessage());

			domainEventHandler.handleEvent(virusDetected);

			throw new UploadFormatException(applicationMessages.getString("upload.dangerousContent"));

		}

		ThreadDetection threadDetection = scanResult.getThreadDetection();

		if (threadDetection != null && threadDetection.isSecurityThreadDetected()) {

			UploadThreadDetected threadDetected = new UploadThreadDetected().withClientId(clientId)
				.withFileName(filename)
				.withOwnerId(fileOwnerId)
				.withFilescannerMessage(threadDetection.getSecurityCheckMessage());

			domainEventHandler.handleEvent(threadDetected);

			throw new UploadFormatException(applicationMessages.getString("upload.dangerousContent"));
		}

		DateiTyp dateiTyp = DateiTyp.valueOfTikaName(scanResult.getMediaType());

		if (dateiTyp == null) {

			LOGGER.error("Unbekannter MediaType {} - brechen ab.", scanResult.getMediaType());
			throw new UploadFormatException(applicationMessages.getString("upload.unbekannterMediaType"));
		}

		return scanResult;
	}

	@Transactional
	PersistenterUpload transformAndPersistUploadMetadata(final UploadRequestPayload uploadPayload, final ScanResult scanResult, final Long checksumme) {

		PersistenterUpload upload = new PersistenterUpload();
		upload.setChecksumme(checksumme);
		upload.setDateiname(uploadPayload.getUploadData().getFilename());
		upload.setMediatype(scanResult.getMediaType());
		upload.setStatus(UploadStatus.HOCHGELADEN);
		upload.setTeilnahmenummer(uploadPayload.getTeilnahmenummer());
		upload.setUploadType(uploadPayload.getUploadType());
		upload.setBenutzerUuid(uploadPayload.getBenutzerID().identifier());

		PersistenterUpload persistenterUpload = uploadRepository.addUploadMetaData(upload);

		return persistenterUpload;
	}

	long getCRC32Checksum(final byte[] bytes) {

		Checksum crc32 = new CRC32();
		crc32.update(bytes, 0, bytes.length);
		return crc32.getValue();
	}

	@Override
	public ResponsePayload processUpload(final UploadRequestPayload uploadPayload) throws UploadFormatException {

		UploadType uploadType = uploadPayload.getUploadType();

		if (UploadType.AUSWERTUNG == uploadType) {

			UploadAuswertungContext uploadAuswertungContext = (UploadAuswertungContext) uploadPayload.getContext();

			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier()
				.withTeilnahmenummer(uploadPayload.getTeilnahmenummer())
				.withWettbewerbID(uploadAuswertungContext.getWettbewerb().id());

			Auswertungsmodus auswertungsmodus = auswertungsmodusInfoService
				.ermittleAuswertungsmodusFuerTeilnahme(teilnahmeIdentifier);

			if (Auswertungsmodus.ONLINE == auswertungsmodus) {

				return ResponsePayload.messageOnly(MessagePayload.error(
					"Der Wettbewerb an dieser Schule wurde bereits online ausgewertet. Die Auswertungstabelle wird ignoriert."));
			}
		}

		ScanResult scanResult = this.scanUpload(uploadPayload);

		Long checksumme = this.getCRC32Checksum(uploadPayload.getUploadData().getDataBASE64());
		UploadIdentifier uploadIdentifier = new UploadIdentifier(uploadPayload.getTeilnahmenummer(), checksumme);

		Optional<PersistenterUpload> optUpload = this.uploadRepository.findUploadByIdentifier(uploadIdentifier);

		if (optUpload.isPresent()) {

			return ResponsePayload.messageOnly(MessagePayload.warn(applicationMessages.getString("upload.exists")));
		}

		PersistenterUpload persistenterUpload = transformAndPersistUploadMetadata(uploadPayload, scanResult, checksumme);

		DateiTyp dateiTyp = DateiTyp.valueOfTikaName(scanResult.getMediaType());

		String pathFile = writeUploadFile(uploadPayload.getUploadData(), dateiTyp, persistenterUpload.getUuid());

		UploadToCSVConverter uploadConverter = UploadToCSVConverter.createForDateityp(dateiTyp);

		Optional<String> optEncoding = uploadConverter.detectEncoding(pathFile);

		if (optEncoding.isPresent()) {

			persistenterUpload.setEncoding(optEncoding.get());
		}

		File csvFile = uploadConverter.convertToCSVAndPersistInFilesystem(pathFile, persistenterUpload.getUuid());

		LOGGER.info("upload " + persistenterUpload.getUuid() + " konvertiert: " + csvFile.getAbsolutePath());

		ResponsePayload responsePayload = null;

		switch (uploadType) {

		case KLASSENLISTE:

			UploadKlassenlisteContext uploadKlassenlisteContext = (UploadKlassenlisteContext) uploadPayload.getContext();
			responsePayload = klassenlisteImportService
				.importiereKinder(uploadKlassenlisteContext, persistenterUpload);
			break;

		case AUSWERTUNG:

			UploadAuswertungContext uploadAuswertungContext = (UploadAuswertungContext) uploadPayload.getContext();
			responsePayload = auswertungImportService.importiereAuswertung(uploadAuswertungContext, persistenterUpload);
			break;

		default:
			break;
		}

		if (responsePayload.isOk()) {

			removeFilesQuietly(persistenterUpload.getUuid(), dateiTyp);

		}

		return responsePayload;
	}

	/**
	 * @param persistenterUpload
	 */
	private void removeFilesQuietly(final String uuidUpload, final DateiTyp dateiTyp) {

		File uploadedFile = new File(this.getPathUploadedFile(dateiTyp, uuidUpload));

		MkGatewayFileUtils.deleteFileWithErrorLogQuietly(uploadedFile, LOGGER);
		File convertedFile = new File(this.getPathConvertedFile(uuidUpload));

		MkGatewayFileUtils.deleteFileWithErrorLogQuietly(convertedFile, LOGGER);
	}

	/**
	 * @param csvData
	 *                byte[]
	 * @param uuid
	 *                String die UUID der Upload-Metadaten aus der DB.
	 */
	private String writeUploadFile(final UploadData uploadData, final DateiTyp dateiTyp, final String uuid) {

		String path = getPathUploadedFile(dateiTyp, uuid);

		File file = new File(path);

		try (FileOutputStream fos = new FileOutputStream(file); InputStream in = new ByteArrayInputStream(uploadData.getData())) {

			IOUtils.copy(in, fos);
			fos.flush();

			return path;
		} catch (IOException e) {

			LOGGER.error("Fehler beim Speichern im Filesystem: " + e.getMessage(), e);
			throw new MkGatewayRuntimeException("Konnte upload nicht ins Filesystem speichern: " + e.getMessage(), e);
		}
	}

	/**
	 * @param  dateiTyp
	 * @param  uuid
	 * @return
	 */
	private String getPathUploadedFile(final DateiTyp dateiTyp, final String uuid) {

		return getPathUploadDir() + File.separator + uuid + dateiTyp.getSuffixWithPoint();
	}

	/**
	 * @param  dateiTyp
	 * @param  uuid
	 * @return
	 */
	private String getPathConvertedFile(final String uuid) {

		return getPathUploadDir() + File.separator + uuid + ".csv";
	}

	private String getPathUploadDir() {

		return pathExternalFiles + File.separator + NAME_UPLOAD_DIR;
	}
}
