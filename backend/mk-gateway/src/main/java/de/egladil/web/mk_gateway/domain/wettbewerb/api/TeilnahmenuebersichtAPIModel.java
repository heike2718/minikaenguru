// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TeilnahmenuebersichtAPIModel
 */
public class TeilnahmenuebersichtAPIModel {

	@JsonProperty
	private int anzahlSchulanmeldungen = 0;

	@JsonProperty
	private int anzahlPrivatanmeldungen = 0;

	@JsonProperty
	private int anzahlLoesungszettelSchulen = 0;

	@JsonProperty
	private int anzahlLoesungszettelPrivat = 0;

}
