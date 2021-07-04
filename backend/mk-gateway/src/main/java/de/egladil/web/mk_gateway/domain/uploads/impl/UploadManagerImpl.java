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
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenlisteImportService;
import de.egladil.web.mk_gateway.domain.klassenlisten.UploadKlassenlisteContext;
import de.egladil.web.mk_gateway.domain.klassenlisten.impl.KlassenlisteCSVImportService;
import de.egladil.web.mk_gateway.domain.uploads.UploadAuthorizationService;
import de.egladil.web.mk_gateway.domain.uploads.UploadData;
import de.egladil.web.mk_gateway.domain.uploads.UploadIdentifier;
import de.egladil.web.mk_gateway.domain.uploads.UploadManager;
import de.egladil.web.mk_gateway.domain.uploads.UploadRepository;
import de.egladil.web.mk_gateway.domain.uploads.UploadRequestPayload;
import de.egladil.web.mk_gateway.domain.uploads.UploadStatus;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.UploadHibernateRepository;

/**
 * UploadManagerImpl
 */
@ApplicationScoped
public class UploadManagerImpl implements UploadManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadManagerImpl.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@ConfigProperty(name = "mkv-app.client-id")
	String clientId;

	@ConfigProperty(name = "upload.folder.path")
	String pathUploadDir;

	@Inject
	AuthorizationService authService;

	@Inject
	UploadAuthorizationService uploadAuthService;

	@Inject
	ScanService scanService;

	@Inject
	KlassenlisteImportService klassenlisteImportService;

	@Inject
	UploadRepository uploadRepository;

	@Inject
	DomainEventHandler domainEventHandler;

	public static UploadManager createForIntegrationTests(final EntityManager em) {

		UploadManagerImpl result = new UploadManagerImpl();
		result.scanService = ScanService.createForIntegrationTest();
		result.clientId = "integration-test-client";
		result.pathUploadDir = "/home/heike/mkv/upload";
		result.authService = AuthorizationService.createForIntegrationTest(em);
		result.uploadAuthService = UploadAuthorizationServiceImpl.createForIntegrationTests(em);
		result.klassenlisteImportService = KlassenlisteCSVImportService.createForIntegrationTests(em);
		result.uploadRepository = UploadHibernateRepository.createForIntegrationTests(em);
		result.domainEventHandler = DomainEventHandler.createForIntegrationTest(em);
		result.scanService = ScanService.createForIntegrationTest();
		return result;

	}

	@Override
	public boolean authorizeUpload(final String veranstalterUuid, final String teilnahmenummer, final UploadType uploadType) {

		Identifier veranstalterID = new Identifier(veranstalterUuid);
		Identifier teilnameID = new Identifier(teilnahmenummer);

		authService.checkPermissionForTeilnahmenummer(veranstalterID, teilnameID, "upload Klassenliste");

		uploadAuthService.authorizeUpload(veranstalterID, teilnahmenummer, uploadType);

		return true;
	}

	ScanResult scanUpload(final UploadRequestPayload uploadPayload) {

		String fileOwnerId = uploadPayload.getVeranstalterID().identifier();
		ScanRequestPayload scanRequestPayload = new ScanRequestPayload().withClientId(clientId)
			.withFileOwner(fileOwnerId).withUpload(uploadPayload.getUploadData().toUpload());

		ScanResult scanResult = scanService.scanFile(scanRequestPayload);

		String filename = uploadPayload.getUploadData().getFilename();

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

		switch (dateiTyp) {

		case TEXT:
			break;

		case OSD:
			// TODO Hier eigenen Umwandler bemühen
			break;

		case EXCEL_ALT:
		case EXCEL_NEU:
			// TODO Hier pandas bemühen
			break;

		default:
			LOGGER.error("Unerwarteter DateiTyp {} - brechen ab.", dateiTyp);
			throw new UploadFormatException(applicationMessages.getString("upload.unbekannterMediaType"));
		}

		return scanResult;
	}

	@Transactional
	PersistenterUpload transformAndPersistUploadMetadata(final UploadRequestPayload uploadPayload, final ScanResult scanResult, final Long checksumme) {

		PersistenterUpload upload = new PersistenterUpload();
		upload.setCharset(scanResult.getCharset());
		upload.setChecksumme(checksumme);
		upload.setDateiname(uploadPayload.getUploadData().getFilename());
		upload.setMediatype(scanResult.getMediaType());
		upload.setStatus(UploadStatus.HOCHGELADEN);
		upload.setTeilnahmenummer(uploadPayload.getSchuleID().identifier());
		upload.setUploadTyp(uploadPayload.getUploadType());
		upload.setVeranstalterUuid(uploadPayload.getVeranstalterID().identifier());

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

		ScanResult scanResult = this.scanUpload(uploadPayload);

		Long checksumme = this.getCRC32Checksum(uploadPayload.getUploadData().getDataBASE64());
		UploadIdentifier uploadIdentifier = new UploadIdentifier(uploadPayload.getSchuleID().identifier(), checksumme);

		Optional<PersistenterUpload> optUpload = this.uploadRepository.findUploadByIdentifier(uploadIdentifier);

		if (optUpload.isPresent()) {

			return ResponsePayload.messageOnly(MessagePayload.warn(applicationMessages.getString("upload.exists")));
		}

		PersistenterUpload persistenterUpload = transformAndPersistUploadMetadata(uploadPayload, scanResult, checksumme);

		writeUploadFile(uploadPayload.getUploadData(), persistenterUpload.getUuid());

		ResponsePayload responsePayload = null;

		switch (uploadPayload.getUploadType()) {

		case KLASSENLISTE:

			UploadKlassenlisteContext uploadKlassenlisteContext = (UploadKlassenlisteContext) uploadPayload.getContext();
			responsePayload = klassenlisteImportService.importiereKinder(uploadKlassenlisteContext, persistenterUpload);
			break;

		default:
			break;
		}

		return responsePayload;
	}

	/**
	 * @param uploadData
	 * @param uuid
	 */
	private void writeUploadFile(final UploadData uploadData, final String uuid) {

		String path = pathUploadDir + File.separator + uuid + ".csv";

		File file = new File(path);

		try (FileOutputStream fos = new FileOutputStream(file); InputStream in = new ByteArrayInputStream(uploadData.getData())) {

			IOUtils.copy(in, fos);
			fos.flush();
		} catch (IOException e) {

			LOGGER.error("Fehler beim Speichern im Filesystem: " + e.getMessage(), e);
			throw new MkGatewayRuntimeException("Konnte upload nicht ins Filesystem speichern: " + e.getMessage(), e);
		}
	}
}
