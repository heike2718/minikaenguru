// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.enums;

import javax.ws.rs.NotFoundException;

/**
 * Dateiart sind die möglichen Dateiarten, die zur Verfügung gestellt werden.
 */
public enum Dateiart {

	UNTERLAGEN_KLASSE_1("klasse1", "_minikaenguru_klasse_1.zip"),
	UNTERLAGEN_KLASSE_2("klasse2", "_minikaenguru_klasse_2.zip");

	private final String queryParameter;

	private final String dateinameWithoutPrefix;

	/**
	 * Erzeugt eine Instanz von Downloadart
	 */
	private Dateiart(final String path, final String dateiname) {

		this.queryParameter = path;
		this.dateinameWithoutPrefix = dateiname;
	}

	/**
	 * Liefert die Membervariable path
	 *
	 * @return die Membervariable path
	 */
	public String getQueryParameter() {

		return queryParameter;
	}

	/**
	 * @param  wettbewerbsjahr
	 * @return                 String der Dateiname.
	 */
	public String getFileName(final String wettbewerbsjahr) throws IllegalArgumentException {

		if (wettbewerbsjahr == null) {

			throw new IllegalArgumentException("Parameter wettbewerbsjahr darf nicht null sein");
		}
		return wettbewerbsjahr + dateinameWithoutPrefix;
	}

	/**
	 * Dateiart anhand des queryParameters.
	 *
	 * @param  string
	 * @return        Dateiart
	 */
	public static Dateiart valueOfQueryParameter(final String string) throws NotFoundException {

		for (final Dateiart a : Dateiart.values()) {

			if (a.queryParameter.equals(string)) {

				return a;
			}
		}
		throw new NotFoundException(
			"keine Dateiart mit [queryParameter=]" + (string == null ? "null" : string + "] bekannt"));
	}
}
