// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.meldungen;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.infrastructure.rest.meldungen.Meldung;

/**
 * MeldungenServiceTest
 */
public class MeldungenServiceTest {

	@Test
	void should_write_and_read() throws IOException {

		// Arrange
		File file = File.createTempFile("meldung-", ".json");
		file.deleteOnExit();

		MeldungenService service = MeldungenService.createForTest(file.getAbsolutePath());

		long currentTime = System.currentTimeMillis();
		Meldung meldung = new Meldung("Aktuelle Zeit = " + currentTime);

		// Act
		service.saveMeldng(meldung);

		// Assert
		Meldung persistent = service.loadMeldung();

		assertEquals("Aktuelle Zeit = " + currentTime, persistent.getText());

	}

}
