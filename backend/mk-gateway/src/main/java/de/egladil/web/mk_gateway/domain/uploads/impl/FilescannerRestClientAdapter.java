// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.AbstractMkResourceAdapter;
import de.egladil.web.mk_gateway.domain.uploads.UploadRequestPayload;
import de.egladil.web.mk_gateway.infrastructure.upload.FileScannerRestClient;
import de.egladil.web.mk_gateway.infrastructure.upload.ScanRequestPayload;
import de.egladil.web.mk_gateway.infrastructure.upload.Upload;

/**
 * FilescannerRestClientAdapter
 */
@RequestScoped
public class FilescannerRestClientAdapter extends AbstractMkResourceAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(UploadManagerImpl.class);

	@ConfigProperty(name = "mkv-app.client-id")
	String clientId;

	@Inject
	@RestClient
	FileScannerRestClient fileScannerRestClient;

	/**
	 * Jagt den Upload durch den Filescanner zum Virencheck sowie zur Content-Detection.
	 *
	 * @param  uploadPayload
	 * @return               Response mit ScanResult als Payload (kommt vom filescanner)
	 */
	public Response checkUpload(final UploadRequestPayload uploadPayload) {

		Upload upload = new Upload().withName(uploadPayload.getUploadData().getFilename())
			.withData(uploadPayload.getUploadData().getDataBASE64());
		ScanRequestPayload scanPayload = new ScanRequestPayload().withClientId(clientId)
			.withFileOwner(uploadPayload.getVeranstalterID().identifier()).withUpload(upload);

		try {

			return fileScannerRestClient.scanFile(scanPayload);

		} catch (Exception e) {

			return handleException(e, LOG, "[checkUpload]");
		}
	}

	@Override
	protected String endpointName() {

		return "filecanner";
	}
}
