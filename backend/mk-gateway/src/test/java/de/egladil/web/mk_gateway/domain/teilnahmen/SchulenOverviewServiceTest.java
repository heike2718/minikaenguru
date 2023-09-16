// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.statistik.AuswertungsmodusInfoService;
import de.egladil.web.mk_gateway.domain.veranstalter.Lehrer;
import de.egladil.web.mk_gateway.domain.veranstalter.Person;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;

/**
 * SchulenAnmeldeinfoServiceWithInMemoryDatabaseTest
 */
@QuarkusTest
public class SchulenOverviewServiceTest {

	private static final String UUID_LEHRER_1 = "UUID_LEHRER_1";

	private static final String SCHULKUERZEL_1 = "SCHULKUERZEL_1";

	@Inject
	SchulenOverviewService service;

	@InjectMock
	AuswertungsmodusInfoService auswertungsmodusInfoService;

	@InjectMock
	VeranstalterRepository veranstalterRepository;

	@InjectMock
	AktuelleTeilnahmeService aktuelleTeilnahmeService;

	@InjectMock
	DomainEventHandler domainEventHandler;

	@InjectMock
	LoggableEventDelegate eventDelegate;

	@Test
	void should_ermittleAnmeldedatenFuerSchulenReturnSchulen_when_zweiSchulen() {

		// Arrange
		Identifier identifier = new Identifier(UUID_LEHRER_1);

		List<Identifier> schulenIDs = new ArrayList<>();
		schulenIDs.add(new Identifier(SCHULKUERZEL_1));
		schulenIDs.add(new Identifier(UUID_LEHRER_1));

		Veranstalter veranstalter = new Lehrer(new Person(UUID_LEHRER_1, UUID_LEHRER_1), false,
			schulenIDs);
		when(veranstalterRepository.ofId(new Identifier(UUID_LEHRER_1))).thenReturn(Optional.of(veranstalter));

		// Act
		List<SchuleAPIModel> schulen = service.ermittleAnmeldedatenFuerSchulen(identifier);

		// Assert
		assertEquals(2, schulen.size());
		verify(eventDelegate, never()).fireSecurityEvent(any(), any());
	}

	@Test
	void should_ermittleAnmeldedatenFuerSchulenReturnSchulen_when_eineSchulen() {

		// Arrange
		Identifier identifier = new Identifier(UUID_LEHRER_1);

		List<Identifier> schulenIDs = new ArrayList<>();
		schulenIDs.add(new Identifier(SCHULKUERZEL_1));

		Veranstalter veranstalter = new Lehrer(new Person(UUID_LEHRER_1, UUID_LEHRER_1), false,
			schulenIDs);
		when(veranstalterRepository.ofId(new Identifier(UUID_LEHRER_1))).thenReturn(Optional.of(veranstalter));

		// Act
		List<SchuleAPIModel> schulen = service.ermittleAnmeldedatenFuerSchulen(identifier);

		// Assert
		assertEquals(1, schulen.size());
		verify(eventDelegate, never()).fireSecurityEvent(any(), any());

	}

	@Test
	void should_ermittleAnmeldedatenFuerSchulen_returnEmptyArray_when_LehrerUnbekannt() {

		// Arrange
		when(veranstalterRepository.ofId(new Identifier(UUID_LEHRER_1))).thenReturn(Optional.empty());

		// Act
		List<SchuleAPIModel> schulen = service.ermittleAnmeldedatenFuerSchulen(new Identifier(UUID_LEHRER_1));

		// Assert
		assertEquals(0, schulen.size());
		verify(eventDelegate).fireSecurityEvent(any(), any());
	}
}
