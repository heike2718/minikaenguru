// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.impl;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenlisteImportService;
import de.egladil.web.mk_gateway.domain.uploads.UploadAuthorizationService;
import de.egladil.web.mk_gateway.domain.uploads.UploadIdentifier;
import de.egladil.web.mk_gateway.domain.uploads.UploadManager;
import de.egladil.web.mk_gateway.domain.uploads.UploadRepository;
import de.egladil.web.mk_gateway.domain.uploads.UploadRequestPayload;
import de.egladil.web.mk_gateway.domain.uploads.UploadStatus;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;
import de.egladil.web.mk_gateway.infrastructure.upload.ScanResult;

/**
 * UploadManagerImpl
 */
@ApplicationScoped
public class UploadManagerImpl implements UploadManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadManagerImpl.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@ConfigProperty(name = "upload.folder.path")
	String pathUploadDir;

	@Inject
	AuthorizationService authService;

	@Inject
	UploadAuthorizationService uploadAuthService;

	@Inject
	FilescannerRestClientAdapter filescannerAdapter;

	@Inject
	KlassenlisteImportService klassenlisteImportService;

	@Inject
	UploadRepository uploadRepository;

	@Override
	public boolean authorizeUpload(final String veranstalterUuid, final String teilnahmenummer, final UploadType uploadType) {

		Identifier veranstalterID = new Identifier(veranstalterUuid);
		Identifier teilnameID = new Identifier(teilnahmenummer);

		authService.checkPermissionForTeilnahmenummer(veranstalterID, teilnameID, "upload Klassenliste");

		uploadAuthService.authorizeUpload(veranstalterID, teilnahmenummer, uploadType);

		return true;
	}

	ScanResult scanUpload(final UploadRequestPayload uploadPayload) {

		Response fileScanResponse = filescannerAdapter.checkUpload(uploadPayload);

		if (fileScanResponse.getStatus() >= 400) {

			LOGGER.error("filescanner returned status " + fileScanResponse.getStatus() + ": upload scheitert.");
			throw new MkGatewayRuntimeException("Fehler beim Scannen der Hochgeladenen Datei");

		}

		ScanResult scanResult = new ResponseToScanResultMapper().getScanResult(fileScanResponse);

		if (scanResult.getVirusDetection() != null) {

			// TODO: event erzeugen und speichern bzw. Telegram-Message, dann DangerousUploadException werfen
			throw new UploadFormatException(applicationMessages.getString("upload.dangerousContent"));

		}

		if (scanResult.getThreadDetection() != null) {

			// TODO: event erzeugen und speichern bzw. Telegram-Message, dann DangerousUploadException werfen
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

			break;

		case EXCEL_ALT:
		case EXCEL_NEU:
			// TODO Hier pandas bemühen
			break;

		default:
			LOGGER.error("Unerwarteter DateiTyp {} - brechen ab.", dateiTyp);
			throw new UploadFormatException(applicationMessages.getString("upload.unbekannterMediaType"));
		}

		return null;
	}

	@Transactional
	PersistenterUpload transformAndPersistUpload(final UploadRequestPayload uploadPayload, final ScanResult scanResult, final Long checksumme) {

		PersistenterUpload upload = new PersistenterUpload();
		upload.setCharset(scanResult.getCharset());
		upload.setChecksumme(checksumme);
		upload.setDateiname(uploadPayload.getUploadData().getFilename());
		upload.setMediatype(scanResult.getMediaType());
		upload.setStatus(UploadStatus.HOCHGELADEN);
		upload.setTeilnahmenummer(uploadPayload.getSchuleID().identifier());
		upload.setUploadTyp(uploadPayload.getUploadType());
		upload.setVeranstalterUuid(uploadPayload.getVeranstalterID().identifier());

		return null;
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

		PersistenterUpload persistenterUpload = transformAndPersistUpload(uploadPayload, scanResult, checksumme);

		switch (uploadPayload.getUploadType()) {

		case KLASSENLISTE:

			break;

		default:
			break;
		}

		return null;
	}
}
