// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.domain.enums;

/**
 * Rolle
 */
public enum Rolle {

	ADMIN {

		@Override
		public boolean kannSichZumWettbewerbAnmelden() {

			return false;
		}

	},
	LEHRER,
	PRIVAT;

	public boolean kannSichZumWettbewerbAnmelden() {

		return true;
	}
}
