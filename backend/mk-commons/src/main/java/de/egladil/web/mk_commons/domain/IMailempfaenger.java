// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain;

/**
 * IMailempfaenger ist jemand mit einer Mailadresse
 */
public interface IMailempfaenger {

	/**
	 * Gibt die Mailadresse zurück.
	 *
	 * @return
	 */
	String getEmail();
}
