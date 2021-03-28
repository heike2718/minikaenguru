// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.fileutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;

/**
 * MkGatewayFileUtilsTest
 */
public class MkGatewayFileUtilsTest {

	@Test
	void should_readBytesFromFileSucced() {

		// Arrange
		String path = "/home/heike/mkv/adv/adv-vereinbarung-1.1.pdf";

		// Act
		byte[] daten = MkGatewayFileUtils.readBytesFromFile(path);

		// Assert
		assertEquals(97575, daten.length);
	}

	@Test
	void should_readBytesFromFileThrowException_when_DateiNichtDa() {

		// Arrange
		String path = "/home/heike/mkv/adv/adv-vereinbarung-10.0.pdf";

		// Act + Assert
		try {

			MkGatewayFileUtils.readBytesFromFile(path);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals("Konnte Datei mit Pfad /home/heike/mkv/adv/adv-vereinbarung-10.0.pdf nicht laden", e.getMessage());
		}
	}

	@Test
	void should_createDownloadResponseSetAllHeaders() {

		// Arrange
		DownloadData downloadData = new DownloadData("xxx.pdf", "Hallo".getBytes());

		// Act
		Response response = MkGatewayFileUtils.createDownloadResponse(downloadData);

		// Assert
		String contentTypHeader = response.getHeaderString("Content-Type");
		assertEquals("application/octet-stream", contentTypHeader);

		assertEquals("attachement; filename=xxx.pdf", response.getHeaderString("Content-Disposition"));

		byte[] entity = (byte[]) response.getEntity();

		assertEquals("Hallo", new String(entity));

	}
}
