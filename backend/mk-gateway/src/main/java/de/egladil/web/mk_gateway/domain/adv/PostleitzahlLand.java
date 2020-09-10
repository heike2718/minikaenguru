// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.adv;

import java.util.Optional;

import de.egladil.web.mk_gateway.domain.apimodel.veranstalter.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.semantik.ValueObject;

/**
 * PostleitzahlLand
 */
@ValueObject
public class PostleitzahlLand {

	/**
	 *
	 */
	private static final String DE = "DE";

	private String postleitzahl;

	private String land;

	public PostleitzahlLand(final String plz, final Optional<SchuleAPIModel> optSchule) {

		this.postleitzahl = extractPostleitzahl(plz);
		this.land = extractLand(plz, optSchule);

	}

	/**
	 * @param plz
	 * @param optSchule
	 */
	private String extractLand(final String plz, final Optional<SchuleAPIModel> optSchule) {

		if (optSchule.isPresent()) {

			String land = optSchule.get().kuerzelLand();

			if (land.startsWith(DE)) {

				return DE;
			}

			return land;
		}

		if (plz.contains("-")) {

			String[] tokens = plz.split("-");
			return tokens[0].trim().toUpperCase();
		}

		return DE;
	}

	/**
	 * @param plz
	 */
	private String extractPostleitzahl(final String plz) {

		String plzTrimed = plz.trim();

		try {

			Integer.valueOf(plzTrimed);
			return plzTrimed;
		} catch (NumberFormatException e) {

			if (plzTrimed.contains("-")) {

				String[] tokens = plz.split("-");
				return tokens[1].trim();
			}

			return plzTrimed;
		}
	}

	public String postleitzahl() {

		return postleitzahl;
	}

	public String landkuerzel() {

		return land;
	}
}
