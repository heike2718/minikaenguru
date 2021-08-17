// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;

/**
 * MultipartUtils
 */
public class MultipartUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(MultipartUtils.class);

	/**
	 * Fummelt die UploadData aus dem input heraus. Es wird davon ausgegangen, dass genau eine Datei hochgeladen wurde. Es handelt
	 * sich um eine Datei mit Textdaten, also Excel, OpenOffice, CSV. Als Charset wird UTF-8 angenommen.
	 *
	 * @param  input
	 * @return
	 * @throws IOException
	 */
	public static UploadData getUploadDataFromTextSource(final MultipartFormDataInput input) {

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("uploadedFile");

		if (inputParts.size() != 1) {

			LOGGER.error("Anzahl hochgeladene Datein=" + inputParts.size());
			throw new MkGatewayRuntimeException("Unerwartete Anzahl hochgeladener Dateien: kann nur genau eine Datei verarbeiten.");
		}

		InputPart inputPart = inputParts.get(0);

		MultivaluedMap<String, String> header = inputPart.getHeaders();
		String fileName = getFileName(header);

		try (InputStream inputStream = inputPart.getBody(InputStream.class, null); StringWriter sw = new StringWriter()) {

			IOUtils.copy(inputStream, sw, Charset.forName("UTF-8"));
			return new UploadData(fileName, sw.toString().getBytes());
		} catch (IOException e) {

			LOGGER.error("Exception beim Umwandeln des uploads: " + e.getMessage(), e);
			throw new MkGatewayRuntimeException("IOException beim Verarbeiten des MultipartFormDataInput");
		}

	}

	/**
	 * Fummelt die UploadData aus dem input heraus. Es wird davon ausgegangen, dass genau eine Datei hochgeladen wurde. Es handelt
	 * sich um eine Datei mit Binärdaten, also PDF, Image etc.
	 *
	 * @param  input
	 * @return
	 * @throws IOException
	 */
	public static UploadData getUploadDataFromBinarySource(final MultipartFormDataInput input) {

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("uploadedFile");

		if (inputParts.size() != 1) {

			LOGGER.error("Anzahl hochgeladene Datein=" + inputParts.size());
			throw new MkGatewayRuntimeException("Unerwartete Anzahl hochgeladener Dateien: kann nur genau eine Datei verarbeiten.");
		}

		InputPart inputPart = inputParts.get(0);

		MultivaluedMap<String, String> header = inputPart.getHeaders();
		String fileName = getFileName(header);

		try (InputStream inputStream = inputPart.getBody(InputStream.class, null);
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
