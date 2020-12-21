// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.loesungszettel.ZulaessigeLoesungszetteleingabe;

/**
 * LoesungszettelZeileAPIModel
 */
public class LoesungszettelZeileAPIModel {

	@JsonProperty
	private int index;

	@JsonProperty
	private int anzahlColumns;

	@JsonProperty
	private ZulaessigeLoesungszetteleingabe eingabe;

}
