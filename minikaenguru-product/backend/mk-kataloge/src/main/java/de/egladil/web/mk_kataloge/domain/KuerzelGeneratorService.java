// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain;

import java.security.SecureRandom;

import de.egladil.web.mk_kataloge.domain.apimodel.KuerzelAPIModel;
import de.egladil.web.mk_kataloge.domain.error.KatalogAPIException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * KuerzelGeneratorService
 */
@ApplicationScoped
public class KuerzelGeneratorService {

	private static final String ALGORITHM = "SHA1PRNG";

	private static final int LAENGE = 8;

	private static final String CHARS = "ABCDEFGHJKLMNOPQRSTUVWXYZ0123456789";

	@Inject
	KatalogeRepository katalogRepository;

	public static KuerzelGeneratorService createForTest(final KatalogeRepository katalogeRepository) {

		KuerzelGeneratorService result = new KuerzelGeneratorService();
		result.katalogRepository = katalogeRepository;
		return result;
	}

	public KuerzelAPIModel generateKuerzel() {

		String schulkuerzel = generiereSchulkuerzel();
		String ortskuerzel = generiereOrtskuerzel();

		return KuerzelAPIModel.create(schulkuerzel, ortskuerzel);

	}

	private String generiereSchulkuerzel() {

		String kuerzel = generiereKuerzel();

		while (katalogRepository.countSchulenMitKuerzel(kuerzel) > 0) {

			kuerzel = generiereKuerzel();
		}

		return kuerzel;

	}

	private String generiereOrtskuerzel() {

		String kuerzel = generiereKuerzel();

		while (katalogRepository.countOrteMitKuerzel(kuerzel) > 0) {

			kuerzel = generiereKuerzel();
		}

		return kuerzel;

	}

	private String generiereKuerzel() {

		try {

			SecureRandom secureRandom = SecureRandom.getInstance(ALGORITHM);
			// nach ESAPI
			StringBuilder sb = new StringBuilder();

			for (int loop = 0; loop < LAENGE; loop++) {

				int index = secureRandom.nextInt(CHARS.toCharArray().length);
				sb.append(CHARS.toCharArray()[index]);
			}
			return sb.toString();
		} catch (final Exception e) {

			throw new KatalogAPIException("Fehler beim generieren eines Zufallsstrings: " + e.getMessage(), e);
		}
	}

}
