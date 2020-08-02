// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.egladil.web.commons_crypto.CryptoService;
import de.egladil.web.mk_kataloge.domain.apimodel.KuerzelAPIModel;

/**
 * KuerzelGeneratorService
 */
@ApplicationScoped
public class KuerzelGeneratorService {

	private static final String ALGORITHM = "SHA-256";

	private static final int LAENGE = 8;

	private static final String CHARS = "ABCDEFGHJKLMNOPQRSTUVWXYZ0123456789";

	@Inject
	KatalogeRepository katalogRepository;

	@Inject
	CryptoService cryptoService;

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

		return cryptoService.generateRandomString(ALGORITHM, LAENGE, CHARS.toCharArray());
	}

}
