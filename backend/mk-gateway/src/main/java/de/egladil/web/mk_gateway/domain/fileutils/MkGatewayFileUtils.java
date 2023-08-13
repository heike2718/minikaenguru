// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.fileutils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;

/**
 * MkGatewayFileUtils
 */
public final class MkGatewayFileUtils {

	/**
	 *
	 */
	public static final String DEFAULT_ENCODING = "UTF-8";

	private static final String CONTENT_TYPE_HEADER = "Content-Type";

	private static final String CONTENT_DISPOSITION_HEADER_NAME = "Content-Disposition";

	private static final String CONTENT_DISPOSITION_MF = "attachement; filename={0}";

	private static final Logger LOGGER = LoggerFactory.getLogger(MkGatewayFileUtils.class);

	/**
	 *
	 */
	private MkGatewayFileUtils() {

	}

	public static byte[] readBytesFromFile(final String path) {

		File file = new File(path);

		try (InputStream in = new FileInputStream(file); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

			IOUtils.copy(in, bos);

			final byte[] result = bos.toByteArray();
			bos.flush();

			return result;

		} catch (IOException e) {

			LOGGER.error(e.getMessage(), e);
			throw new MkGatewayRuntimeException("Konnte Datei mit Pfad " + path + " nicht laden", e);
		}
	}

	/**
	 * Bastelt einen APPLICATION_OCTET_STREAM aus den DownloadData.
	 *
	 * @param  downloadData
	 * @return              Response
	 */
	public static Response createDownloadResponse(final DownloadData downloadData) {

		String filename = downloadData.filename();
		String contentDisposition = MessageFormat.format(CONTENT_DISPOSITION_MF, filename);

		return Response.ok(downloadData.data()).header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_OCTET_STREAM)
			.header(CONTENT_DISPOSITION_HEADER_NAME, contentDisposition).build();
	}

	/**
	 * Liest die Datei zeilenweise ein.
	 *
	 * @param  path
	 *                  Pfad zu einer Textdatei.
	 * @param  encoding
	 *                  String - darf null sein. Wenn null, dann wird UTF-8 verwendet.
	 * @return          List
	 */
	public static List<String> readLines(final String path, final String encoding) {

		File file = new File(path);

		try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {

			List<String> lines = new ArrayList<>();
			String line = null;
			int index = 0;

			while ((line = br.readLine()) != null) {

				if (StringUtils.isNotBlank(line)) {

					String converted = convertToUnicode(line, encoding);
					lines.add(index++, converted);
				}
			}

			return lines;

		} catch (IOException e) {

			String msg = "Fehler beim Laden der Textdatei " + path + ": " + e.getMessage();
			LOGGER.error(msg, e);

			throw new MkGatewayRuntimeException(msg, e);

		}
	}

	static String convertToUnicode(final String line, final String encoding) {

		String theEncoding = encoding == null ? DEFAULT_ENCODING : encoding;

		if (DEFAULT_ENCODING.equals(theEncoding)) {

			return line;
		}

		byte[] bytes = line.getBytes(Charset.forName(theEncoding));
		return new String(bytes, Charset.forName(DEFAULT_ENCODING));
	}

	public static File writeLines(final List<String> lines, final String path) {

		File file = new File(path);

		String content = StringUtils.join(lines, "\n") + "\n";

		try (FileOutputStream fos = new FileOutputStream(file); InputStream in = new ByteArrayInputStream(content.getBytes())) {

			IOUtils.copy(in, fos);
			fos.flush();

			return file;
		} catch (IOException e) {

			LOGGER.error("Fehler beim Speichern im Filesystem: " + e.getMessage(), e);
			throw new MkGatewayRuntimeException("Konnte lines nicht Filesystem speichern: " + e.getMessage(), e);
		}
	}

	/**
	 * Löscht die gegebene Datei und Loggt Exceptons als Warnung.
	 *
	 * @param file
	 * @param logger
	 */
	public static void deleteFileWithErrorLogQuietly(final File file, final Logger logger) {

		if (file.exists() && file.isFile()) {

			try {

				file.delete();
			} catch (Exception e) {

				logger.warn("{} konnte nicht geloescht werden: {}", file.getAbsolutePath(),
					e.getMessage(), e);

			}
		}
	}
}
