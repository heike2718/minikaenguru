// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.impl;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;
import de.egladil.web.mk_gateway.domain.uploads.UploadAuthorizationService;
import de.egladil.web.mk_gateway.domain.uploads.UploadManager;
import de.egladil.web.mk_gateway.domain.uploads.UploadRequestPayload;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;
import de.egladil.web.mk_gateway.infrastructure.upload.ScanResult;

/**
 * UploadManagerImpl
 */
@ApplicationScoped
public class UploadManagerImpl implements UploadManager {

	private static final String MEDIA_TYPE_EXCEL = "";

	private static final String MEDIA_TYPE_EXCEL_ALT = "";

	private static final String MEDIA_ODS = "";

	private static final String MEDIA_TYPE_TEXT_CSV = "";

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

	@Override
	public boolean authorizeUpload(final String veranstalterUuid, final String teilnahmenummer, final UploadType uploadType) {

		Identifier veranstalterID = new Identifier(veranstalterUuid);
		Identifier teilnameID = new Identifier(teilnahmenummer);

		authService.checkPermissionForTeilnahmenummer(veranstalterID, teilnameID, "upload Klassenliste");

		uploadAuthService.authorizeUpload(veranstalterID, teilnahmenummer, uploadType);

		return true;
	}

	@Override
	public ScanResult scanUpload(final UploadRequestPayload uploadPayload) {

		Response fileScanResponse = filescannerAdapter.checkUpload(uploadPayload);

		if (fileScanResponse.getStatus() >= 400) {

			LOGGER.error("filescanner returned status " + fileScanResponse.getStatus() + ": upload scheitert.");
			throw new MkGatewayRuntimeException("Fehler beim Scannen der Hochgeladenen Datei");

		}

		ScanResult scanResult = new ResponseToScanResultMapper().getScanResult(fileScanResponse);

		if (scanResult.getVirusDetection() != null) {

		}

		if (scanResult.getThreadDetection() != null) {

		}

		DateiTyp dateiTyp = DateiTyp.valueOfTikaName(scanResult.getMediaType());

		if (dateiTyp == null) {

			LOGGER.error("Unbekannter MediaType {} - brechen ab.", scanResult.getMediaType());
			throw new UploadFormatException(applicationMessages.getString("upload.unbekannterMediaType"));
		}

		switch (dateiTyp) {

		case TEXT:
			break;

		case OOS:

			break;

		case EXCEL_ALT:
		case EXCEL_NEU:
			// Hier pandas bemühen
			break;

		default:
			LOGGER.error("Unerwarteter DateiTyp {} - brechen ab.", dateiTyp);
			throw new UploadFormatException(applicationMessages.getString("upload.unbekannterMediaType"));
		}

		return null;
	}

	@Override
	public PersistenterUpload transformAndPersistUpload(final UploadRequestPayload uploadPayload, final ScanResult scanResult) {

		return null;
	}
}
