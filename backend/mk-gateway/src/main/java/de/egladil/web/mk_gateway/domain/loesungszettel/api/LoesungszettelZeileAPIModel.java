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
	private int anzahlSpalten;

	@JsonProperty // A-1,..., C-5
	private String name;

	@JsonProperty
	private ZulaessigeLoesungszetteleingabe eingabe;

}
