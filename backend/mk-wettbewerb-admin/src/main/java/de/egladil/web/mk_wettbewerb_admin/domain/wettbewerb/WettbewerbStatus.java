// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb;

/**
 * WettbewerbStatus
 */
public enum WettbewerbStatus {

	ERFASST,
	ANMELDUNG,
	DOWNLOAD_LEHRER,
	DOWNLOAD_PRIVAT,
	BEENDET;

	public static WettbewerbStatus nextStatus(final WettbewerbStatus currentStatus) throws IllegalStateException, IllegalArgumentException {

		if (currentStatus == null) {

			return ERFASST;
		}

		switch (currentStatus) {

		case ERFASST:

			return ANMELDUNG;

		case ANMELDUNG:
			return DOWNLOAD_LEHRER;

		case DOWNLOAD_LEHRER:
			return DOWNLOAD_PRIVAT;

		case DOWNLOAD_PRIVAT:
			return BEENDET;

		case BEENDET:
			throw new IllegalStateException("Wettbewerb hat sein Lebensende erreicht. Es gibt keinen Folgestatus.");

		default:
			throw new IllegalArgumentException("unbekannter Status " + currentStatus);
		}
	}
}
