// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.commons_crypto.CryptoService;
import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Privatteilnahme;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbID;

/**
 * PrivatteilnahmeKuerzelServiceTest
 */
public class PrivatteilnahmeKuerzelServiceTest {

	private PrivatteilnahmeKuerzelService service;

	@Test
	void should_recalculateKuerzel_when_vorhanden() {

		// Arrange
		CryptoService crypoService = Mockito.mock(CryptoService.class);
		Mockito.when(crypoService.generateRandomString(PrivatteilnahmeKuerzelService.ALGORITHM,
			PrivatteilnahmeKuerzelService.LAENGE, PrivatteilnahmeKuerzelService.CHARS.toCharArray())).thenReturn("ABCDEFGHIJ");

		TeilnahmenRepository teilnahmeRepo = Mockito.mock(TeilnahmenRepository.class);
		List<Teilnahme> teilnahmen = new ArrayList<>();
		teilnahmen.add(new Privatteilnahme(new WettbewerbID(2017), new Identifier("ABCDEFGHIJ")));
		Mockito.when(teilnahmeRepo.ofTeilnahmenummer("ABCDEFGHIJ")).thenReturn(teilnahmen);

		service = PrivatteilnahmeKuerzelService.createForTest(crypoService, teilnahmeRepo).withTheUltimativeTestKuerzel();

		// Act
		String kuerzel = service.neuesKuerzel();

		// Assert
		assertEquals("A1B2C3E4F5", kuerzel);
		Mockito.verify(crypoService, Mockito.times(6)).generateRandomString(PrivatteilnahmeKuerzelService.ALGORITHM,
			PrivatteilnahmeKuerzelService.LAENGE, PrivatteilnahmeKuerzelService.CHARS.toCharArray());

	}

	@Test
	void should_terminate_when_vorhanden() {

		// Arrange
		CryptoService crypoService = Mockito.mock(CryptoService.class);
		Mockito.when(crypoService.generateRandomString(PrivatteilnahmeKuerzelService.ALGORITHM,
			PrivatteilnahmeKuerzelService.LAENGE, PrivatteilnahmeKuerzelService.CHARS.toCharArray())).thenReturn("ABCDEFGHIJ");

		TeilnahmenRepository teilnahmeRepo = Mockito.mock(TeilnahmenRepository.class);
		List<Teilnahme> teilnahmen = new ArrayList<>();
		teilnahmen.add(new Privatteilnahme(new WettbewerbID(2017), new Identifier("ABCDEFGHIJ")));
		Mockito.when(teilnahmeRepo.ofTeilnahmenummer("ABCDEFGHIJ")).thenReturn(teilnahmen);

		service = PrivatteilnahmeKuerzelService.createForTest(crypoService, teilnahmeRepo);

		// Act
		String kuerzel = service.neuesKuerzel();

		// Assert
		assertEquals(10, kuerzel.length());
		Mockito.verify(crypoService, Mockito.times(6)).generateRandomString(PrivatteilnahmeKuerzelService.ALGORITHM,
			PrivatteilnahmeKuerzelService.LAENGE, PrivatteilnahmeKuerzelService.CHARS.toCharArray());

		System.out.println(kuerzel);

	}
}
