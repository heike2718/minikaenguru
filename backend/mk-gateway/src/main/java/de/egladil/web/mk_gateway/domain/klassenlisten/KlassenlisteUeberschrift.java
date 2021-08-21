// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.error.UploadFormatException;

/**
 * KlassenlisteUeberschrift ist die Überschriftenzeile einer hochgeladenen Klassenliste.
 */
public class KlassenlisteUeberschrift {

	private static final Logger LOGGER = LoggerFactory.getLogger(KlassenlisteUeberschrift.class);

	private final String[] eingaben;

	/**
	 * @param  zeileSemikolonsepariert
	 *                               String
	 * @throws UploadFormatException
	 *                               wenn zeileKommasepariert null oder es nicht genau 4 Token sind.
	 */
	public KlassenlisteUeberschrift(final String zeileSemikolonsepariert) throws UploadFormatException {

		eingaben = StringUtils.split(zeileSemikolonsepariert, ';');

		if (eingaben == null || eingaben.length != 4) {

			LOGGER.error("ungueltige erste Zeile: -{}-", zeileSemikolonsepariert);
			throw new UploadFormatException(KlassenlisteFeldart.getMessageExpectedContents());
		}

	}

	/**
	 * Gibt den Spaltenindex zur gegebenen Feldart zurück.
	 *
	 * @param  feldart
	 *                 KlassenlisteFeldart
	 * @return         Optional
	 */
	public Optional<Integer> getIndexFeldart(final KlassenlisteFeldart feldart) {

		for (int i = 0; i < eingaben.length; i++) {

			String name = eingaben[i];

			try {

				KlassenlisteFeldart result = KlassenlisteFeldart.valueOf(name.trim().toUpperCase());

				if (result == feldart) {

					return Optional.of(Integer.valueOf(i));
				}
			} catch (IllegalArgumentException e) {

				LOGGER.error(e.getMessage());

			}

		}

		return Optional.empty();
	}

}
