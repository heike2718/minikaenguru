// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.egladil.web.commons_crypto.CryptoService;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.TeilnahmenRepository;

/**
 * PrivatteilnahmeKuerzelService
 */
@ApplicationScoped
public class PrivatteilnahmeKuerzelService {

	static final String ALGORITHM = "SHA1PRNG";

	static final int LAENGE = 10;

	static final String CHARS = "ABCDEFGHJKLMNOPQRSTUVWXYZ0123456789";

	private static final int MAX_RETRIES = 5;

	private String theUltimativeTestKuerzel;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	CryptoService cryptoService;

	public static PrivatteilnahmeKuerzelService createForTest(final CryptoService cryptoService, final TeilnahmenRepository teilnahmenRepository) {

		PrivatteilnahmeKuerzelService result = new PrivatteilnahmeKuerzelService();
		result.cryptoService = cryptoService;
		result.teilnahmenRepository = teilnahmenRepository;
		return result;
	}

	public PrivatteilnahmeKuerzelService withTheUltimativeTestKuerzel() {

		this.theUltimativeTestKuerzel = "A1B2C3E4F5";
		return this;
	}

	/**
	 * Generiert ein eindeutiges 10stelliges kuerzel.
	 *
	 * @return String
	 */
	public String neuesKuerzel() {

		String kuerzel = this.generiereKuerzel();
		int anzahlTeilnahmen = teilnahmenRepository.ofTeilnahmenummer(kuerzel).size();

		int retryCount = 0;

		while (anzahlTeilnahmen > 0 || retryCount >= MAX_RETRIES) {

			if (retryCount >= MAX_RETRIES) {

				kuerzel = theUltimativeTestKuerzel == null
					? UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10).toUpperCase()
					: theUltimativeTestKuerzel;
				break;
			}

			retryCount++;
			kuerzel = generiereKuerzel();
			anzahlTeilnahmen = teilnahmenRepository.ofTeilnahmenummer(kuerzel).size();
		}

		return kuerzel;

	}

	String generiereKuerzel() {

		return cryptoService.generateRandomString(ALGORITHM, LAENGE, CHARS.toCharArray());
	}

}
