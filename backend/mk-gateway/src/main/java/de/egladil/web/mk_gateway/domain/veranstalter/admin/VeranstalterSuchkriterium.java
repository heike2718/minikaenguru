// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.admin;

/**
 * VeranstalterSuchkriterium
 */
public enum VeranstalterSuchkriterium {

	EMAIL,
	NAME,
	TEILNAHMENUMMER,
	UUID,
	ZUGANGSSTATUS {

		@Override
		public boolean isLikeQuery() {

			return false;
		}

	};

	public boolean isLikeQuery() {

		return true;
	}
}
