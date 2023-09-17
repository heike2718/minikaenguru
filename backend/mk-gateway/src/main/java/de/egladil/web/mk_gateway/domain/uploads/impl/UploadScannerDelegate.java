// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.impl;

import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.filescanner_service.clamav.VirusDetection;
import de.egladil.web.filescanner_service.scan.ScanRequestPayload;
import de.egladil.web.filescanner_service.scan.ScanResult;
import de.egladil.web.filescanner_service.scan.ScanService;
import de.egladil.web.filescanner_service.securitychecks.ThreadDetection;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.UploadThreadDetected;
import de.egladil.web.mk_gateway.domain.event.VirusDetected;
import de.egladil.web.mk_gateway.domain.uploads.UploadRequestPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

/**
 * UploadScannerDelegate
 */
@ApplicationScoped
public class UploadScannerDelegate {

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadScannerDelegate.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@ConfigProperty(name = "upload.max.bytes")
	String maxFilesizeBytes;

	@ConfigProperty(name = "mkv-app.client-id")
	String clientId;

	@Inject
	ScanService scanService;

	@Inject
	DomainEventHandler domainEventHandler;

	public static final UploadScannerDelegate createForIntegrationTest(final EntityManager em) {

		UploadScannerDelegate result = new UploadScannerDelegate();
		result.scanService = ScanService.createForIntegrationTest();
		result.clientId = "integration-test-client";
		result.domainEventHandler = DomainEventHandler.createForIntegrationTest(em);
		result.maxFilesizeBytes = "2097152";
		return result;
	}

	public ScanResult scanUpload(final UploadRequestPayload uploadPayload) {

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

}
