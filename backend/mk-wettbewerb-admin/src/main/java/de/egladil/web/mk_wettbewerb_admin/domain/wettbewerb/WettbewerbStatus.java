// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * WettbewerbStatus
 */
public enum WettbewerbStatus {

	ERFASST,
	ANMELDUNG,
	DOWNLOAD_PRIVAT,
	DOWNLOAD_LEHRER,
	BEENDET;

	public static String erlaubteStatus() {

		return StringUtils.join(Arrays.stream(WettbewerbStatus.values()).map(s -> s.toString()).collect(Collectors.toList()), ",");

	}
}
