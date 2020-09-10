// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.fileutils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;

/**
 * MkGatewayFileUtils
 */
public final class MkGatewayFileUtils {

	private static final Logger LOG = LoggerFactory.getLogger(MkGatewayFileUtils.class);

	/**
	 *
	 */
	private MkGatewayFileUtils() {

	}

	public static byte[] readBytesFromFile(final String path) {

		try (InputStream in = new FileInputStream(new File(path)); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

			IOUtils.copy(in, bos);

			final byte[] result = bos.toByteArray();
			bos.flush();

			return result;

		} catch (IOException e) {

			LOG.error(e.getMessage(), e);
			throw new MkGatewayRuntimeException("Konnte Datei mit Pfad " + path + " nicht laden");
		}
	}

}
