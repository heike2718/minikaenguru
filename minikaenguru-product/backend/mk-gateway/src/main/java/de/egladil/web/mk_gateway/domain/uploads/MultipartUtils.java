// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.reactive.server.multipart.FormValue;
import org.jboss.resteasy.reactive.server.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import jakarta.ws.rs.core.MultivaluedMap;

/**
 * MultipartUtils
 */
public class MultipartUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(MultipartUtils.class);

	/**
	 * Fummelt die UploadData aus dem input heraus. Es wird davon ausgegangen, dass genau eine Datei hochgeladen wurde.
	 *
	 * @param  input
	 * @return
	 * @throws IOException
	 */
	public static UploadData getUploadData(final MultipartFormDataInput input) {

		Map<String, Collection<FormValue>> uploadForm = input.getValues();
		Collection<FormValue> inputParts = uploadForm.get("uploadedFile");

		if (inputParts.size() != 1) {

			LOGGER.error("Anzahl hochgeladene Datein=" + inputParts.size());
			throw new MkGatewayRuntimeException("Unerwartete Anzahl hochgeladener Dateien: kann nur genau eine Datei verarbeiten.");
		}

		FormValue formPart = inputParts.iterator().next();

		MultivaluedMap<String, String> header = formPart.getHeaders();
		String fileName = getFileName(header);

		try (InputStream inputStream = formPart.getFileItem().getInputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

			IOUtils.copy(inputStream, bos);

			final byte[] result = bos.toByteArray();
			bos.flush();

			return new UploadData(fileName, result);

		} catch (IOException e) {

			LOGGER.error("Exception beim Umwandeln des uploads: " + e.getMessage(), e);
			throw new MkGatewayRuntimeException("IOException beim Verarbeiten des MultipartFormDataInput");
		}
	}

	private static String getFileName(final MultivaluedMap<String, String> header) {

		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

		for (String filename : contentDisposition) {

			if ((filename.trim().startsWith("filename"))) {

				String[] name = filename.split("=");

				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return "unknown";
	}

}
