// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.feedback;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagen;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import io.quarkus.test.junit.QuarkusTest;

/**
 * ActivateFeedbackDelegateTest
 */
@QuarkusTest
public class ActivateFeedbackDelegateTest {

	final ActivateFeedbackDelegate delegate = new ActivateFeedbackDelegate();

	@Nested
	class FalseTests {

		@Test
		void should_canActivateFeedbackReturnFalse_when_statusWettbewerbErfasst_and_zugangErteilt() {

			// Arrange
			WettbewerbStatus statusWettbewerb = WettbewerbStatus.ERFASST;
			ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.ERTEILT;

			// Act + Assert
			assertFalse(delegate.canActivateFeedback(statusWettbewerb, zugangUnterlagen));

		}

		@Test
		void should_canActivateFeedbackReturnFalse_when_statusWettbewerbErfasst_and_zugangDefaut() {

			// Arrange
			WettbewerbStatus statusWettbewerb = WettbewerbStatus.ERFASST;
			ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.DEFAULT;

			// Act + Assert
			assertFalse(delegate.canActivateFeedback(statusWettbewerb, zugangUnterlagen));

		}

		@Test
		void should_canActivateFeedbackReturnFalse_when_statusWettbewerbErfasst_and_zugangEntzogen() {

			// Arrange
			WettbewerbStatus statusWettbewerb = WettbewerbStatus.ERFASST;
			ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.ENTZOGEN;

			// Act + Assert
			assertFalse(delegate.canActivateFeedback(statusWettbewerb, zugangUnterlagen));

		}

		@Test
		void should_canActivateFeedbackReturnFalse_when_statusWettbewerbAnmeldung_and_zugangEntzogen() {

			// Arrange
			WettbewerbStatus statusWettbewerb = WettbewerbStatus.ANMELDUNG;
			ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.ENTZOGEN;

			// Act + Assert
			assertFalse(delegate.canActivateFeedback(statusWettbewerb, zugangUnterlagen));

		}

		@Test
		void should_canActivateFeedbackReturnFalse_when_statusWettbewerbDownloadLehrer_and_zugangEntzogen() {

			// Arrange
			WettbewerbStatus statusWettbewerb = WettbewerbStatus.DOWNLOAD_LEHRER;
			ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.ENTZOGEN;

			// Act + Assert
			assertFalse(delegate.canActivateFeedback(statusWettbewerb, zugangUnterlagen));

		}

		@Test
		void should_canActivateFeedbackReturnFalse_when_statusWettbewerbDownloadPrivat_and_zugangEntzogen() {

			// Arrange
			WettbewerbStatus statusWettbewerb = WettbewerbStatus.DOWNLOAD_PRIVAT;
			ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.ENTZOGEN;

			// Act + Assert
			assertFalse(delegate.canActivateFeedback(statusWettbewerb, zugangUnterlagen));

		}

		@Test
		void should_canActivateFeedbackReturnFalse_when_statusWettbewerbBeendet_and_zugangEntzogen() {

			// Arrange
			WettbewerbStatus statusWettbewerb = WettbewerbStatus.BEENDET;
			ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.ENTZOGEN;

			// Act + Assert
			assertFalse(delegate.canActivateFeedback(statusWettbewerb, zugangUnterlagen));

		}
	}

	@Nested
	class TrueTests {

		@Test
		void should_canActivateFeedbackReturnTrue_when_statusWettbewerbAnmeldung_and_zugangDefault() {

			// Arrange
			WettbewerbStatus statusWettbewerb = WettbewerbStatus.ANMELDUNG;
			ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.DEFAULT;

			// Act + Assert
			assertTrue(delegate.canActivateFeedback(statusWettbewerb, zugangUnterlagen));

		}

		@Test
		void should_canActivateFeedbackReturnTrue_when_statusWettbewerbDownloadLehrer_and_zugangDefault() {

			// Arrange
			WettbewerbStatus statusWettbewerb = WettbewerbStatus.DOWNLOAD_LEHRER;
			ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.DEFAULT;

			// Act + Assert
			assertTrue(delegate.canActivateFeedback(statusWettbewerb, zugangUnterlagen));

		}

		@Test
		void should_canActivateFeedbackReturnTrue_when_statusWettbewerbDownloadPrivat_and_zugangDefault() {

			// Arrange
			WettbewerbStatus statusWettbewerb = WettbewerbStatus.DOWNLOAD_PRIVAT;
			ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.DEFAULT;

			// Act + Assert
			assertTrue(delegate.canActivateFeedback(statusWettbewerb, zugangUnterlagen));

		}

		@Test
		void should_canActivateFeedbackReturnTrue_when_statusWettbewerbBeendet_and_zugangDefault() {

			// Arrange
			WettbewerbStatus statusWettbewerb = WettbewerbStatus.BEENDET;
			ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.DEFAULT;

			// Act + Assert
			assertTrue(delegate.canActivateFeedback(statusWettbewerb, zugangUnterlagen));

		}

		// ///////////////////////////////

		@Test
		void should_canActivateFeedbackReturnTrue_when_statusWettbewerbAnmeldung_and_zugangErteilt() {

			// Arrange
			WettbewerbStatus statusWettbewerb = WettbewerbStatus.ANMELDUNG;
			ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.ERTEILT;

			// Act + Assert
			assertTrue(delegate.canActivateFeedback(statusWettbewerb, zugangUnterlagen));

		}

		@Test
		void should_canActivateFeedbackReturnTrue_when_statusWettbewerbDownloadLehrer_and_zugangErteilt() {

			// Arrange
			WettbewerbStatus statusWettbewerb = WettbewerbStatus.DOWNLOAD_LEHRER;
			ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.ERTEILT;

			// Act + Assert
			assertTrue(delegate.canActivateFeedback(statusWettbewerb, zugangUnterlagen));

		}

		@Test
		void should_canActivateFeedbackReturnTrue_when_statusWettbewerbDownloadPrivat_and_zugangErteilt() {

			// Arrange
			WettbewerbStatus statusWettbewerb = WettbewerbStatus.DOWNLOAD_PRIVAT;
			ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.ERTEILT;

			// Act + Assert
			assertTrue(delegate.canActivateFeedback(statusWettbewerb, zugangUnterlagen));

		}

		@Test
		void should_canActivateFeedbackReturnTrue_when_statusWettbewerbBeendet_and_zugangErteilt() {

			// Arrange
			WettbewerbStatus statusWettbewerb = WettbewerbStatus.BEENDET;
			ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.ERTEILT;

			// Act + Assert
			assertTrue(delegate.canActivateFeedback(statusWettbewerb, zugangUnterlagen));

		}

	}

}
