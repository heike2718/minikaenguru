// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.health;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;

import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Pacemaker;
import de.egladil.web.mk_kataloge.profiles.FullDatabaseTestProfile;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;

/**
 * HeartbeatServiceTest
 */
@QuarkusTest
@TestProfile(value = FullDatabaseTestProfile.class)
public class HeartbeatServiceTest {

	@InjectMock
	PacemakerRepository pacemakerRepository;

	@Inject
	HeartbeatService service;

	@Test
	void should_checkReturn500_when_exceptionBeimSpeichern() {

		// Arrange
		when(pacemakerRepository.change(any(Pacemaker.class))).thenThrow(new RuntimeException("schlimm schlmm"));

		// Act
		service.updatePacemaker();
	}

}
