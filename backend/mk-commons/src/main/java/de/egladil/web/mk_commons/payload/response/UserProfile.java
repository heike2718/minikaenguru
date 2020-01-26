// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.payload.response;

/**
 * UserProfile sind die Daten, die nach dem Einloggen nicht noch einmal mit einem Extrarequest geholt werden sollen. Sie sind
 * minimal, damit die den localStorage des Browsers nicht überlasten.
 */
public class UserProfile {

	private boolean angemeldet;

	private int anzahlTeilnahmen;

	private SchulePayload schule;

	/**
	 *
	 */
	public UserProfile() {

	}

	public boolean isAngemeldet() {

		return angemeldet;
	}

	public void setAngemeldet(final boolean angemeldet) {

		this.angemeldet = angemeldet;
	}

	public int getAnzahlTeilnahmen() {

		return anzahlTeilnahmen;
	}

	public void setAnzahlTeilnahmen(final int anzahlTeilnahmen) {

		this.anzahlTeilnahmen = anzahlTeilnahmen;
	}

	public SchulePayload getSchule() {

		return schule;
	}

	public void setSchule(final SchulePayload schule) {

		this.schule = schule;
	}

}
