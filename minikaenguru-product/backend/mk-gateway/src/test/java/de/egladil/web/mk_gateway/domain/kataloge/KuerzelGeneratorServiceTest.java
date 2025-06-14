// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kataloge;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.kataloge.api.KuerzelAPIModel;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.dao.KatalogeRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.entities.Land;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.entities.Ort;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.entities.Schule;
import io.quarkus.test.junit.QuarkusTest;

/**
 * KuerzelGeneratorServiceTest
 */
@QuarkusTest
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

		KuerzelGeneratorService kuerzelGeneratorService = KuerzelGeneratorService.createForTest(katalogeRepository);

		// Act
		KuerzelAPIModel kuerzel = kuerzelGeneratorService.generateKuerzel();

		// Assert
		assertNotNull(kuerzel.kuerzelSchule());
		assertNotNull(kuerzel.kuerzelOrt());

	}

}
