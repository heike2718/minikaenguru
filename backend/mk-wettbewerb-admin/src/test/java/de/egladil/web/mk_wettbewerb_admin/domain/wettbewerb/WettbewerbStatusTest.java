// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * WettbewerbStatusTest
 */
public class WettbewerbStatusTest {

	@Test
	void should_nextStatus_beImplementedCorrectly() {

		// Arrange
		WettbewerbStatus currentStatus = WettbewerbStatus.nextStatus(null);
		assertEquals(WettbewerbStatus.ERFASST, currentStatus);

		currentStatus = WettbewerbStatus.nextStatus(currentStatus);
		assertEquals(WettbewerbStatus.ANMELDUNG, currentStatus);

		currentStatus = WettbewerbStatus.nextStatus(currentStatus);
		assertEquals(WettbewerbStatus.DOWNLOAD_LEHRER, currentStatus);

		currentStatus = WettbewerbStatus.nextStatus(currentStatus);
		assertEquals(WettbewerbStatus.DOWNLOAD_PRIVAT, currentStatus);

		currentStatus = WettbewerbStatus.nextStatus(currentStatus);
		assertEquals(WettbewerbStatus.BEENDET, currentStatus);
	}

	@Test
	void should_nextStatusThrowException_when_CurrentStatusBeendet() {

		try {

			WettbewerbStatus.nextStatus(WettbewerbStatus.BEENDET);
		} catch (IllegalStateException e) {

			assertEquals("Wettbewerb hat sein Lebensende erreicht. Es gibt keinen Folgestatus.", e.getMessage());
		}
	}

}
