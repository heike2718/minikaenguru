// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.apimodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TeilnahmenuebersichAPIModel
 */
public class TeilnahmenuebersichAPIModel {

	@JsonProperty
	private int anzahlSchulanmeldungen = 0;

	@JsonProperty
	private int anzahlPrivatanmeldungen = 0;

	@JsonProperty
	private int anzahlLoesungszettelSchulen = 0;

	@JsonProperty
	private int anzahlLoesungszettelPrivat = 0;

}
