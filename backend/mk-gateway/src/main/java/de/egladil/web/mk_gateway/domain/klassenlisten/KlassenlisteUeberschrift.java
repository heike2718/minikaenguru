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

	private final KlassenlisteFeldart[] feldarten;

	/**
	 *
	 */
	KlassenlisteUeberschrift() {

		feldarten = new KlassenlisteFeldart[0];
	}

	/**
	 * @param  zeileSemikolonsepariert
	 *                                 String
	 * @throws UploadFormatException
	 *                                 wenn zeileKommasepariert null oder es nicht genau 4 Token sind.
	 */
	public KlassenlisteUeberschrift(final String zeileSemikolonsepariert) throws UploadFormatException {

		String[] eingaben = StringUtils.split(zeileSemikolonsepariert, ';');

		if (eingaben == null || eingaben.length != 4) {

			LOGGER.error("ungueltige erste Zeile: -{}-", zeileSemikolonsepariert);
			throw new UploadFormatException(KlassenlisteFeldart.getMessageExpectedContents());
		}

		int indexNachname = this.detectIndexNachname(eingaben);

		if (indexNachname < 0) {

			LOGGER.error("ungueltige erste Zeile: -{}-", zeileSemikolonsepariert);
			throw new UploadFormatException(KlassenlisteFeldart.getMessageExpectedContents());
		}

		eingaben[indexNachname] = "Nachname";

		this.feldarten = new KlassenlisteFeldart[eingaben.length];
		int index = 0;

		for (String eingabe : eingaben) {

			String trimed = eingabe.trim().toUpperCase();

			try {

				this.feldarten[index] = KlassenlisteFeldart.detectFromString(trimed);
			} catch (IllegalArgumentException e) {

				LOGGER.error(e.getMessage());
			}
			index++;
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

		for (int i = 0; i < this.feldarten.length; i++) {

			if (feldart == this.feldarten[i]) {

				return Optional.of(Integer.valueOf(i));
			}

		}

		return Optional.empty();
	}

	int detectIndexNachname(final String[] eingaben) {

		for (int i = 0; i < eingaben.length; i++) {

			if (eingaben[i] == null) {

				return -1;
			}

			String str = eingaben[i].toUpperCase();

			if (str.contains("NAME")) {

				if (!str.contains(KlassenlisteFeldart.VORNAME.getLabel().toUpperCase())) {

					return i;
				}
			}

		}

		return -1;
	}

}
