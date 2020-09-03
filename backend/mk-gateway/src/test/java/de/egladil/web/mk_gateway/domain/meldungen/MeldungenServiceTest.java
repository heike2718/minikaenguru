// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.meldungen;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * MeldungenServiceTest
 */
public class MeldungenServiceTest {

	@Test
	void should_write_and_read() throws IOException {

		// Arrange
		MeldungenService service = MeldungenService.createForTest(System.getProperty("java.io.tmpdir"));

		long currentTime = System.currentTimeMillis();
		Meldung meldung = new Meldung("Aktuelle Zeit = " + currentTime);

		// Act
		service.saveMeldng(meldung);

		// Assert
		Meldung persistent = service.loadMeldung();

		assertEquals("Aktuelle Zeit = " + currentTime, persistent.getText());

	}

}
