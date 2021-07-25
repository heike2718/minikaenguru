// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.user;

/**
 * Rolle
 */
public enum Rolle {

	ADMIN {

		@Override
		public boolean isAdmin() {

			return true;
		}

	},
	LEHRER,
	PRIVAT;

	public boolean isAdmin() {

		return false;
	}
}
