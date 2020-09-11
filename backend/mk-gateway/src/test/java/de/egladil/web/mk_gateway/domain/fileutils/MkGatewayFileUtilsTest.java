// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.fileutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

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
}
