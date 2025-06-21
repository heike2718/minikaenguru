// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.katalogantrag;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.egladil.web.mk_kataloge.domain.apimodel.SchulkatalogAntrag;
import de.egladil.web.mk_kataloge.domain.event.LoggableEventDelegate;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * KatalogAntragServiceTest
 */
@QuarkusTest
public class KatalogAntragServiceTest {

	@ConfigProperty(name = "bccEmpfaengerSchulkatalogantrag", defaultValue = "minikaenguru@egladil.de")
	String bccEmpfaenger;

	@InjectMock
	KatalogMailService katalogMailService;

	@InjectMock
	LoggableEventDelegate eventDelegate;

	@Inject
	KatalogAntragService service;

	@Test
	void should_NotSendMail_when_HoneypotNotBlank() {

		// Arrange
		SchulkatalogAntrag antrag = SchulkatalogAntrag.createForTest("mail@web.de", "land", "ort", null, "schulname", null, "hagh");

		// Act
		boolean sent = service.validateAndSend(antrag);

		// Assert
		assertFalse(sent);
		verify(eventDelegate).fireSecurityEvent(any(), any());
		verify(katalogMailService, never()).sendMail(any());

	}

	@Test
	void should_SendMailAndFireEvent_when_AntragValid() throws JsonProcessingException {

		// Arrange
		SchulkatalogAntrag antrag = SchulkatalogAntrag.createForTest("mail@web.de", "land", "ort", null, "schulname", null,
			"");

		doNothing().when(eventDelegate).fireKatalogAntragReceived(any(), any());

		// Act
		boolean sent = service.validateAndSend(antrag);

		// Assert
		assertTrue(sent);
		verify(eventDelegate, never()).fireSecurityEvent(any(), any());
		verify(eventDelegate).fireKatalogAntragReceived(any(), any());
	}

}
