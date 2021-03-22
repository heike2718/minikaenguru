// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.admin;

import static org.junit.Assert.assertEquals;
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
 * VeranstalterZugangsstatusServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class VeranstalterZugangsstatusServiceTest {

	@Mock
	private VeranstalterRepository veranstalterRepository;

	@InjectMocks
	private VeranstalterZugangsstatusService service;

	@Test
	void should_zugangsstatusAendernReturnErrorResponsePayload_when_keinTrefferMitUuidFragment() {

		// Arrange
		String uuidPrefix = "80c8052f";
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage().withSuchkriterium(VeranstalterSuchkriterium.UUID)
			.withSuchstring(uuidPrefix);

		when(veranstalterRepository.findVeranstalter(suchanfrage)).thenReturn(new ArrayList<>());

		// Act
		ResponsePayload result = service.zugangsstatusAendern(uuidPrefix, ZugangUnterlagen.ERTEILT);

		// Assert
		MessagePayload messagePayload = result.getMessage();
		assertEquals("ERROR", messagePayload.getLevel());
		assertEquals("Veranstalter mit UUID like '80c8052f%' existiert nicht.", messagePayload.getMessage());
	}

	@Test
	void should_zugangsstatusAendernReturnErrorResponsePayload_when_mehrAlsEinTrefferMitUuidFragment() {

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
		ResponsePayload result = service.zugangsstatusAendern(uuidPrefix, ZugangUnterlagen.ERTEILT);

		// Assert
		MessagePayload messagePayload = result.getMessage();
		assertEquals("ERROR", messagePayload.getLevel());
		assertEquals("2 Veranstalter mit UUID like '80c8052f%' gefunden.", messagePayload.getMessage());

	}

	@Test
	void should_zugangsstatusAendernReturnInfoNotChangedResponsePayload_when_trefferUndZugangsstatusBereitsGesetzt() {

		// Arrange
		String uuidPrefix = "80c8052f";
		ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.ERTEILT;
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage().withSuchkriterium(VeranstalterSuchkriterium.UUID)
			.withSuchstring(uuidPrefix);

		List<Veranstalter> veranstalter = new ArrayList<>();
		veranstalter.add(new Lehrer(new Person(uuidPrefix + "-guigsdiqg", "Harald Schulze"), true,
			Arrays.asList(new Identifier[] { new Identifier("hqdhoq") })).withZugangUnterlagen(zugangUnterlagen));

		when(veranstalterRepository.findVeranstalter(suchanfrage)).thenReturn(veranstalter);

		// Act
		ResponsePayload result = service.zugangsstatusAendern(uuidPrefix, zugangUnterlagen);

		// Assert
		MessagePayload messagePayload = result.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals("Veranstalter hatte bereits den gewünschten Zugangsstatus Unterlagen.", messagePayload.getMessage());
		assertEquals(zugangUnterlagen, result.getData());

	}

	@Test
	void should_zugangsstatusAendernCallPersistAndReturnInfoWithNeuerZugangsstatusResponsePayload_when_trefferUndZugangsstatusNeuErteilt() {

		// Arrange
		String uuidPrefix = "80c8052f";
		ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.ERTEILT;
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage().withSuchkriterium(VeranstalterSuchkriterium.UUID)
			.withSuchstring(uuidPrefix);

		List<Veranstalter> veranstalter = new ArrayList<>();
		Veranstalter theVeranstalter = new Lehrer(new Person(uuidPrefix + "-guigsdiqg", "Harald Schulze"), true,
			Arrays.asList(new Identifier[] { new Identifier("hqdhoq") })).withZugangUnterlagen(ZugangUnterlagen.ENTZOGEN);
		veranstalter.add(theVeranstalter);

		when(veranstalterRepository.findVeranstalter(suchanfrage)).thenReturn(veranstalter);
		when(veranstalterRepository.changeVeranstalter(theVeranstalter)).thenReturn(Boolean.TRUE);

		// Act
		ResponsePayload result = service.zugangsstatusAendern(uuidPrefix, zugangUnterlagen);

		// Assert
		MessagePayload messagePayload = result.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals("Zugangsstatus Unterlagen erfolgreich geändert.", messagePayload.getMessage());
		assertEquals(zugangUnterlagen, result.getData());

		verify(veranstalterRepository, times(1)).changeVeranstalter(theVeranstalter);

	}

	@Test
	void should_zugangsstatusAendernCallPersistAndReturnInfoWithNeuerZugangsstatusResponsePayload_when_trefferUndZugangsstatusNeuDefault() {

		// Arrange
		String uuidPrefix = "80c8052f";
		ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.DEFAULT;
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage().withSuchkriterium(VeranstalterSuchkriterium.UUID)
			.withSuchstring(uuidPrefix);

		List<Veranstalter> veranstalter = new ArrayList<>();
		Veranstalter theVeranstalter = new Lehrer(new Person(uuidPrefix + "-guigsdiqg", "Harald Schulze"), true,
			Arrays.asList(new Identifier[] { new Identifier("hqdhoq") })).withZugangUnterlagen(ZugangUnterlagen.ENTZOGEN);
		veranstalter.add(theVeranstalter);

		when(veranstalterRepository.findVeranstalter(suchanfrage)).thenReturn(veranstalter);
		when(veranstalterRepository.changeVeranstalter(theVeranstalter)).thenReturn(Boolean.TRUE);

		// Act
		ResponsePayload result = service.zugangsstatusAendern(uuidPrefix, zugangUnterlagen);

		// Assert
		MessagePayload messagePayload = result.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals("Zugangsstatus Unterlagen erfolgreich geändert.", messagePayload.getMessage());
		assertEquals(zugangUnterlagen, result.getData());

		verify(veranstalterRepository, times(1)).changeVeranstalter(theVeranstalter);

	}

	@Test
	void should_zugangsstatusAendernCallPersistAndReturnInfoWithNeuerZugangsstatusResponsePayload_when_trefferUndZugangsstatusNeuEntzogen() {

		// Arrange
		String uuidPrefix = "80c8052f";
		ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.ENTZOGEN;
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage().withSuchkriterium(VeranstalterSuchkriterium.UUID)
			.withSuchstring(uuidPrefix);

		List<Veranstalter> veranstalter = new ArrayList<>();
		Veranstalter theVeranstalter = new Lehrer(new Person(uuidPrefix + "-guigsdiqg", "Harald Schulze"), true,
			Arrays.asList(new Identifier[] { new Identifier("hqdhoq") })).withZugangUnterlagen(ZugangUnterlagen.DEFAULT);
		veranstalter.add(theVeranstalter);

		when(veranstalterRepository.findVeranstalter(suchanfrage)).thenReturn(veranstalter);
		when(veranstalterRepository.changeVeranstalter(theVeranstalter)).thenReturn(Boolean.TRUE);

		// Act
		ResponsePayload result = service.zugangsstatusAendern(uuidPrefix, zugangUnterlagen);

		// Assert
		MessagePayload messagePayload = result.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals("Zugangsstatus Unterlagen erfolgreich geändert.", messagePayload.getMessage());
		assertEquals(zugangUnterlagen, result.getData());

		verify(veranstalterRepository, times(1)).changeVeranstalter(theVeranstalter);

	}

}
