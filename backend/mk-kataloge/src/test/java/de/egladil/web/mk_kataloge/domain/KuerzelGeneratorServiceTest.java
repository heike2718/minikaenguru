// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_crypto.CryptoService;
import de.egladil.web.commons_crypto.impl.CryptoServiceImpl;
import de.egladil.web.mk_kataloge.domain.apimodel.KuerzelAPIModel;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Land;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Ort;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * KuerzelGeneratorServiceTest
 */
public class KuerzelGeneratorServiceTest {

	@Test
	void should_generateKuerzel() {

		// Arrange
		KatalogeRepository katalogeRepository = new KatalogeRepository() {

			@Override
			public List<Schule> loadSchulenInOrt(final String ortKuerzel) {

				return null;
			}

			@Override
			public List<Ort> loadOrteInLand(final String landKuerzel) {

				return null;
			}

			@Override
			public List<Land> loadLaender() {

				return null;
			}

			@Override
			public List<Schule> findSchulenWithKuerzeln(final List<String> schulkuerzel) {

				return null;
			}

			@Override
			public List<Schule> findSchulenInOrt(final String ortKuerzel, final String searchTerm) {

				return null;
			}

			@Override
			public List<Schule> findSchulen(final String searchTerm) {

				return null;
			}

			@Override
			public Optional<Schule> findSchuleWithKuerzel(final String schulkuerzel) {

				return null;
			}

			@Override
			public List<Ort> findOrteInLand(final String landKuerzel, final String searchTerm) {

				return null;
			}

			@Override
			public List<Ort> findOrte(final String searchTerm) {

				return null;
			}

			@Override
			public Optional<Ort> findOrtWithKuerzel(final String ortKuerzel) {

				return null;
			}

			@Override
			public List<Land> findLander(final String searchTerm) {

				return null;
			}

			@Override
			public Optional<Land> findLandWithKuerzel(final String landKuerzel) {

				return null;
			}

			@Override
			public int countSchulenMitKuerzel(final String kuerzel) {

				return 0;
			}

			@Override
			public int countSchulenInOrt(final String kuerzel) {

				return 0;
			}

			@Override
			public int countOrteMitKuerzel(final String kuerzel) {

				return 0;
			}

			@Override
			public int countOrteInLand(final String kuerzel) {

				return 0;
			}

			@Override
			public int countLaenderMitKuerzel(final String kuerzel) {

				return 0;
			}
		};

		CryptoService cryptoService = new CryptoServiceImpl();

		KuerzelGeneratorService kuerzelGeneratorService = KuerzelGeneratorService.createForTest(katalogeRepository, cryptoService);

		// Act
		KuerzelAPIModel kuerzel = kuerzelGeneratorService.generateKuerzel();

		// Assert
		assertNotNull(kuerzel.kuerzelSchule());
		assertNotNull(kuerzel.kuerzelOrt());

	}

}
