// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.veranstalter.Lehrer;
import de.egladil.web.mk_gateway.domain.veranstalter.Person;
import de.egladil.web.mk_gateway.domain.veranstalter.Privatveranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagen;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VeranstalterSuchanfrage;

/**
 * VeranstalterNewsletterServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class VeranstalterNewsletterServiceTest {

	@Mock
	private VeranstalterRepository veranstalterRepository;

	@InjectMocks
	private VeranstalterNewsletterService service;

	@Test
	void should_aendereVeranstalterReturnErrorResponsePayload_when_keinTrefferMitUuidFragment() {

		// Arrange
		String uuidPrefix = "80c8052f";
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage().withSuchkriterium(VeranstalterSuchkriterium.UUID)
			.withSuchstring(uuidPrefix);

		when(veranstalterRepository.findVeranstalter(suchanfrage)).thenReturn(new ArrayList<>());

		// Act
		ResponsePayload result = service.aendereVeranstalter(uuidPrefix, null);

		// Assert
		MessagePayload messagePayload = result.getMessage();
		assertEquals("ERROR", messagePayload.getLevel());
		assertEquals("Veranstalter mit UUID like '80c8052f%' existiert nicht.", messagePayload.getMessage());
	}

	@Test
	void should_aendereVeranstalterReturnErrorResponsePayload_when_mehrAlsEinTrefferMitUuidFragment() {

		// Arrange
		String uuidPrefix = "80c8052f";
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage().withSuchkriterium(VeranstalterSuchkriterium.UUID)
			.withSuchstring(uuidPrefix);

		List<Veranstalter> veranstalter = new ArrayList<>();
		veranstalter.add(new Lehrer(new Person(uuidPrefix + "-guigsdiqg", "Harald Schulze"), true,
			Arrays.asList(new Identifier[] { new Identifier("hqdhoq") })));

		veranstalter.add(new Privatveranstalter(new Person(uuidPrefix + "-ugquggo", "Lieschen Müller"), true,
			Arrays.asList(new Identifier[] { new Identifier("hqdhoq") })));

		when(veranstalterRepository.findVeranstalter(suchanfrage)).thenReturn(veranstalter);

		// Act
		ResponsePayload result = service.aendereVeranstalter(uuidPrefix, null);

		// Assert
		MessagePayload messagePayload = result.getMessage();
		assertEquals("ERROR", messagePayload.getLevel());
		assertEquals("2 Veranstalter mit UUID like '80c8052f%' gefunden.", messagePayload.getMessage());
	}

	@Test
	void should_aendereVeranstalterReturnInfoNotChangedResponsePayload_when_trefferUndNewsletterBereitsDeaktiviert() {

		// Arrange
		String uuidPrefix = "80c8052f";
		ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.ERTEILT;
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage().withSuchkriterium(VeranstalterSuchkriterium.UUID)
			.withSuchstring(uuidPrefix);

		List<Veranstalter> veranstalter = new ArrayList<>();
		veranstalter.add(new Lehrer(new Person(uuidPrefix + "-guigsdiqg", "Harald Schulze"), false,
			Arrays.asList(new Identifier[] { new Identifier("hqdhoq") })));

		when(veranstalterRepository.findVeranstalter(suchanfrage)).thenReturn(veranstalter);

		// Act
		ResponsePayload result = service.aendereVeranstalter(uuidPrefix, zugangUnterlagen);

		// Assert
		MessagePayload messagePayload = result.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals("Veranstalter hatte den Newsletterempfang bereits deaktiviert.", messagePayload.getMessage());
		assertNull(result.getData());
		verify(veranstalterRepository, times(0)).changeVeranstalter(any());

	}

	@Test
	void should_aendereVeranstalterCallsPersist_when_trefferUndNewsletterAktiviert() {

		// Arrange
		String uuidPrefix = "80c8052f";
		ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.ERTEILT;
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage().withSuchkriterium(VeranstalterSuchkriterium.UUID)
			.withSuchstring(uuidPrefix);

		List<Veranstalter> veranstalter = new ArrayList<>();
		veranstalter.add(new Lehrer(new Person(uuidPrefix + "-guigsdiqg", "Harald Schulze"), true,
			Arrays.asList(new Identifier[] { new Identifier("hqdhoq") })));

		when(veranstalterRepository.findVeranstalter(suchanfrage)).thenReturn(veranstalter);

		// Act
		ResponsePayload result = service.aendereVeranstalter(uuidPrefix, zugangUnterlagen);

		// Assert
		MessagePayload messagePayload = result.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals("Newsletterempfang erfolgreich deaktiviert", messagePayload.getMessage());
		assertNull(result.getData());
		verify(veranstalterRepository, times(1)).changeVeranstalter(any());

	}
}
