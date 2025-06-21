// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.impl;

import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.UploadThreadDetected;
import de.egladil.web.mk_gateway.domain.event.VirusDetected;
import de.egladil.web.mk_gateway.domain.uploads.UploadRequestPayload;
import de.egladil.web.mk_gateway.domain.uploads.scan.FileScanResult;
import de.egladil.web.mk_gateway.domain.uploads.scan.ScanRequestPayload;
import de.egladil.web.mk_gateway.domain.uploads.scan.ThreadDetection;
import de.egladil.web.mk_gateway.domain.uploads.scan.Upload;
import de.egladil.web.mk_gateway.domain.uploads.scan.VirusDetection;
import de.egladil.web.mk_gateway.infrastructure.restclient.FilescannerRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.Response;

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

	@RestClient
	@Inject
	FilescannerRestClient fileScannerClient;

	@Inject
	DomainEventHandler domainEventHandler;

	private boolean isIT;

	public static final UploadScannerDelegate createForIntegrationTest(final EntityManager em) {

		UploadScannerDelegate result = new UploadScannerDelegate();
		result.clientId = "integration-test-client";
		result.domainEventHandler = DomainEventHandler.createForIntegrationTest(em);
		result.maxFilesizeBytes = "2097152";
		result.isIT = true;
		return result;
	}

	public FileScanResult scanUpload(final UploadRequestPayload uploadPayload) {

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

		Upload upload = uploadPayload.getUploadData().toUpload();
		ScanRequestPayload scanRequestPayload = new ScanRequestPayload().withClientId(clientId)
			.withFileOwner(fileOwnerId).withUpload(upload);

		Response response = null;

		if (isIT) {

			response = Response.ok(createFileScanResultForIT(uploadPayload)).build();

		} else {

			response = fileScannerClient.scanUpload(scanRequestPayload);
		}

		if (response.getStatus() != 200) {

			String message = "Beim Scannen des Uploads ist ein Fehler aufgetreten: filescanner response not ok: status="
				+ response.getStatus();
			LOGGER.error(message);
			throw new MkGatewayRuntimeException(message);
		}

		FileScanResult scanResult = response.readEntity(FileScanResult.class);

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

	private FileScanResult createFileScanResultForIT(final UploadRequestPayload uploadPayload) {

		VirusDetection vd = new VirusDetection();
		ThreadDetection td = new ThreadDetection();

		FileScanResult result = new FileScanResult()
			.withMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").withThreadDetection(td)
			.withVirusDetection(vd).withUploadName(uploadPayload.getUploadData().getFilename())
			.withUserID(uploadPayload.getBenutzerID().identifier());

		return result;
	}

}
