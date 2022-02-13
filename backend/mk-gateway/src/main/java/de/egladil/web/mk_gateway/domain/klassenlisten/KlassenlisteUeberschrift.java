// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;

/**
 * KlassenlisteUeberschrift ist die Überschriftenzeile einer hochgeladenen Klassenliste.
 */
public class KlassenlisteUeberschrift {

	private static final Logger LOGGER = LoggerFactory.getLogger(KlassenlisteUeberschrift.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	private final KlassenlisteFeldart[] feldarten;

	private final String zeileSemikolonsepariert;

	/**
	 *
	 */
	KlassenlisteUeberschrift() {

		feldarten = new KlassenlisteFeldart[0];
		zeileSemikolonsepariert = "";
	}

	/**
	 * @param  zeileSemikolonsepariert
	 *                                 String
	 * @throws UploadFormatException
	 *                                 wenn zeileKommasepariert null oder es nicht genau 4 Token sind.
	 */
	public KlassenlisteUeberschrift(final String zeileSemikolonsepariert) throws UploadFormatException {

		this.zeileSemikolonsepariert = zeileSemikolonsepariert;

		if (zeileSemikolonsepariert == null) {

			throw new MkGatewayRuntimeException("zeileSemikolonsepariert null");
		}

		String[] eingaben = StringUtils.splitPreserveAllTokens(zeileSemikolonsepariert, ';');

		if (StringUtils.isAllBlank(eingaben)) {

			LOGGER.error("ungueltige erste Zeile: -{}-", zeileSemikolonsepariert);
			throw new UploadFormatException(applicationMessages.getString("upload.klassenliste.leereSpaltenueberschriften"));
		}

		if (eingaben.length != 4) {

			LOGGER.error("ungueltige erste Zeile: -{}-", zeileSemikolonsepariert);
			// falscheAnzahlSpalten
			String msg = MessageFormat.format(applicationMessages.getString("upload.klassenliste.falscheAnzahlSpalten"),
				new Object[] { eingaben.length, zeileSemikolonsepariert });

			throw new UploadFormatException(msg);
		}

		int indexNachname = this.detectIndexNachname(eingaben);

		if (indexNachname < 0) {

			LOGGER.error("ungueltige erste Zeile: -{}-", zeileSemikolonsepariert);
			String msg = MessageFormat.format(applicationMessages.getString("upload.klassenliste.unerwarteteUeberschriften"),
				new Object[] { zeileSemikolonsepariert });
			throw new UploadFormatException(msg);
		}

		eingaben[indexNachname] = "Nachname";

		this.feldarten = new KlassenlisteFeldart[eingaben.length];
		int index = 0;
		int errors = 0;

		for (String eingabe : eingaben) {

			String trimed = eingabe.trim().toUpperCase();

			try {

				this.feldarten[index] = KlassenlisteFeldart.detectFromString(trimed);
			} catch (IllegalArgumentException e) {

				errors++;
				LOGGER.error(e.getMessage());
			}
			index++;
		}

		if (errors > 0) {

			LOGGER.error("ungueltige erste Zeile: -{}-", zeileSemikolonsepariert);
			String msg = MessageFormat.format(applicationMessages.getString("upload.klassenliste.unerwarteteUeberschriften"),
				new Object[] { zeileSemikolonsepariert });
			throw new UploadFormatException(msg);
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

	public String getZeileSemikolonsepariert() {

		return zeileSemikolonsepariert;
	}

}
