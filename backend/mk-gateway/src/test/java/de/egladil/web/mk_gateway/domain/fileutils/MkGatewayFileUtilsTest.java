// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.fileutils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

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
		String path = "/home/heike/git/testdaten/minikaenguru/adv/adv-vereinbarung-1.1.pdf";

		// Act
		byte[] daten = MkGatewayFileUtils.readBytesFromFile(path);

		// Assert
		assertEquals(97575, daten.length);
	}

	@Test
	void should_readBytesFromFileThrowException_when_DateiNichtDa() {

		// Arrange
		String path = "/home/heike/git/testdaten/minikaenguru/adv/adv-vereinbarung-10.0.pdf";

		// Act + Assert
		try {

			MkGatewayFileUtils.readBytesFromFile(path);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals("Konnte Datei mit Pfad /home/heike/git/testdaten/minikaenguru/adv/adv-vereinbarung-10.0.pdf nicht laden",
				e.getMessage());
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

	@Test
	void should_readLinesThrowMkGatewayException_when_FileDoesNotExist() {

		// Arrange
		String path = "/home/heike/git/minikaenguru/missing-file.csv";

		// Act + Assert
		try {

			MkGatewayFileUtils.readLines(path, "UTF-8");
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals(
				"Fehler beim Laden der Textdatei /home/heike/git/minikaenguru/missing-file.csv: /home/heike/git/minikaenguru/missing-file.csv (Datei oder Verzeichnis nicht gefunden)",
				e.getMessage());
		}

	}

	@Test
	void should_readFileContent_IgnoreEmptyLines() {

		// Arrange
		String path = "/home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/klassenliste-mit-leerzeilen.csv";

		// Act
		List<String> lines = MkGatewayFileUtils.readLines(path, MkGatewayFileUtils.DEFAULT_ENCODING);

		// Assert
		assertEquals(11, lines.size());
		assertEquals("Vorname;Nachname;Klasse;Klassenstufe", lines.get(0));
		assertEquals("Lukas;Granach;1a;1.0", lines.get(1));
		assertEquals("Nathalie;Weiß;1a;1.0", lines.get(2));
		assertEquals("Szymon;Wanowski;1b;1.0", lines.get(3));
		assertEquals("Patrick;Schöner;1b;1.0", lines.get(4));
		assertEquals("Lennart;Hofstedter;2a;2.0", lines.get(5));
		assertEquals("Flora;Gfauna;2a;2.0", lines.get(6));
		assertEquals("Pauline;Gröblin;2b;2.0", lines.get(7));
		assertEquals("Lucie;Hinremöller;2b;2.0", lines.get(8));
		assertEquals(";;;", lines.get(9));
		assertEquals(";;;", lines.get(10));
	}
}
